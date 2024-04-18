package com.newwek.commentservice.controller;

import com.newwek.commentservice.domain.Comment;
import com.newwek.commentservice.domain.dto.CreateCommentDto;
import com.newwek.commentservice.domain.dto.UpdateCommentDto;
import org.junit.jupiter.api.BeforeEach;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RestTemplate restTemplateMock;

    @BeforeEach
    public void init() {
        ResponseEntity<Object> result = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplateMock.exchange(
                anyString(),
                any(HttpMethod.class),
                eq(null),
                ArgumentMatchers.<Class<Object>>any())
        ).thenReturn(result);
    }

    @Test
    public void testCommentLifecycle() {
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

    @Test
    public void testCreateComment_InvalidData() {
        CreateCommentDto invalidComment = new CreateCommentDto(0L, ""); // Assuming validation for non-empty username and content and valid postId

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Username", "testuser");
        ResponseEntity<Comment> response = restTemplate.exchange("/api/comments", HttpMethod.POST, new HttpEntity<>(invalidComment, headers), Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testUpdateComment_NotFound() {
        UpdateCommentDto updateComment = new UpdateCommentDto("Updated content");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Username", "testuser");
        HttpEntity<UpdateCommentDto> updateEntity = new HttpEntity<>(updateComment, headers);
        ResponseEntity<Comment> response = restTemplate.exchange("/api/comments/999", HttpMethod.PUT, updateEntity, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateComment_Forbidden_UsernameMismatch() {
        // Create a comment first
        CreateCommentDto createComment = new CreateCommentDto(123L, "This is a test comment");
        HttpHeaders headersCreate = new HttpHeaders();
        headersCreate.setContentType(MediaType.APPLICATION_JSON);
        headersCreate.set("X-Username", "user1");
        ResponseEntity<Comment> createResponse = restTemplate.exchange("/api/comments", HttpMethod.POST, new HttpEntity<>(createComment, headersCreate), Comment.class);
        Comment createdComment = createResponse.getBody();

        // Try to update with a different username
        UpdateCommentDto updateComment = new UpdateCommentDto("Updated content");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Username", "user2");  // Assuming "user2" is not allowed to update "user1"'s comment
        HttpEntity<UpdateCommentDto> updateEntity = new HttpEntity<>(updateComment, headers);
        ResponseEntity<Comment> updateResponse = restTemplate.exchange(STR."/api/comments/\{createdComment.getId()}", HttpMethod.PUT, updateEntity, Comment.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void testDeleteComment_NonExistent() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/comments/1000", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testCommentPermissions() {
        // Create a comment
        CreateCommentDto createComment = new CreateCommentDto(123L, "This is a test comment");
        HttpHeaders createHeaders = new HttpHeaders();
        createHeaders.setContentType(MediaType.APPLICATION_JSON);
        createHeaders.set("X-Username", "user1");
        ResponseEntity<Comment> createResponse = restTemplate.exchange("/api/comments", HttpMethod.POST, new HttpEntity<>(createComment, createHeaders), Comment.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Comment createdComment = createResponse.getBody();

        // Attempt to delete with incorrect user
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Username", "wrongUser");
        HttpEntity<?> deleteEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(STR."/api/comments/\{createdComment.getId()}", HttpMethod.DELETE, deleteEntity, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void createComment_MaxLengthFields() {
        String maxContent = new String(new char[255]).replace("\0", "a");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Username", "userWithMaxAllowedUsername");
        CreateCommentDto comment = new CreateCommentDto(123L, maxContent);
        HttpEntity<CreateCommentDto> requestEntity = new HttpEntity<>(comment, headers);

        ResponseEntity<Comment> response = restTemplate.postForEntity("/api/comments", requestEntity, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getContent()).isEqualTo(maxContent);
        assertThat(response.getBody().getUsername()).isEqualTo("userWithMaxAllowedUsername");
    }

    @Test
    public void createComment_InvalidContent_EmptyContent() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Username", "validUser");
        CreateCommentDto comment = new CreateCommentDto(123L, "");
        HttpEntity<CreateCommentDto> requestEntity = new HttpEntity<>(comment, headers);

        ResponseEntity<Comment> response = restTemplate.postForEntity("/api/comments", requestEntity, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteComment_AtBoundaryCondition() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Username", "user1");
        CreateCommentDto createComment = new CreateCommentDto(123L, "Comment for deletion test");
        HttpEntity<CreateCommentDto> createRequest = new HttpEntity<>(createComment, headers);
        ResponseEntity<Comment> createdResponse = restTemplate.postForEntity("/api/comments", createRequest, Comment.class);
        Comment createdComment = createdResponse.getBody();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(STR."/api/comments/\{createdComment.getId()}", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void testCommentPermissions_AtBoundaryUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Username", "boundaryUser");
        CreateCommentDto createComment = new CreateCommentDto(123L, "Comment at user boundary");
        HttpEntity<CreateCommentDto> requestEntity = new HttpEntity<>(createComment, headers);

        ResponseEntity<Comment> response = restTemplate.postForEntity("/api/comments", requestEntity, Comment.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        headers.add("X-Username", "outsideBoundaryUser");
        ResponseEntity<Comment> failedResponse = restTemplate.postForEntity("/api/comments", requestEntity, Comment.class);
        assertThat(failedResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
