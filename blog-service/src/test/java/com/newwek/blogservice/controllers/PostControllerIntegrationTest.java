    package com.newwek.blogservice.controllers;

    import com.newwek.blogservice.domain.Post;
    import org.apache.commons.lang3.RandomStringUtils;
    import org.junit.jupiter.api.BeforeAll;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.web.client.TestRestTemplate;
    import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.test.annotation.DirtiesContext;
    import org.springframework.test.annotation.Rollback;
    import org.springframework.transaction.annotation.Transactional;
    import org.testcontainers.containers.MySQLContainer;
    import org.testcontainers.junit.jupiter.Container;
    import org.testcontainers.junit.jupiter.Testcontainers;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.Map;

    import static org.assertj.core.api.Assertions.*;
    import static org.springframework.http.HttpMethod.*;

    @Testcontainers
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @Transactional
    @DirtiesContext
    public class PostControllerIntegrationTest {

        @Container
        @ServiceConnection
        static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.36");

        @Autowired
        TestRestTemplate restTemplate;

        @BeforeAll
        static void setUpBeforeAll(){
            mySQLContainer.withInitScript("post.sql");
        }

        @Test
        @Rollback
        @DirtiesContext
        public void basicScenario(){
            Post blogPost = new Post("New Blog", "Here is Blog Description", "Cool Author");

            ResponseEntity<Post> createdPostResponse = restTemplate.exchange("/api/posts", POST, new HttpEntity<>(blogPost), Post.class);
            assertThat(createdPostResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            Post createdPost = createdPostResponse.getBody();
            LocalDateTime postCreatedTime = createdPost.createdTime();
            assertThat(createdPost.title()).isEqualTo(blogPost.title());
            assertThat(createdPost.content()).isEqualTo(blogPost.content());
            assertThat(createdPost.author()).isEqualTo(blogPost.author());
            assertThat(createdPost.id()).isNotNull();
            assertThat(postCreatedTime).isNotNull();
            assertThat(createdPost.modifiedTime()).isNull();

            //Verify if a new post exists in the database
            Post[] postList = restTemplate.getForObject("/api/posts", Post[].class);

            assertThat(postList.length).isEqualTo(1);

            Post firstPost = postList[0];
            assertThat(firstPost).isEqualTo(createdPost);

            //Update Post
            Post post = new Post("Totally new Blog content", "Totally new content is here", "Cool Author");
            ResponseEntity<Post> updatedPostResponse = restTemplate.exchange("/api/posts/{id}", PUT, new HttpEntity<>(post), Post.class, createdPost.id());
            assertThat(updatedPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            Post updatedPost = updatedPostResponse.getBody();
            assertThat(updatedPost).isNotNull();
            assertThat(updatedPost.title()).isEqualTo(post.title());
            assertThat(updatedPost.content()).isEqualTo(post.content());
            assertThat(updatedPost.author()).isEqualTo(post.author());
            assertThat(updatedPost.id()).isEqualTo(createdPost.id());
            assertThat(updatedPost.createdTime()).isEqualTo(postCreatedTime);
            assertThat(updatedPost.modifiedTime()).isAfter(postCreatedTime);
            //Verify if the updated post exists in the database
            Post[] updatedPostList = restTemplate.getForObject("/api/posts", Post[].class);

            assertThat(updatedPostList.length).isEqualTo(1);

            Post currentPost = updatedPostList[0];
            assertThat(currentPost).isEqualTo(updatedPost);

            //Delete and verify if entity does not exists
            ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/posts/{id}", DELETE, null, Void.class, createdPost.id());
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            Post[] postListAfterDelete = restTemplate.getForObject("/api/posts", Post[].class);
            assertThat(postListAfterDelete.length).isEqualTo(0);

        }


        @Test
        @Rollback
        @DirtiesContext
        public void extendedWorkingScenario(){
            Map<Long,Post> newPosts = new HashMap<>();
            Post post1 = createAndTest("Test", "Here is super cool description", "Test");
            Post post2 = createAndTest(RandomStringUtils.random(3), RandomStringUtils.random(10), RandomStringUtils.random(3));
            Post post3 = createAndTest(RandomStringUtils.random(255), RandomStringUtils.random(255), RandomStringUtils.random(255));
            Post post4 = createAndTest(RandomStringUtils.random(50), RandomStringUtils.random(255), RandomStringUtils.random(255));
            Post post5 = createAndTest(RandomStringUtils.randomAlphanumeric(255), RandomStringUtils.randomAlphanumeric(9999), RandomStringUtils.random(3));
            Post post6 = createAndTest(RandomStringUtils.random(2), RandomStringUtils.random(10), RandomStringUtils.random(2));


            newPosts.put(post1.id(), post1);
            newPosts.put(post2.id(), post2);
            newPosts.put(post3.id(), post3);
            newPosts.put(post4.id(), post4);
            newPosts.put(post5.id(), post5);
            newPosts.put(post6.id(), post6);

            //Verify if a new post exists in the database
            Post[] postList = restTemplate.getForObject("/api/posts", Post[].class);

            assertThat(postList.length).isEqualTo(newPosts.size());

            for (Post post : postList) {
                assertThat(newPosts.toString()).contains(post.toString());
            }

            //Update Post
            for (Map.Entry<Long, Post> longPostEntry : newPosts.entrySet()) {
                Post createdPost = longPostEntry.getValue();
                createdPost.title(RandomStringUtils.random(100));
                ResponseEntity<Post> updatedPostResponse = restTemplate.exchange("/api/posts/{id}", PUT, new HttpEntity<>(createdPost), Post.class, createdPost.id());
                assertThat(updatedPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                Post updatedPost = updatedPostResponse.getBody();
                assertThat(updatedPost).isNotNull();
                assertThat(updatedPost.title()).isEqualTo(createdPost.title());
                assertThat(updatedPost.content()).isEqualTo(createdPost.content());
                assertThat(updatedPost.author()).isEqualTo(createdPost.author());
                assertThat(updatedPost.id()).isEqualTo(createdPost.id());
                assertThat(updatedPost.createdTime()).isEqualTo(createdPost.createdTime());
                assertThat(updatedPost.modifiedTime()).isAfter(createdPost.createdTime());
            }
        }

        private Post createAndTest(String title, String content, String author){
            Post blogPost = new Post(title, content, author);

            ResponseEntity<Post> createdPostResponse = restTemplate.exchange("/api/posts", POST, new HttpEntity<>(blogPost), Post.class);
            assertThat(createdPostResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            Post createdPost = createdPostResponse.getBody();

            LocalDateTime postCreatedTime = createdPost.createdTime();
            assertThat(createdPost.title()).isEqualTo(blogPost.title());
            assertThat(createdPost.content()).isEqualTo(blogPost.content());
            assertThat(createdPost.author()).isEqualTo(blogPost.author());
            assertThat(createdPost.id()).isNotNull();
            assertThat(postCreatedTime).isNotNull();
            assertThat(createdPost.modifiedTime()).isNull();

            return createdPost;
        }
    }
