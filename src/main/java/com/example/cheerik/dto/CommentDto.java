package com.example.cheerik.dto;

import com.example.cheerik.model.Comment;
import com.example.cheerik.model.Post;
import com.example.cheerik.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class CommentDto {
    private Long id;
    private User user;
    private Post post;
    @NotBlank(message = "Текст не может быть пустым!")
    private String text;
    private Date date;

    public CommentDto() {
    }

    public CommentDto(Comment comment){
        this.id=comment.getId();
        this.text=comment.getText();
        this.date=comment.getDate();
        this.user=comment.getUser();
        this.post= comment.getPost();
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
}
