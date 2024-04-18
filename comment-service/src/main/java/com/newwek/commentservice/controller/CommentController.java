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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Comment Management", description = "API endpoints for managing comments related to blog posts.")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Get all comments", description = "Retrieves a list of all comments from the database.")
    public List<Comment> getComments() {
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a comment by ID", description = "Retrieves a specific comment by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found the comment", content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a comment", description = "Creates a new comment for a blog post, using the provided content and the username extracted from the request header.")
    @ApiResponse(responseCode = "201", description = "Comment created successfully", content = @Content(schema = @Schema(implementation = Comment.class)))
    public Comment createComment(@RequestHeader("X-Username") @NotNull @NotBlank String username, @RequestBody @Validated CreateCommentDto comment) {
        Comment createdComment = new Comment(comment.blogPostId(), username, comment.content());
        return commentService.save(createdComment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a comment", description = "Updates an existing comment's content, if the requesting user is the original author and the modification window has not expired.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated successfully", content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Either you are not the author or the update window has expired"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Comment> updateComment(@RequestHeader("X-Username") @NotNull @NotBlank String username, @PathVariable Long id, @RequestBody UpdateCommentDto comment) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ResponseEntity.notFound().build();
        }
        if (!username.equals(existingComment.getUsername()) || LocalDateTime.now().isAfter(existingComment.getEnableToUpdateTill())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this comment or the update period has expired.");
        }
        existingComment.setContent(comment.content());
        return ResponseEntity.ok(commentService.save(existingComment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment", description = "Deletes a specific comment by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        if (commentService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/post/{postId}")
    @Operation(summary = "Delete comments by post ID", description = "Deletes all comments associated with a specific post ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All comments for the post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No comments found for the post ID")
    })
    public ResponseEntity<Void> deleteCommentsForPostId(@PathVariable @NotNull Long postId) {
        List<Comment> commentsByPostId = commentService.findCommentsByPostId(postId);
        if (commentsByPostId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteAllForPostId(postId);
        return ResponseEntity.noContent().build();
    }
}
