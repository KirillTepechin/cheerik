package com.example.cheerik.dto;

import com.example.cheerik.model.User;

import java.util.HashSet;
import java.util.Set;

public class UserDto {
    private final long id;
    private final String login;
    private final String password;
    private final Set<User> subscriptions;
    private final Set<User> subscribers;

    public UserDto(User user) {
        this.id = user.getId();
        this.login = user.getUsername();
        this.password = user.getPassword();
        this.subscriptions = user.getSubscriptions();
        this.subscribers = user.getSubscribers();
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Set<User> getSubscriptions() {
        return subscriptions;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }
}
