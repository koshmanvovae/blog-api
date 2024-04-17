package com.newwek.commentservice.service;

import com.newwek.commentservice.domain.Comment;
import com.newwek.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import static java.lang.StringTemplate.STR;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final RestTemplate restTemplate;

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public Comment save(Comment comment) {
        Long postID = comment.blogPostId();
        decreaseBlogPostCommentsCounter(postID, HttpMethod.GET);

        Comment savedComment;
        try {
            savedComment = commentRepository.save(comment);
        }catch (Exception exception){
            log.error("Error saving comment {}", comment, exception);
            log.info("Revert blog post comments counter");

            decreaseBlogPostCommentsCounter(postID, HttpMethod.DELETE);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, STR."Could not save comment \{comment}");
        }

        return savedComment;
    }

    @Override
    public void deleteById(Long id) {
        try {
            decreaseBlogPostCommentsCounter(id, HttpMethod.DELETE);
        } finally {
            commentRepository.deleteById(id);
        }
    }

    private void decreaseBlogPostCommentsCounter(Long postID, HttpMethod delete) {
        ResponseEntity<Object> deleteResponse = restTemplate.exchange(STR."http://BLOG-SERVICE/api/posts/update-comments-count/\{postID}", delete, null, Object.class);

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
