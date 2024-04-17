package com.newwek.blogservice.config;

import com.newwek.blogservice.services.PostCommentsService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for handling actions to be performed after a blog post is deleted.
 * This aspect is linked to the deletion operation on a blog post, specifically
 * designed to perform clean-up tasks related to the post after it has been deleted,
 * such as deleting all comments associated with that post.
 *
 * Annotations:
 * @Aspect - Marks this class as an aspect. Part of AOP that defines the class
 *           as containing advice that will be applied at certain join points
 *           defined by pointcut expressions.
 * @Component - Marks this class as a Spring-managed bean, allowing it to be
 *              automatically detected through classpath scanning.
 * @RequiredArgsConstructor - Lombok annotation that generates a constructor with
 *                            required arguments (those marked as 'final'). This
 *                            constructor is used for dependency injection.
 *
 * It is crucial to have this aspect properly configured within an application context
 * that supports AOP, and the PostService.deleteByID(Long) method must be executed
 * within a Spring-managed environment to trigger the advice contained in this aspect.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class DeletePostAspect {

    // Injected service that handles operations related to post comments.
    private final PostCommentsService postCommentsService;

    /**
     * After-advice that triggers once the PostService.deleteById method successfully
     * completes execution. This advice ensures that all comments associated with the
     * deleted post are also removed, maintaining data integrity and cleanliness.
     *
     * The pointcut expression targets the deleteById method of PostService, filtering
     * only executions where a Long type postId is used as an argument.
     *
     * @param postId the ID of the post that was deleted, used to identify associated
     *               comments that need to be removed.
     * @throws IllegalArgumentException if postId is null or invalid, though typically,
     *         such checks would be handled within the PostService or the PostCommentsService.
     */
    @After("execution(* com.newwek.blogservice.services.PostService.deleteById(Long)) && args(postId)")
    public void afterPostDeleted(Long postId) {
        postCommentsService.deleteAllCommentForBlogPost(postId);
    }
}
