package com.newwek.commentservice.repository;

import com.newwek.commentservice.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBlogPostId(Long blogPostId);

    void deleteAllByBlogPostId(Long blogPostId);

}
