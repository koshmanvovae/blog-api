package com.newwek.commentservice.service;

import com.newwek.commentservice.domain.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findAll();

    Comment findById(Long id);

    Comment save(Comment comment);

    void deleteById(Long id);
}
