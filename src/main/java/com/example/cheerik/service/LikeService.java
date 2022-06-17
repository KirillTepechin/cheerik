package com.example.cheerik.service;

import com.example.cheerik.dto.LikeDto;
import com.example.cheerik.model.Like;
import com.example.cheerik.model.Post;
import com.example.cheerik.model.User;
import com.example.cheerik.repository.LikeRepository;
import com.example.cheerik.repository.PostRepository;
import com.example.cheerik.repository.UserRepository;
import com.example.cheerik.util.error.LikeNotFoundException;
import com.example.cheerik.util.validation.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidatorUtil validatorUtil;

    @Transactional(readOnly = true)
    public List<Like> findAll() {
        return likeRepository.findAll();

    }
    @Transactional(readOnly = true)
    public Like findLike(LikeDto likeDto) {
        Example<Like> example = Example.of(new Like(likeDto.getUser(),likeDto.getPost()));
        return likeRepository.findOne(example).orElse(null);
    }

    @Transactional
    public Like like(LikeDto likeDto){
        final Like like = new Like(likeDto.getUser(), likeDto.getPost());
        var user = userRepository.findById(like.getUser().getId()).orElseThrow();
        var post = postRepository.findById(like.getPost().getId()).orElseThrow();
        user.getLikes().add(like);
        post.getLikes().add(like);

        likeRepository.save(like);
        userRepository.save(user);
        postRepository.save(post);
        return like;
    }

    @Transactional
    public Like unlike(LikeDto likeDto){
        final Like currentLike = findLike(likeDto);
        var user = userRepository.findById(currentLike.getUser().getId()).orElseThrow();
        var post = postRepository.findById(currentLike.getPost().getId()).orElseThrow();
        user.getLikes().remove(currentLike);
        post.getLikes().remove(currentLike);

        userRepository.save(user);
        postRepository.save(post);
        likeRepository.delete(currentLike);
        return currentLike;
    }
}
