package com.example.cheerik.dto;

import com.example.cheerik.model.User;

public class ReportStatsDto {
    private String username;
    private int likes;
    private int posts;
    private int subscribers;
    private int subscriptions;

    public ReportStatsDto() {
    }

    public ReportStatsDto(User user) {
        this.username = user.getUsername();
        this.likes =  user.getLikes().size();
        this.posts = user.getPosts().size();
        this.subscribers = user.getSubscribers().size();
        this.subscriptions = user.getSubscriptions().size();
    }

    public ReportStatsDto(String username, int likes, int posts, int subscribers, int subscriptions) {
        this.username = username;
        this.likes = likes;
        this.posts =posts;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    public int getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(int subscriptions) {
        this.subscriptions = subscriptions;
    }
}
