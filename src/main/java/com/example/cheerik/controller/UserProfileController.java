package com.example.cheerik.controller;

import com.example.cheerik.dto.*;
import com.example.cheerik.model.Comment;
import com.example.cheerik.model.User;
import com.example.cheerik.service.CommentService;
import com.example.cheerik.service.LikeService;
import com.example.cheerik.service.PostService;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/profile")
public class UserProfileController {
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
    public String createPost(@AuthenticationPrincipal UserDetails userDetails,  @ModelAttribute @Valid PostDto postDto,
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
                           Model model) {
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
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String password) {
        var user = userService.findByLogin(userDetails.getUsername());
        userService.updateUser(user, password);
        return "redirect:/profile";
    }
    @PostMapping("/add-comment/{id}")
    public String addComment(@PathVariable Long id,@AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute @Valid CommentDto commentDto,
                             BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "profile";
        }
        var user = userService.findByLogin(userDetails.getUsername());
        commentDto.setPost(postService.findPost(id));
        commentDto.setUser(user);
        commentService.createComment(commentDto);

        return "redirect:/profile";
    }
    @GetMapping("/like/{id}")
    public String like(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute @Valid LikeDto likeDto,
            BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "profile";
        }
        var user = userService.findByLogin(userDetails.getUsername());
        likeDto.setPost(postService.findPost(id));
        likeDto.setUser(user);

        if(likeService.findLike(likeDto)!=null){
            likeService.unlike(likeDto);
        }else{
            likeService.like(likeDto);
        }
        return "redirect:/profile";
    }
    @PostMapping("/delete-comment/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return "redirect:/profile";
    }
    @GetMapping("/edit-comment/{id}")
    public String editComment(@PathVariable() Long id,
                           Model model) {
        model.addAttribute("commentId", id);
        model.addAttribute("commentDto", new CommentDto(commentService.findComment(id)));

        return "edit-comment";
    }
    @PostMapping("edit-comment/{id}")
    public String editComment(@PathVariable() Long id,
                           @ModelAttribute @Valid CommentDto commentDto,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "edit-comment";
        }

        commentService.updateComment(commentDto);

        return "redirect:/profile";
    }
}