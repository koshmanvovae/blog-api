package com.newwek.blogservice.services;

import com.newwek.blogservice.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * {@code PostCounterServiceImpl} is a Spring service that implements {@link PostCounterService} to
 * manage the comments counter of blog posts. It utilizes a {@code PostService} to interact with post data.
 */
@Service
@RequiredArgsConstructor
public class PostCounterServiceImpl implements PostCounterService {

    private final PostService postService; // A service dependency to interact with post data.

    /**
     * Increments the comment count of a post identified by {@code postId}.
     * This method retrieves the current post using its ID, updates the comments counter by adding one,
     * and then persists the changes.
     *
     * @param postId the ID of the post to update; must not be {@code null}.
     * @throws ResponseStatusException if no post with the given ID could be found.
     */
    @Override
    public void incrementPostCommentsCounter(Long postId) {
        Post currentPost = findPostById(postId);
        Long commentsCounter = currentPost.getCommentsCounter();
        currentPost.setCommentsCounter(commentsCounter + 1);
        postService.save(currentPost);
    }

    /**
     * Decrements the comment count of a post identified by {@code postId}.
     * This method retrieves the post by its ID, decrements the comments counter by one,
     * and then saves the updated post.
     *
     * @param postId the ID of the post to update; must not be {@code null}.
     * @throws ResponseStatusException if no post with the given ID could be found.
     */
    @Override
    public void decrementPostCommentsCounter(Long postId) {
        Post currentPost = findPostById(postId);
        Long commentsCounter = currentPost.getCommentsCounter();
        currentPost.setCommentsCounter(commentsCounter - 1);
        postService.save(currentPost);
    }

    /**
     * Retrieves a post by its ID using the {@code PostService}.
     *
     * @param postId the ID of the post to retrieve; must not be {@code null}.
     * @return the retrieved {@code Post} object.
     * @throws ResponseStatusException if the post cannot be found, encapsulating a {@code HttpStatus.NOT_FOUND}.
     */
    private Post findPostById(Long postId) {
        Post currentPost = postService.findById(postId);
        if (currentPost == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, STR."Blog post with id \{postId} could not be found");
        }
        return currentPost;
    }
}
