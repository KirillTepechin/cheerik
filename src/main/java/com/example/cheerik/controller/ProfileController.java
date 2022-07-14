package com.example.cheerik.controller;

import com.example.cheerik.dto.CommentDto;
import com.example.cheerik.dto.LikeDto;
import com.example.cheerik.dto.PostDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.Comment;
import com.example.cheerik.model.User;
import com.example.cheerik.service.CommentService;
import com.example.cheerik.service.LikeService;
import com.example.cheerik.service.PostService;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.MultipartConfigElement;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;


    @GetMapping
    public String getPosts(Model model, @AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int size) {
        var user = userService.findByLogin(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page -1,size, Sort.by("id").descending());
        final Page<PostDto> posts = postService.findAllByUser(pageable, user);
        final int totalPages = posts.getTotalPages();
        final List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .toList();
        var userLikes= user.getLikes().stream().map(like -> like.getPost().getId()).toList();
        var userComments= user.getComments().stream().map(Comment::getId).toList();
        model.addAttribute("likeDto", new LikeDto());
        model.addAttribute("userLikes", userLikes);
        model.addAttribute("userComments", userComments);
        model.addAttribute("pages", pageNumbers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("userDto", new UserDto(user));
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("posts", posts);
        return "profile";
    }
    @GetMapping("/create-post")
    public String createPost(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("postDto", new PostDto());
        return "create-post";
    }
    @PostMapping("/create-post")
    public String createPost(@AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute @Valid PostDto postDto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "create-post";
        }
        var user = userService.findByLogin(userDetails.getUsername());
        postDto.setUser(new UserDto(user));
        postService.createPost(postDto);

        return "redirect:/profile";
    }
    @GetMapping("/edit-post/{id}")
    public String editPost(@PathVariable() Long id,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        var authUser = userService.findByLogin(userDetails.getUsername());
        if(!authUser.getPosts().contains(postService.findPost(id))){
            throw new AccessDeniedException("Вы не являетесь автором поста");
        }
        model.addAttribute("postId", id);
        model.addAttribute("postDto", new PostDto(postService.findPost(id)));

        return "edit-post";
    }

    @PostMapping("edit-post/{id}")
    public String editPost(@PathVariable() Long id,
                           @ModelAttribute @Valid PostDto postDto,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "edit-post";
        }

        postService.updatePost(postDto);

        return "redirect:/profile";
    }
    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/profile";
    }
    @GetMapping("/edit-profile")
    public String getProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByLogin(userDetails.getUsername());
        model.addAttribute("userDto", new UserDto(user));

        return "edit-profile";
    }

    @PostMapping("/edit-profile")
    public String editProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String password,
            @RequestParam("file") MultipartFile file) throws IOException {
        var user = userService.findByLogin(userDetails.getUsername());
        userService.updateUser(user, password, file);
        return "redirect:/profile";
    }

    @GetMapping("subscribers")
    public String getSubscribersList(@AuthenticationPrincipal UserDetails userDetails,
                                  Model model,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "5") int size){

        var user = userService.findByLogin(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page-1,size,Sort.by("id").descending());

        final Page<UserDto> users = userService.findSubscribers(page, size, user.getId());
        final int totalPages = users.getTotalPages();
        final List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .toList();

        var userSubscriptions= user.getSubscriptions().stream().map(User::getId).toList();

        model.addAttribute("pages", pageNumbers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("users", users);
        model.addAttribute("userSubscriptions", userSubscriptions);

        return "subscribers";
    }

    @GetMapping("subscriptions")
    public String getSubscriptionsList(@AuthenticationPrincipal UserDetails userDetails,
                                  Model model,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "5") int size){

        var user = userService.findByLogin(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page-1,size,Sort.by("id").descending());

        final Page<UserDto> users = userService.findSubscriptions(page, size, user.getId());

        final int totalPages = users.getTotalPages();
        final List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .toList();

        model.addAttribute("pages", pageNumbers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("users", users);

        return "subscriptions";
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver = new org.springframework.web.multipart.commons.CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(2097152);
        return multipartResolver;
    }
}