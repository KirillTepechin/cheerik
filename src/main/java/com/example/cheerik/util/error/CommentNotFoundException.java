package com.example.cheerik.util.error;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(Long id) {
        super(String.format("Комментарий с id [%s] не найден", id));
    }
}
