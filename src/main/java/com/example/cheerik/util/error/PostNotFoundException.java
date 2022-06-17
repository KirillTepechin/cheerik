package com.example.cheerik.util.error;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super(String.format("Чирик с id [%s] не найден", id));
    }
}
