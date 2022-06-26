package com.example.cheerik.controller;

import com.example.cheerik.dto.CommentDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.Comment;
import com.example.cheerik.model.User;
import com.example.cheerik.service.CommentService;
import com.example.cheerik.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


import javax.validation.Valid;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/users")
public class UserListController {
    private final UserService userService;
    private final CommentService commentService;
    public UserListController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
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
            @PathVariable String login,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ) {
        var user = userService.findByLogin(login);
        var currentUser = userService.findByLogin(userDetails.getUsername());
        userService.subscribe(currentUser, user);

        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams().forEach(redirectAttributes::addAttribute);

        return "redirect:" + components.getPath();
    }

    @GetMapping("/unsubscribe/{login}")
    public String unsubscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String login,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ) {
        var user = userService.findByLogin(login);
        var currentUser = userService.findByLogin(userDetails.getUsername());
        userService.unsubscribe(currentUser, user);

        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams().forEach(redirectAttributes::addAttribute);

        return "redirect:" + components.getPath();
    }

}
