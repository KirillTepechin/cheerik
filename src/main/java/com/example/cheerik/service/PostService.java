package com.example.cheerik.service;

import com.example.cheerik.dto.PostDto;
import com.example.cheerik.model.Comment;
import com.example.cheerik.model.Post;
import com.example.cheerik.model.User;
import com.example.cheerik.repository.CommentRepository;
import com.example.cheerik.repository.LikeRepository;
import com.example.cheerik.repository.PostRepository;
import com.example.cheerik.repository.UserRepository;
import com.example.cheerik.util.error.CommentNotFoundException;
import com.example.cheerik.util.error.LikeNotFoundException;
import com.example.cheerik.util.error.PostNotFoundException;
import com.example.cheerik.util.validation.ValidatorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ValidatorUtil validatorUtil;

    public PostService(PostRepository postRepository,
                       CommentRepository commentRepository,
                       LikeRepository likeRepository,
                       UserRepository userRepository,
                       ValidatorUtil validatorUtil) {
        this.postRepository = postRepository;
        this.validatorUtil = validatorUtil;
        this.commentRepository=commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Post findPost(Long id) {
        final Optional<Post> post  = postRepository.findById(id);
        return post.orElseThrow(() -> new PostNotFoundException(id));
    }
    @Transactional(readOnly = true)
    public Page<PostDto> findAllSubscriptionsPosts(Pageable pageable, User user) {
        var userSubscriptionsPosts= user.getSubscriptions().stream().map(User::getPosts).toList().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList()).stream().map(Post::getId).toList();

        return postRepository.findByUserSubscriptions(pageable, userSubscriptionsPosts);
    }
    @Transactional(readOnly = true)
    public Page<PostDto> findAllByUser(Pageable pageable, User user) {

        return postRepository.findByUser(pageable, user.getId());
    }
    @Transactional
    public Post createPost(PostDto postDto){
        var user = userRepository.findById(postDto.getUser().getId()).orElseThrow(()->new UsernameNotFoundException("Пользователь не найден"));
        final Post post = new Post(postDto.getText(), new Date(), user);
        validatorUtil.validate(post);
        return postRepository.save(post);
    }
    @Transactional
    public Post updatePost(PostDto postDto){
        final Post currentPost = findPost(postDto.getId());
        currentPost.setText(postDto.getText());
        if(postDto.getUser()!=null) currentPost.setUser(userRepository.findById(postDto.getId()).orElseThrow(()->new UsernameNotFoundException("Пользоаветль не найден")));
        if(postDto.getComments()!=null) currentPost.setComments(postDto.getComments().stream()
                .map(com -> commentRepository.findById(com.getId())
                        .orElseThrow(()-> new CommentNotFoundException(com.getId()))).toList());
        if(postDto.getLikes()!=null) currentPost.setLikes(postDto.getLikes().stream()
                .map(like -> likeRepository.findById(like.getId())
                        .orElseThrow(()-> new LikeNotFoundException(like.getId()))).toList());
        validatorUtil.validate(currentPost);
        return postRepository.save(currentPost);
    }
    @Transactional
    public Post deletePost(Long id){
        final Post currentPost = findPost(id);
        postRepository.delete(currentPost);
        return currentPost;
    }

}
