package com.newwek.blogservice.services;

public interface PostCounterService {

    void incrementPostCommentsCounter(Long postId);

    void decrementPostCommentsCounter(Long postId);

}
