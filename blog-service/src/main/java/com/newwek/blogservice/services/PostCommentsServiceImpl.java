package com.newwek.blogservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostCommentsServiceImpl implements PostCommentsService {

    private final RestTemplate restTemplate;


    @Override
    public void deleteAllCommentForBlogPost(Long postId) {
        ResponseEntity<Object> deleteResponse = restTemplate.exchange(STR."http://COMMENTS-SERVICE/api/comments/post/\{postId}", HttpMethod.DELETE, null, Object.class);

        checkResponseForIssues(deleteResponse);
    }

    private static void checkResponseForIssues(ResponseEntity<Object> deleteResponse) {
        if (!deleteResponse.getStatusCode().is2xxSuccessful()) {
            String reason = "";

            if(deleteResponse.hasBody()){
                reason = Objects.requireNonNull(deleteResponse.getBody()).toString();
            }

            throw new ResponseStatusException(deleteResponse.getStatusCode(), reason);
        }
    }

}
