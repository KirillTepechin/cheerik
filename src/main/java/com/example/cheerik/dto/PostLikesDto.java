package com.example.cheerik.dto;

import com.example.cheerik.model.Like;

public class PostLikesDto {
    private Long id;

    public PostLikesDto() {

    }

    public PostLikesDto(Like like) {
        this.id = like.getId();
    }

    public long getId() {
        return id;
    }
}
