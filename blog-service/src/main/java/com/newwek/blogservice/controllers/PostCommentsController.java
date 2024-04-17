package com.newwek.blogservice.controllers;

import com.newwek.blogservice.services.PostCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/posts/update-comments-count")
@RequiredArgsConstructor
@CrossOrigin
public class PostCommentsController {

    private final PostCounterService postService;

    @GetMapping("/{id}")
    public ResponseEntity<Void> getPostById(@PathVariable Long id) {
        postService.incrementPostCommentsCounter(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.decrementPostCommentsCounter(id);
        return ResponseEntity.ok().build();
    }
}
