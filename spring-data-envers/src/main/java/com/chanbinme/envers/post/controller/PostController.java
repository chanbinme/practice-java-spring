package com.chanbinme.envers.post.controller;

import com.chanbinme.envers.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public void createPost() {
        postService.createPost();
    }

    @PatchMapping
    public void updatePost() {
        postService.updatePost();
    }

    @DeleteMapping
    public void deletePost() {
        postService.deletePost();
    }
}
