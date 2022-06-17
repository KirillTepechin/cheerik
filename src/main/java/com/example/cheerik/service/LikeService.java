package com.example.cheerik.service;

import com.example.cheerik.model.Like;
import com.example.cheerik.model.Post;
import com.example.cheerik.model.User;
import com.example.cheerik.repository.LikeRepository;
import com.example.cheerik.util.error.LikeNotFoundException;
import com.example.cheerik.util.validation.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private ValidatorUtil validatorUtil;

    @Transactional(readOnly = true)
    public Like findLike(Long id) {
        final Optional<Like> employee  = likeRepository.findById(id);
        return employee.orElseThrow(() -> new LikeNotFoundException(id));
    }

    @Transactional
    public Like like(User user, Post post){
        final Like like = new Like(user, post);
        validatorUtil.validate(post);
        return likeRepository.save(like);
    }

    @Transactional
    public Like unlike(Long id){
        final Like currentLike = findLike(id);
        likeRepository.delete(currentLike);
        return currentLike;
    }
}
