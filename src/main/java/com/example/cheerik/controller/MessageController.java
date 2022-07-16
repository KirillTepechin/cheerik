package com.example.cheerik.controller;

import com.example.cheerik.dto.MessageDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.service.MessageService;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

        var chats = messageService.findChats(currentUser);

        var sortedChats = chats.entrySet().stream()
                .sorted(Map.Entry.<UserDto, MessageDto>comparingByValue(
                        Comparator.comparing(MessageDto::getId)).reversed()).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        model.addAttribute("user", currentUser);
        model.addAttribute("chats", sortedChats);

        return "chats";
    }

    @GetMapping("/chat/{to}")
    public String getMessages(@AuthenticationPrincipal UserDetails userDetails,@PathVariable String to,
                           Model model) {

        var currentUser = userService.findByLogin(userDetails.getUsername());
        var user = userService.findByLogin(to);
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
