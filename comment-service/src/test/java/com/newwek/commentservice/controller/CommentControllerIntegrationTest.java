package com.newwek.commentservice.controller;

import com.newwek.commentservice.domain.Comment;
import com.newwek.commentservice.domain.dto.CreateCommentDto;
import com.newwek.commentservice.domain.dto.UpdateCommentDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext
public class CommentControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RestTemplate restTemplateMock;

    @Test
    public void testCommentLifecycle() {
        ResponseEntity<Object> result = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplateMock.exchange(
                anyString(),
                any(HttpMethod.class),
                eq(null),
                ArgumentMatchers.<Class<Object>>any())
        ).thenReturn(result);


        // Create Comment
        CreateCommentDto createComment = new CreateCommentDto(123L, "This is a test comment");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("X-Username", "testuser");
        ResponseEntity<Comment> createResponse = restTemplate.exchange("/api/comments", HttpMethod.POST, new HttpEntity<>(createComment, params), Comment.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Comment createdComment = createResponse.getBody();
        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getContent()).isEqualTo(createComment.content());
        assertThat(createdComment.getBlogPostId()).isEqualTo(createComment.blogPostId());

        // Get Comment by ID
        ResponseEntity<Comment> getResponse = restTemplate.getForEntity(STR."/api/comments/\{createdComment.getId()}", Comment.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(createdComment.getId());

        // Update Comment
        UpdateCommentDto updateComment = new UpdateCommentDto("Updated content");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Username", "testuser");
        HttpEntity<UpdateCommentDto> updateEntity = new HttpEntity<>(updateComment, headers);
        ResponseEntity<Comment> updateResponse = restTemplate.exchange(STR."/api/comments/\{createdComment.getId()}", HttpMethod.PUT, updateEntity, Comment.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().getContent()).isEqualTo("Updated content");

        // Delete Comment
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(STR."/api/comments/\{createdComment.getId()}", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify Deletion
        ResponseEntity<Comment> deletedGetResponse = restTemplate.getForEntity(STR."/api/comments/\{createdComment.getId()}", Comment.class);
        assertThat(deletedGetResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
