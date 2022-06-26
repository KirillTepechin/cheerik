package com.example.cheerik.controller;

import com.example.cheerik.dto.CommentDto;
import com.example.cheerik.service.CommentService;
import com.example.cheerik.service.LikeService;
import com.example.cheerik.service.PostService;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.nio.charset.Charset;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @PostMapping("/add-comment/{id}")
    public String addComment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute @Valid CommentDto commentDto,
                             BindingResult bindingResult, Model model,
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(required = false) String referer){
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "index";
        }
        var user = userService.findByLogin(userDetails.getUsername());
        commentDto.setPost(postService.findPost(id));
        commentDto.setUser(user);
        commentService.createComment(commentDto);

        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams().forEach(redirectAttributes::addAttribute);

        return "redirect:" + components.getPath();
    }

    @PostMapping("/delete-comment/{id}")
    public String deleteComment(@PathVariable Long id,
                                RedirectAttributes redirectAttributes,
                                @RequestHeader(required = false) String referer) {
        commentService.deleteComment(id);

        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams().forEach(redirectAttributes::addAttribute);

        return "redirect:" + components.getPath();
    }
    @GetMapping("/edit-comment/{id}")
    public String editComment(@PathVariable() Long id,
                              Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {

        var authUser = userService.findByLogin(userDetails.getUsername());
        if(!authUser.getComments().contains(commentService.findComment(id))){
            throw new AccessDeniedException("Вы не являетесь автором комментария");
        }
        model.addAttribute("commentId", id);
        model.addAttribute("commentDto", new CommentDto(commentService.findComment(id)));

        return "edit-comment";
    }
    @PostMapping("/edit-comment/{id}")
    public String editComment(@PathVariable() Long id,
                              @ModelAttribute @Valid CommentDto commentDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @RequestHeader(required = false) String referer) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "edit-comment";
        }

        commentService.updateComment(commentDto);

        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams().forEach(redirectAttributes::addAttribute);

        return "redirect:" + components.getPath()+"?updated=" + commentDto.getText();
    }
}
