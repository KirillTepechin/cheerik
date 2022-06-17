package com.example.cheerik.util.error;

public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(Long id) {
        super(String.format("Лайк с id [%s] не найден", id));
    }
}
