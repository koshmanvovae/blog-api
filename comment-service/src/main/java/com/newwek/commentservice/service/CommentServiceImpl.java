package com.newwek.commentservice.service;

import com.newwek.commentservice.domain.Comment;
import com.newwek.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static java.lang.StringTemplate.STR;

/**
 * Provides implementation for {@link CommentService} interface with business logic to manage comments
 * on blog posts. This service uses {@link CommentRepository} for database operations and integrates
 * external service calls to manage related functionalities like comment counters.
 *
 * @see CommentService for service interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final RestTemplate restTemplate;

    /**
     * Retrieves all comments stored in the database.
     *
     * @return a list of {@link Comment} instances from the database.
     */
    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    /**
     * Finds a single comment by its unique identifier.
     *
     * @param id The ID of the comment to find.
     * @return the found {@link Comment}, or null if no comment exists with the provided ID.
     */
    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    /**
     * Saves a comment to the database and adjusts the comment count for the associated blog post.
     * If saving fails due to a data integrity issue, it attempts to revert the comment count and throws a status exception.
     *
     * @param comment The {@link Comment} to save.
     * @return the saved {@link Comment} instance.
     * @throws ResponseStatusException if the comment cannot be saved or the comment count adjustment fails.
     */
    @Override
    public Comment save(Comment comment) {
        Long postID = comment.getBlogPostId();
        decreaseBlogPostCommentsCounter(postID, HttpMethod.GET);

        try {
            return commentRepository.save(comment);
        } catch (DataIntegrityViolationException exception) {
            log.error("Error saving comment {}", comment, exception);
            log.info("Reverting blog post comments counter");
            decreaseBlogPostCommentsCounter(postID, HttpMethod.DELETE);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not save comment.");
        }
    }

    /**
     * Deletes a comment by its ID and updates the associated blog post's comment count.
     *
     * @param id The ID of the comment to delete.
     */
    @Override
    public void deleteById(Long id) {
        try {
            decreaseBlogPostCommentsCounter(id, HttpMethod.DELETE);
        } finally {
            commentRepository.deleteById(id);
        }
    }

    /**
     * Retrieves all comments associated with a specific blog post ID.
     *
     * @param blogPostId The blog post ID for which to find comments.
     * @return a list of {@link Comment} associated with the given blog post.
     */
    @Override
    public List<Comment> findCommentsByPostId(Long blogPostId) {
        return commentRepository.findAllByBlogPostId(blogPostId);
    }

    /**
     * Deletes all comments associated with a given blog post ID and updates the comment count accordingly.
     * This method is transactional to ensure all deletions complete successfully before updating the count.
     *
     * @param postId The blog post ID for which all comments should be deleted.
     */
    @Override
    @Transactional
    public void deleteAllForPostId(Long postId) {
        commentRepository.deleteAllByBlogPostId(postId);
    }

    /**
     * Helper method to decrease the comment count of a blog post by communicating with the blog service.
     * It handles HTTP responses to determine if the operation was successful or if there were errors.
     *
     * @param postID The ID of the blog post whose comment count is to be adjusted.
     * @param method The HTTP method to use for the request.
     * @throws ResponseStatusException If the blog post is not found or the service cannot process the request.
     */
    private void decreaseBlogPostCommentsCounter(Long postID, HttpMethod method) {
        ResponseEntity<Object> deleteResponse = restTemplate.exchange("http://BLOG-SERVICE/api/posts/update-comments-count/{postID}", method, null, Object.class);

        if (deleteResponse.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, STR."Blog post with id \{postID} not found.");
        }

        if(!deleteResponse.getStatusCode().is2xxSuccessful()) {
            String reason = "";

            if (deleteResponse.hasBody()){
                reason = Objects.requireNonNull(deleteResponse.getBody()).toString();
            }

            throw new ResponseStatusException(deleteResponse.getStatusCode(), reason);
        }
    }
}
