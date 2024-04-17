package com.newwek.blogservice.services;

import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;

/**
 * Interface for managing comments related to blog posts.
 * Provides an abstraction for operations on comments, such as deleting all comments associated with a specific blog post.
 */
public interface PostCommentsService {

    /**
     * Deletes all comments associated with a given blog post.
     *
     * @param postId the ID of the blog post for which all comments should be deleted
     * @throws IllegalArgumentException if the postId is null
     * @throws DataAccessException if there is any issue in executing the operation in the underlying comment storage
     * @throws ServiceException if any other issue occurs during the process (e.g., network errors, configuration errors)
     */
    void deleteAllCommentForBlogPost(Long postId);

}

