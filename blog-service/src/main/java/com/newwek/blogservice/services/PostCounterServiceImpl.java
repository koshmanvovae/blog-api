package com.newwek.blogservice.services;

import com.newwek.blogservice.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostCounterServiceImpl implements PostCounterService {

    private final PostService postService;

    @Override
    public void incrementPostCommentsCounter(Long postId) {
        Post currentPost = findPostById(postId);

        Long commentsCounter = currentPost.commentsCounter();

        currentPost.commentsCounter(commentsCounter + 1);

        postService.save(currentPost);
    }

    @Override
    public void decrementPostCommentsCounter(Long postId) {
        Post currentPost = findPostById(postId);

        Long commentsCounter = currentPost.commentsCounter();

        currentPost.commentsCounter(commentsCounter - 1);

        postService.save(currentPost);
    }


    private Post findPostById(Long postId) {
        Post currentPost = postService.findById(postId);
        if(currentPost == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, STR."Blog post with id \{postId} could not be found");
        }
        return currentPost;
    }
}
