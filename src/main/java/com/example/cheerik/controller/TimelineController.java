package com.example.cheerik.controller;

import com.example.cheerik.dto.CommentDto;
import com.example.cheerik.dto.LikeDto;
import com.example.cheerik.dto.PostDto;
import com.example.cheerik.model.Comment;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/")
public class TimelineController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @GetMapping
    public String getTimeline(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        var user = userService.findByLogin(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page-1,size,Sort.by("id").descending());
        final Page<PostDto> posts = postService.findAllSubscriptionsPosts(pageable, user);

        model.addAttribute("posts", posts);
        final int totalPages = posts.getTotalPages();
        final List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .toList();
        var userLikes= user.getLikes().stream().map(like -> like.getPost().getId()).toList();

        var userComments= user.getComments().stream().map(Comment::getId).toList();
        model.addAttribute("userComments", userComments);
        model.addAttribute("pages", pageNumbers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("likeDto", new LikeDto());
        model.addAttribute("userLikes", userLikes);
        return "index";
    }

}
