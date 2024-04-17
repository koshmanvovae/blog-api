package com.newwek.blogservice.controllers;

import com.newwek.blogservice.services.PostCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing the comments count of blog posts.
 * This controller provides endpoints to increment and decrement the comments count for a specific blog post,
 * which helps in maintaining an accurate count of comments, especially after direct modifications like deletions
 * or bulk actions that may not automatically update the post's metadata.
 */
@RestController
@RequestMapping("/api/posts/update-comments-count")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Post Comments Management", description = "API endpoints for adjusting the comments count on blog posts.")
public class PostCommentsController {

    private final PostCounterService postService;

    /**
     * Increments the comment count for a specified post by ID.
     * This operation is typically triggered when a new comment is added to ensure the count reflects the current state.
     *
     * @param id The unique identifier of the blog post to update.
     * @return ResponseEntity indicating the operation's success.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Increment post comments count", description = "Increments the comment count for a blog post identified by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully incremented the comments count."),
            @ApiResponse(responseCode = "404", description = "Post not found. No post exists with the provided ID, so no increment performed.")
    })
    @Parameter(name = "id", description = "The unique ID of the blog post", required = true, schema = @Schema(type = "integer"))
    public ResponseEntity<Void> getPostById(@PathVariable Long id) {
        postService.incrementPostCommentsCounter(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Decrements the comment count for a specified post by ID.
     * This is necessary when a comment is removed to keep the count accurate and reflective of the actual comments present.
     *
     * @param id The unique identifier of the blog post whose comment count needs to be decremented.
     * @return ResponseEntity indicating the operation's success.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Decrement post comments count", description = "Decrements the comment count for a blog post identified by its ID. Useful for accurately reflecting the count after comments are deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully decremented the comments count."),
            @ApiResponse(responseCode = "404", description = "Post not found. No post exists with the provided ID, so no decrement performed.")
    })
    @Parameter(name = "id", description = "The unique ID of the blog post", required = true, schema = @Schema(type = "integer"))
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.decrementPostCommentsCounter(id);
        return ResponseEntity.ok().build();
    }
}
