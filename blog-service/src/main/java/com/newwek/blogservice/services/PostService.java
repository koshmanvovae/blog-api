package com.newwek.blogservice.services;

import com.newwek.blogservice.domain.Post;

import java.util.List;

public interface PostService {
    List<Post> findAll();

    List<Post> findAllSortedByCommentCountDesc();

    Post findById(Long id);

    Post save(Post post);

    void deleteById(Long id);
}
