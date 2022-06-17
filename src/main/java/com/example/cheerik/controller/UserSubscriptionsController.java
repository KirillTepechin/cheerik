package com.example.cheerik.controller;

import com.example.cheerik.dto.CommentDto;
import com.example.cheerik.dto.LikeDto;
import com.example.cheerik.dto.PostDto;
import com.example.cheerik.dto.UserDto;
import com.example.cheerik.model.Like;
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
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/")
public class UserSubscriptionsController {
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
        Pageable pageable = PageRequest.of(page-1,size);
        final Page<PostDto> posts = postService.findAllSubscriptionsPosts(pageable, user);

        model.addAttribute("posts", posts);
        final int totalPages = posts.getTotalPages();
        final List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .toList();
        var userLikes= user.getLikes().stream().map(like -> like.getPost().getId()).toList();

        model.addAttribute("pages", pageNumbers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("likeDto", new LikeDto());
        model.addAttribute("userLikes", userLikes);
        return "index";
    }
    @PostMapping("/add-comment/{id}")
    public String addComment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute @Valid CommentDto commentDto,
                             BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "index";
        }
        var user = userService.findByLogin(userDetails.getUsername());
        commentDto.setPost(postService.findPost(id));
        commentDto.setUser(user);
        commentService.createComment(commentDto);

        return "redirect:/index";
    }
    @GetMapping("/like/{id}")
    public String like(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute @Valid LikeDto likeDto,
            BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "index";
        }
        var user = userService.findByLogin(userDetails.getUsername());
        likeDto.setPost(postService.findPost(id));
        likeDto.setUser(user);

        if(likeService.findLike(likeDto)!=null){
            likeService.unlike(likeDto);
        }else{
            likeService.like(likeDto);
        }
        return "redirect:/";
    }
}
