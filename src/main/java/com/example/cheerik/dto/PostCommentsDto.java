package com.example.cheerik.dto;

import com.example.cheerik.model.Comment;
import com.example.cheerik.model.User;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class PostCommentsDto {
    private Long id;
    @NotBlank(message = "Текст не может быть пустым!")
    private String text;
    private Date date;
    private User user;
    public PostCommentsDto() {

    }

    public PostCommentsDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.date = comment.getDate();
        this.user = comment.getUser();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
