package com.newwek.blogservice.config;

import com.newwek.blogservice.services.PostCommentsService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class DeletePostAspect {

    private final PostCommentsService postCommentsService;

    @After("execution(* com.newwek.blogservice.services.PostService.deleteById(Long)) && args(postId)")
    public void afterPostDeleted(Long postId) {
        postCommentsService.deleteAllCommentForBlogPost(postId);
    }
}
