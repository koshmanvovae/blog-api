package com.newwek.blogservice.controllers;

import com.newwek.blogservice.domain.Post;
import com.newwek.blogservice.repositories.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PostCommentsControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void testIncrementDecrementCommentCount() {
        Post saved = postRepository.save(new Post("Title", "Content", "Author"));
        Long postId = saved.getId();

        // Increment comments count
        ResponseEntity<Void> incrementResponse = restTemplate.getForEntity("/api/posts/update-comments-count/" + postId, Void.class);
        assertThat(incrementResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify comments count is increased by 1 in the database
        Post incrementedPost = verifyIncremented(postId, saved);

        // Decrement comments count
        ResponseEntity<Void> decrementResponse = restTemplate.exchange("/api/posts/update-comments-count/" + postId, HttpMethod.DELETE, null, Void.class);
        assertThat(decrementResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify comments count is decreased by 1 in the database
        verifyDecremented(postId, incrementedPost);
    }

    @Test
    public void testIncrementDecrementCommentCountOnAlreadyNotZeroCommentsPost() {
        Post saved = postRepository.save(new Post("Title", "Content", "Author"));
        saved.setCommentsCounter(1_000_000L);
        postRepository.save(saved);
        Long postId = saved.getId();

        // Increment comments count
        ResponseEntity<Void> incrementResponse = restTemplate.getForEntity("/api/posts/update-comments-count/" + postId, Void.class);
        assertThat(incrementResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify comments count is increased by 1 in the database
        Post incrementedPost = verifyIncremented(postId, saved);

        // Decrement comments count
        ResponseEntity<Void> decrementResponse = restTemplate.exchange("/api/posts/update-comments-count/" + postId, HttpMethod.DELETE, null, Void.class);
        assertThat(decrementResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify comments count is decreased by 1 in the database
        verifyDecremented(postId, incrementedPost);
    }

    @Test
    public void testIncrementDecrement100Times() {
        Post saved = postRepository.save(new Post("Title", "Content", "Author"));
        saved.setCommentsCounter(999L);
        postRepository.save(saved);
        Long postId = saved.getId();

        // Increment comments count
        for (int i = 0; i < 100; i++) {
            ResponseEntity<Void> incrementResponse = restTemplate.getForEntity("/api/posts/update-comments-count/" + postId, Void.class);
            assertThat(incrementResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        // Verify comments count is increased by 1 in the database
        verifyIncremented(postId, 1_099L);

        // Decrement comments count
        for (int i = 0; i < 100; i++) {
            ResponseEntity<Void> decrementResponse = restTemplate.exchange("/api/posts/update-comments-count/" + postId, HttpMethod.DELETE, null, Void.class);
            assertThat(decrementResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        // Verify comments count is decreased by 1 in the database
        verifyDecremented(postId, 999L);
    }

    private Post verifyIncremented(Long postId, Post post) {
        return verifyIncremented(postId, post.getCommentsCounter() + 1);
    }

    private Post verifyIncremented(Long postId, Long expectedCommentsCounter) {
        Post foundPost = postRepository.findById(postId).orElse(null);
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getCommentsCounter()).isEqualTo(expectedCommentsCounter);
        return foundPost;
    }

    private Post verifyDecremented(Long postId, Post post) {
        return verifyDecremented(postId,post.getCommentsCounter() - 1);
    }

    private Post verifyDecremented(Long postId, Long expectedCommentsCounter) {
        Post foundPost = postRepository.findById(postId).orElse(null);
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getCommentsCounter()).isEqualTo(expectedCommentsCounter);
        return foundPost;
    }

    @Test
    public void incrementCommentsCounter_NotFound() {
        Long nonExistentPostId = 999L; // This post does not exist

        // Attempt to increment comments counter
        ResponseEntity<Void> response = restTemplate.getForEntity("/api/posts/update-comments-count/" + nonExistentPostId, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void decrementCommentsCounter_NotFound() {
        Long nonExistentPostId = 999L; // This post does not exist

        // Attempt to decrement comments counter
        ResponseEntity<Void> response = restTemplate.exchange("/api/posts/update-comments-count/" + nonExistentPostId, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
