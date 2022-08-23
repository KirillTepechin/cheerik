package com.example.cheerik.controller;

import com.example.cheerik.dto.MessageDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.service.MessageService;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MessageController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @GetMapping("/chats")
    public String getChats(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        var currentUser = userService.findByLogin(userDetails.getUsername());

        var sortedChats = messageService.findChats(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("chats", sortedChats);

        return "chats";
    }

    @GetMapping("/chat/{to}")
    public String getMessages(@AuthenticationPrincipal UserDetails userDetails,@PathVariable String to,
                           Model model) throws ChangeSetPersister.NotFoundException {

        var currentUser = userService.findByLogin(userDetails.getUsername());
        var user = userService.findByLogin(to);
        if(user == null){
            throw new ChangeSetPersister.NotFoundException();
        }
        var messages = messageService.findChatMessages(currentUser, user);

        model.addAttribute("messages", messages);
        model.addAttribute("to", to);
        model.addAttribute("from", currentUser.getUsername());
        return "chat";
    }

    @MessageMapping("/chat/{to}")
    public void sendMessage(@Payload MessageDto messageDto) throws Exception {
        messageService.createMessage(messageDto);
    }

}
