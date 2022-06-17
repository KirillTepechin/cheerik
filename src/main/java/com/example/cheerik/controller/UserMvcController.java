package com.example.cheerik.controller;

import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.User;
import com.example.cheerik.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/users")
public class UserMvcController {
    private final UserService userService;

    public UserMvcController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUsers(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int size,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        final Page<UserDto> users = userService.findAllPages(page, size)
                .map(UserDto::new);
        model.addAttribute("users", users);
        final int totalPages = users.getTotalPages();
        final List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .toList();

        var currentUser = userService.findByLogin(userDetails.getUsername());
        var userSubscriptions= currentUser.getSubscriptions().stream().map(User::getId).toList();
        model.addAttribute("userSubscriptions", userSubscriptions);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("pages", pageNumbers);
        model.addAttribute("totalPages", totalPages);
        return "users";
    }

    @GetMapping("/subscribe/{login}")
    public String subscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String login
    ) {
        var user = userService.findByLogin(login);
        var currentUser = userService.findByLogin(userDetails.getUsername());
        userService.subscribe(currentUser, user);

        return "redirect:/users/";
    }

    @GetMapping("/unsubscribe/{login}")
    public String unsubscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String login
    ) {
        var user = userService.findByLogin(login);
        var currentUser = userService.findByLogin(userDetails.getUsername());
        userService.unsubscribe(currentUser, user);

        return "redirect:/users/";
    }
}