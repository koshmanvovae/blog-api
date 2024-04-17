package com.newwek.blogservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

/**
 * Service implementation for managing comments on blog posts using a RESTful web service.
 * Implements {@link PostCommentsService} to provide actual execution logic for deleting comments.
 */
@Service
@RequiredArgsConstructor
public class PostCommentsServiceImpl implements PostCommentsService {

    private final RestTemplate restTemplate;  // Spring's tool for RESTful communication, injected via constructor

    /**
     * Deletes all comments associated with a specific blog post by making a DELETE request to a remote service.
     * The method constructs the URL by inserting the blog post ID into a predefined template.
     *
     * @param postId the ID of the blog post for which comments should be deleted
     * @throws IllegalArgumentException if the postId is null
     * @throws ResponseStatusException if the response from the server indicates that the request was unsuccessful
     */
    @Override
    public void deleteAllCommentForBlogPost(Long postId) {
        String url = STR."http://COMMENT-SERVICE/api/comments/post/\{postId}"; // URL for the DELETE operation
        ResponseEntity<Object> deleteResponse = restTemplate.exchange(url, HttpMethod.DELETE, null, Object.class);

        checkResponseForIssues(deleteResponse);
    }

    /**
     * Checks the response from the RESTful web service to ensure that the operation was successful.
     * Throws an exception if the response status code is not in the 2xx range indicating a successful operation.
     *
     * @param deleteResponse the response entity obtained from the RESTful web service call
     * @throws ResponseStatusException if the response status code is not successful (2xx)
     */
    public void checkResponseForIssues(ResponseEntity<Object> deleteResponse) {
        if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
            String reason = STR."Unexpected response status: \{deleteResponse.getStatusCode()}";

            if (deleteResponse.hasBody()) {
                reason = Objects.requireNonNull(deleteResponse.getBody()).toString();
            }

            throw new ResponseStatusException(deleteResponse.getStatusCode(), reason);
        }
    }
}

