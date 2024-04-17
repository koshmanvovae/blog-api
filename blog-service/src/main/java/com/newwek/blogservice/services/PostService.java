package com.newwek.blogservice.services;

import com.newwek.blogservice.domain.Post;

import java.util.List;

/**
 * The {@code PostService} interface provides methods for managing posts in a blog or similar system.
 * It includes operations to retrieve, save, and delete posts, as well as to retrieve posts in a sorted order.
 */
public interface PostService {

    /**
     * Retrieves all posts available in the repository.
     *
     * @return a list of {@link Post} objects; if no posts are available, an empty list is returned.
     */
    List<Post> findAll();

    /**
     * Retrieves all posts sorted by the number of comments in descending order.
     * This is useful for displaying the most discussed posts first.
     *
     * @return a list of {@link Post} objects sorted by comment count in descending order;
     *         if no posts are available, an empty list is returned.
     */
    List<Post> findAllSortedByCommentCountDesc();

    /**
     * Retrieves a post by its unique ID.
     *
     * @param id the ID of the post to retrieve. Must not be null.
     * @return the {@link Post} with the specified ID, or {@code null} if no such post exists.
     */
    Post findById(Long id);

    /**
     * Saves a given post to the repository. If the post does not exist, it will be created.
     * If the post exists (as determined by its ID), it will be updated.
     *
     * @param post the {@link Post} to save. Must not be null.
     * @return the saved {@link Post}, with any generated ID (if created) and other changes that occurred during save.
     */
    Post save(Post post);

    /**
     * Deletes a post by its ID.
     *
     * @param id the ID of the post to delete. Must not be null.
     */
    void deleteById(Long id);
}

