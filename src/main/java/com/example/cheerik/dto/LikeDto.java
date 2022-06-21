package com.example.cheerik.dto;

import com.example.cheerik.model.Like;
import com.example.cheerik.model.Post;
import com.example.cheerik.model.User;

import javax.persistence.*;

public class LikeDto {

    private Long id;
    private User user;
    private Post post;
    private boolean liked;
    private String test;
    public LikeDto() {
    }

    public LikeDto(Like like) {
        this.id= like.getId();
        this.post = like.getPost();
        this.user =like.getUser();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
