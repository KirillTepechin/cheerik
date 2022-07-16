package com.example.cheerik.service;

import com.example.cheerik.dto.MessageDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.Message;
import com.example.cheerik.model.User;
import com.example.cheerik.repository.MessageRepository;
import com.example.cheerik.util.validation.ValidatorUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


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

    public MessageDto findLastMessage(User currentUser, User chatUser) {
        var chatMessages = findChatMessages(currentUser, chatUser);
        return chatMessages.get(chatMessages.size()-1);
    }

    public HashMap<UserDto, MessageDto> findChats(User currentUser) {
        List<UserDto> chatUsers = userService.findChatsByUser(currentUser);

        List<MessageDto> messageDtoList = new ArrayList<>();
        for (var chatUser:
                chatUsers) {
            messageDtoList.add(findLastMessage(currentUser, userService.findByLogin(chatUser.getLogin())));
        }
        HashMap<UserDto, MessageDto> chats = new HashMap<>();
        for (int i = 0; i < messageDtoList.size(); i++) {
            chats.put(chatUsers.get(i), messageDtoList.get(i));
        }
        //сортировка
        return chats.entrySet().stream()
                .sorted(Map.Entry.<UserDto, MessageDto>comparingByValue(
                        Comparator.comparing(MessageDto::getId)).reversed()).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
