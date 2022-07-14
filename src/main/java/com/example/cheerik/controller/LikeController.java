package com.example.cheerik.controller;

import com.example.cheerik.dto.LikeDto;
import com.example.cheerik.service.CommentService;
import com.example.cheerik.service.LikeService;
import com.example.cheerik.service.PostService;
import com.example.cheerik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.configurationprocessor.json.*;

@RestController
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

    @PostMapping
    public ResponseEntity<String> like( @AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody String body) throws JSONException {

        JSONObject request = new JSONObject(body);
        String id = request.getString("id");

        var user = userService.findByLogin(userDetails.getUsername());
        var likeDto = new LikeDto();
        likeDto.setPost(postService.findPost(Long.parseLong(id)));
        likeDto.setUser(user);

        JSONObject response = new JSONObject();
        if(likeService.findLike(likeDto)!=null){
            likeService.unlike(likeDto);
            response.put("color","grey");
        }else{
            likeService.like(likeDto);
            response.put("color","red");
        }
        int count = postService.findPost(Long.parseLong(id)).getLikes().size();
        response.put("count",count);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(response.toString(),
                headers, HttpStatus.OK);
    }

}
