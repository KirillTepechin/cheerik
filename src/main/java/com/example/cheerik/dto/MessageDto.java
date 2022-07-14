package com.example.cheerik.dto;

import com.example.cheerik.model.Message;
import com.example.cheerik.model.User;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
public class MessageDto {
    private Long id;
    private String from;
    private String to;
    private String text;
    private Date date;
    private String userFilename;

    public MessageDto() {
    }

    public MessageDto(Message message) {
        this.id = message.getId();
        this.from = message.getFrom().getUsername();
        this.to = message.getTo().getUsername();
        this.text = message.getText();
        this.date = message.getDate();
        this.userFilename = message.getFrom().getFilename();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String getUserFilename(){
        return userFilename;
    }

    public void setUserFilename(String userFilename) {
        this.userFilename = userFilename;
    }
}
