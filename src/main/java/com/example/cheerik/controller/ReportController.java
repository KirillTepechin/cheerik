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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @GetMapping("/report-stats")
    public String getReportByUser(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        var user = userService.findByLogin(userDetails.getUsername());
        final List<ReportStatsDto> reports = userService.findReport();

        model.addAttribute("user", user);
        model.addAttribute("reports", reports);

        return "/report-stats";
    }


}
