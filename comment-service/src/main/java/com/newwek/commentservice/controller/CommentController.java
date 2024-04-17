package com.newwek.commentservice.controller;

import com.newwek.commentservice.domain.Comment;
import com.newwek.commentservice.domain.dto.CreateCommentDto;
import com.newwek.commentservice.domain.dto.UpdateCommentDto;
import com.newwek.commentservice.service.CommentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping( "/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<Comment> getComments() {
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Comment createComment(@RequestHeader("X-Username") @NotNull @NotBlank String username, @RequestBody @Validated CreateCommentDto comment) {
        Comment createdComment = new Comment(comment.blogPostId(), username, comment.content());
        return commentService.save(createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@RequestHeader("X-Username") @NotNull @NotBlank String username, @PathVariable Long id, @RequestBody UpdateCommentDto comment) {

        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ResponseEntity.notFound().build();
        }

        if(!username.equals(existingComment.username())){
            return ResponseEntity.status(FORBIDDEN).build();
        }

        if(LocalDateTime.now().isAfter(existingComment.enableToUpdateTill())){
            return ResponseEntity.status(FORBIDDEN).build();
        }

        existingComment.content(comment.content());
        Comment savedPost = commentService.save(existingComment);
        return ResponseEntity.ok(savedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        if (commentService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
