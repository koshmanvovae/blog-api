package com.newwek.blogservice.services;

/**
 * The {@code PostCounterService} interface defines the operations for updating the comments counter
 * for a post in a blog-like system. It provides methods to increment or decrement the count of comments
 * on a specific post.
 */
public interface PostCounterService {

    /**
     * Increments the comment count for the specified post. This method should be used
     * when a new comment is added to a post.
     *
     * @param postId the unique identifier of the post whose comment count is to be incremented.
     *               Must not be {@code null}.
     * @throws IllegalArgumentException if {@code postId} is null.
     */
    void incrementPostCommentsCounter(Long postId);

    /**
     * Decrements the comment count for the specified post. This method should be used
     * when a comment is deleted from a post.
     *
     * @param postId the unique identifier of the post whose comment count is to be decremented.
     *               Must not be {@code null}.
     * @throws IllegalArgumentException if {@code postId} is null.
     */
    void decrementPostCommentsCounter(Long postId);
}

