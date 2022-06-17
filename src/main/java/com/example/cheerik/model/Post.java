package com.example.cheerik.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Текст не может быть пустым!")
    private String text;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany
    @JoinColumn(name = "post_id")
    private List<Like> likes;
    @OneToMany
    @JoinColumn(name = "post_id")
    private List<Comment> comments;

    public Post(@NotBlank(message = "Текст не может быть пустым!") String text, Date date, User user) {
        this.text = text;
        this.date = date;
        this.user = user;
    }

    public Post() {
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

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
