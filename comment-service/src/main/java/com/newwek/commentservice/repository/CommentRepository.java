package com.newwek.commentservice.repository;

import com.newwek.commentservice.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for handling persistence operations for {@link Comment} entities.
 * This repository provides standard CRUD operations and includes custom methods to handle specific
 * queries related to the 'comments' related to a particular blog post.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Retrieves all comments associated with a given blog post ID.
     * @param blogPostId the ID of the blog post
     * @return a list of {@link Comment} objects linked to the specified blog post ID
     */
    List<Comment> findAllByBlogPostId(Long blogPostId);

    /**
     * Deletes all comments that are associated with a specific blog post ID.
     * This operation is typically used when a blog post is deleted to ensure data integrity.
     * @param blogPostId the ID of the blog post for which comments need to be deleted
     */
    void deleteAllByBlogPostId(Long blogPostId);
}

