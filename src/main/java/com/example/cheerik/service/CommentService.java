package com.example.cheerik.service;

import com.example.cheerik.dto.CommentDto;
import com.example.cheerik.dto.PostDto;
import com.example.cheerik.model.Comment;
import com.example.cheerik.model.Post;
import com.example.cheerik.repository.CommentRepository;
import com.example.cheerik.repository.LikeRepository;
import com.example.cheerik.util.error.CommentNotFoundException;
import com.example.cheerik.util.error.LikeNotFoundException;
import com.example.cheerik.util.error.PostNotFoundException;
import com.example.cheerik.util.validation.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ValidatorUtil validatorUtil;

    @Transactional(readOnly = true)
    public Comment findComment(Long id) {
        final Optional<Comment> comment  = commentRepository.findById(id);
        return comment.orElseThrow(() -> new CommentNotFoundException(id));
    }
    @Transactional
    public Comment createComment(CommentDto comment){
        final Comment post = new Comment(comment.getUser(),comment.getPost(),comment.getText(), new Date());
        validatorUtil.validate(post);
        return commentRepository.save(post);
    }
    @Transactional
    public Comment updateComment(CommentDto comment){
        final Comment currentComment = findComment(comment.getId());
        if(comment.getText()!=null)currentComment.setText(comment.getText());
        if(comment.getUser()!=null) currentComment.setUser(comment.getUser());
        if(comment.getUser()!=null) currentComment.setPost(comment.getPost());
        validatorUtil.validate(currentComment);
        return commentRepository.save(currentComment);
    }
    @Transactional
    public Comment deleteComment(Long id){
        final Comment currentComment = findComment(id);
        commentRepository.delete(currentComment);
        return currentComment;
    }
}
