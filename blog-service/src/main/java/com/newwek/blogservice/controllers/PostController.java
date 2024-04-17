package com.newwek.blogservice.controllers;

import com.newwek.blogservice.domain.Post;
import com.newwek.blogservice.domain.dto.PostDto;
import com.newwek.blogservice.domain.dto.CreatePostDto;
import com.newwek.blogservice.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin
@Tag(name = "Blog Post Management",
        description = "Central API controller for managing blog posts. " +
                      "This controller facilitates various operations, including the creation, retrieval, updating, and deletion of blog posts." +
                      " It serves as the primary interface for interaction with the blog's post repository," +
                      " providing tools for both content creators and consumers to manage and engage with blog content effectively.")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Retrieve all posts",
            description = "This endpoint retrieves a list of all blog posts available in the system, sorted by the number of comments each post has received in descending order. " +
                          "This provides a quick overview of the most discussed posts at the top of the list.")
    public List<PostDto> getAllPosts() {
        return PostDto.of(postService.findAllSortedByCommentCountDesc());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a post by ID",
            description = "Fetches a specific blog post identified by its unique ID. " +
                          "This endpoint is crucial for accessing detailed information about a post, including its content, author, and engagement metrics. " +
                          "Useful for detailed views where one might display the full content and comments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = PostDto.class))),
            @ApiResponse(responseCode = "404", description = "Post not found, indicating no post exists with the provided ID")
    })
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        Post post = postService.findById(id);
        return post != null ? ResponseEntity.ok(new PostDto(post)) : ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new post",
            description = "Creates a new post with the provided title, content, and author details. " +
                          "This endpoint is responsible for post creation within the blog service, ensuring that all new posts meet validation criteria before being added to the database.")
    @ApiResponse(responseCode = "201", description = "Post created successfully", content = @Content(schema = @Schema(implementation = PostDto.class)))
    public PostDto createPost(@RequestBody @Validated CreatePostDto post) {
        Post newPost = new Post(post.title(), post.content(), post.author());
        return new PostDto(postService.save(newPost));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a post",
            description = "Updates an existing post, identified by its ID, with new details provided by the user. " +
                          "This endpoint allows for the modification of post attributes such as title, content, and author. " +
                          "It is key for maintaining the relevance and accuracy of the post information over time.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post updated successfully", content = @Content(schema = @Schema(implementation = PostDto.class))),
            @ApiResponse(responseCode = "404", description = "Post not found, no update performed due to invalid post ID")
    })
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody @Validated CreatePostDto postDetails) {
        Post post = postService.findById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        Post updatedPost = new Post(
                post.getId(),
                postDetails.title(),
                postDetails.content(),
                postDetails.author(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.getCommentsCounter());

        Post savedPost = postService.save(updatedPost);
        return ResponseEntity.ok(new PostDto(savedPost));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post",
            description = "Permanently removes a post from the blog based on its ID. " +
                          "This action is irreversible and should be used with caution. " +
                          "This endpoint facilitates content moderation and management by allowing the removal of outdated or inappropriate posts.")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (postService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        postService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

