package com.example.cheerik.dto;

import com.example.cheerik.model.Comment;
import com.example.cheerik.model.Like;
import com.example.cheerik.model.Post;
import com.example.cheerik.model.User;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

public class PostDto {
    private Long id;
    @NotBlank(message = "Текст не может быть пустым!")
    private String text;
    private Date date;
    private UserDto user;
    private List<PostLikesDto> likes;
    private List<PostCommentsDto> comments;

    public PostDto() {

    }

    public PostDto(Post post){
        this.id = post.getId();
        this.text=post.getText();
        this.date=post.getDate();
        if(post.getUser()!=null){
            this.user=new UserDto(post.getUser());
        }
        this.likes = post.getLikes().stream()
                .map(PostLikesDto::new)
                .toList();
        this.comments = post.getComments().stream()
                .map(PostCommentsDto::new)
                .toList();
    }

    public PostDto(Post post, int id){
        this.id = post.getId();
        this.text=post.getText();
        this.date=post.getDate();
        if(post.getUser()!=null){
            this.user=new UserDto(post.getUser());
        }
        this.likes = post.getLikes().stream()
                .map(PostLikesDto::new)
                .toList();
        this.comments = post.getComments().stream()
                .map(PostCommentsDto::new)
                .toList();
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<PostLikesDto> getLikes() {
        return likes;
    }

    public void setLikes(List<PostLikesDto> likes) {
        this.likes = likes;
    }

    public List<PostCommentsDto> getComments() {
        return comments;
    }

    public void setComments(List<PostCommentsDto> comments) {
        this.comments = comments;
    }
}
