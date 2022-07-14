package com.example.cheerik.service;

import com.example.cheerik.dto.MessageDto;
import com.example.cheerik.model.Message;
import com.example.cheerik.model.User;
import com.example.cheerik.repository.MessageRepository;
import com.example.cheerik.util.validation.ValidatorUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidatorUtil validatorUtil;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Transactional
    public Message createMessage(MessageDto messageDto) throws JSONException, JsonProcessingException {
        ArrayList<String> tmp = new ArrayList<>(List.of(messageDto.getTo(), messageDto.getFrom()));
        Collections.sort(tmp);
        String users = String.join("-", tmp);
        messageDto.setUserFilename(userService.findByLogin(messageDto.getFrom()).getFilename());

        simpMessagingTemplate.convertAndSend("/topic/"+users, messageDto);
        final Message message = new Message(userService.findByLogin(messageDto.getFrom()),
                userService.findByLogin(messageDto.getTo()), messageDto.getText());
        validatorUtil.validate(message);
        return messageRepository.save(message);
    }
    @Transactional
    public List<MessageDto> findChatMessages(User from, User to){
        return messageRepository.findChatMessages(from, to).stream().map(MessageDto::new).toList();
    }
}
