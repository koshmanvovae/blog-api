package com.newwek.commentservice.service;

import com.newwek.commentservice.domain.Comment;

import java.util.List;

/**
 * Service interface for managing comments. This interface abstracts the logic required to
 * access comment data, thereby decoupling it from the specific data access mechanisms.
 */
public interface CommentService {

    /**
     * Retrieves all comments in the system.
     * @return a list of all comments
     */
    List<Comment> findAll();

    /**
     * Finds a specific comment by its ID.
     * @param id the ID of the comment to find
     * @return the found {@link Comment}, or null if no comment with the given ID exists
     */
    Comment findById(Long id);

    /**
     * Saves a comment either by creating a new one or updating an existing one.
     * @param comment the {@link Comment} to save
     * @return the saved comment
     */
    Comment save(Comment comment);

    /**
     * Deletes a comment by its ID.
     * @param id the ID of the comment to delete
     */
    void deleteById(Long id);

    /**
     * Finds all comments related to a specific blog post ID.
     * @param id the blog post ID for which comments are to be found
     * @return a list of comments associated with the blog post
     */
    List<Comment> findCommentsByPostId(Long id);

    /**
     * Deletes all comments associated with a specific blog post ID.
     * @param postId the ID of the blog post for which all comments should be deleted
     */
    void deleteAllForPostId(Long postId);
}
