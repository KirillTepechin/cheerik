package com.example.cheerik.controller;

import com.example.cheerik.dto.LikeDto;
import com.example.cheerik.service.CommentService;
import com.example.cheerik.service.LikeService;
import com.example.cheerik.service.PostService;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
@Controller
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/{id}")
    public String like(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute @Valid LikeDto likeDto,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
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
        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();

        components.getQueryParams().forEach(redirectAttributes::addAttribute);

        return "redirect:" + components.getPath();
    }
}
