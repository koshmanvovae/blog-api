    package com.newwek.blogservice.controllers;

    import com.newwek.blogservice.domain.Post;
    import com.newwek.blogservice.domain.dto.PostDto;
    import com.newwek.blogservice.domain.dto.CreatePostDto;
    import com.newwek.blogservice.services.PostCommentsServiceImpl;
    import org.apache.commons.lang.math.RandomUtils;
    import org.apache.commons.lang3.RandomStringUtils;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.boot.test.mock.mockito.SpyBean;
    import org.springframework.boot.test.web.client.TestRestTemplate;
    import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
    import org.springframework.http.*;
    import org.springframework.test.annotation.DirtiesContext;
    import org.springframework.test.annotation.Rollback;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.client.RestTemplate;
    import org.testcontainers.containers.MySQLContainer;
    import org.testcontainers.junit.jupiter.Container;
    import org.testcontainers.junit.jupiter.Testcontainers;

    import java.time.LocalDateTime;
    import java.util.Arrays;
    import java.util.HashMap;
    import java.util.Map;

    import static org.assertj.core.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;
    import static org.springframework.http.HttpMethod.*;

    @Testcontainers
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @Transactional
    @DirtiesContext
    public class PostControllerIntegrationTest {

        @Container
        @ServiceConnection
        static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.36");

        @SpyBean
        private PostCommentsServiceImpl deletePostAspect;

        @MockBean
        private RestTemplate restTemplateMock;

        @Autowired
        TestRestTemplate restTemplate;


        @Test
        @Rollback
        @DirtiesContext
        public void basicScenario(){
            Post blogPost = new Post("New Blog", "Here is Blog Description", "Cool Author");

            ResponseEntity<PostDto> createdPostResponse = restTemplate.exchange("/api/posts", POST, new HttpEntity<>(blogPost), PostDto.class);
            assertThat(createdPostResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            PostDto createdPost = createdPostResponse.getBody();
            LocalDateTime postCreatedTime = createdPost.createdTime();
            assertThat(createdPost.title()).isEqualTo(blogPost.getTitle());
            assertThat(createdPost.content()).isEqualTo(blogPost.getContent());
            assertThat(createdPost.author()).isEqualTo(blogPost.getAuthor());
            assertThat(createdPost.id()).isNotNull();
            assertThat(postCreatedTime).isNotNull();
            assertThat(createdPost.modifiedTime()).isNull();

            //Verify if a new post exists in the database
            PostDto[] postList = restTemplate.getForObject("/api/posts", PostDto[].class);

            assertThat(postList.length).isEqualTo(1);

            PostDto firstPost = postList[0];
            assertThat(firstPost).isEqualTo(createdPost);

            //Update Post
            Post post = new Post("Totally new Blog content", "Totally new content is here", "Cool Author");
            ResponseEntity<PostDto> updatedPostResponse = restTemplate.exchange("/api/posts/{id}", PUT, new HttpEntity<>(post), PostDto.class, createdPost.id());
            assertThat(updatedPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            PostDto updatedPost = updatedPostResponse.getBody();
            assertThat(updatedPost).isNotNull();
            assertThat(updatedPost.title()).isEqualTo(post.getTitle());
            assertThat(updatedPost.content()).isEqualTo(post.getContent());
            assertThat(updatedPost.author()).isEqualTo(post.getAuthor());
            assertThat(updatedPost.id()).isEqualTo(createdPost.id());
            assertThat(updatedPost.createdTime()).isEqualTo(postCreatedTime);
            assertThat(updatedPost.modifiedTime()).isAfter(postCreatedTime);
            //Verify if the updated post exists in the database
            PostDto[] updatedPostList = restTemplate.getForObject("/api/posts", PostDto[].class);

            assertThat(updatedPostList.length).isEqualTo(1);

            PostDto currentPost = updatedPostList[0];
            assertThat(currentPost).isEqualTo(updatedPost);

            //Delete and verify if entity does not exists
            doNothing().when(deletePostAspect).checkResponseForIssues(any());
            ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/posts/{id}", DELETE, null, Void.class, createdPost.id());
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(deletePostAspect, times(1)).checkResponseForIssues(any());

            Post[] postListAfterDelete = restTemplate.getForObject("/api/posts", Post[].class);
            assertThat(postListAfterDelete.length).isEqualTo(0);

        }


        @Test
        @Rollback
        @DirtiesContext
        public void extendedWorkingScenario(){
            Map<Long,PostDto> newPosts = new HashMap<>();
            PostDto post1 = createAndTest("Test", "Here is super cool description", "Test");
            PostDto post2 = createAndTest(RandomStringUtils.random(3), RandomStringUtils.random(10), RandomStringUtils.random(3));
            PostDto post3 = createAndTest(RandomStringUtils.random(255), RandomStringUtils.random(255), RandomStringUtils.random(255));
            PostDto post4 = createAndTest(RandomStringUtils.random(50), RandomStringUtils.random(255), RandomStringUtils.random(255));
            PostDto post5 = createAndTest(RandomStringUtils.randomAlphanumeric(255), RandomStringUtils.randomAlphanumeric(9999), RandomStringUtils.random(3));
            PostDto post6 = createAndTest(RandomStringUtils.random(2), RandomStringUtils.random(10), RandomStringUtils.random(2));


            newPosts.put(post1.id(), post1);
            newPosts.put(post2.id(), post2);
            newPosts.put(post3.id(), post3);
            newPosts.put(post4.id(), post4);
            newPosts.put(post5.id(), post5);
            newPosts.put(post6.id(), post6);

            //Verify if a new post exists in the database
            PostDto[] postList = restTemplate.getForObject("/api/posts", PostDto[].class);

            assertThat(postList.length).isEqualTo(newPosts.size());

            for (PostDto post : postList) {
                assertThat(post).isNotNull();
                assertThat(post.id()).isNotNull();
                assertThat(newPosts.get(post.id())).isNotNull();
                assertThat(newPosts.get(post.id())).isEqualTo(post);
            }

            //Update Post
            for (Map.Entry<Long, PostDto> longPostEntry : newPosts.entrySet()) {
                PostDto createdPost = longPostEntry.getValue();
                CreatePostDto createPostDto = new CreatePostDto(RandomStringUtils.random(100), createdPost.content(), createdPost.author());
                ResponseEntity<PostDto> updatedPostResponse = restTemplate.exchange("/api/posts/{id}", PUT, new HttpEntity<>(createPostDto), PostDto.class, createdPost.id());
                assertThat(updatedPostResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                PostDto updatedPost = updatedPostResponse.getBody();
                assertThat(updatedPost).isNotNull();
                assertThat(updatedPost.title()).isEqualTo(createPostDto.title());
                assertThat(updatedPost.content()).isEqualTo(createPostDto.content());
                assertThat(updatedPost.author()).isEqualTo(createPostDto.author());
                assertThat(updatedPost.id()).isEqualTo(createdPost.id());
                assertThat(updatedPost.createdTime()).isEqualTo(createdPost.createdTime());
                assertThat(updatedPost.modifiedTime()).isAfter(createdPost.createdTime());
            }
        }

        @Test
        @Rollback
        @DirtiesContext
        public void verifySorting(){
            Map<Long,PostDto> newPosts = new HashMap<>();
            PostDto post1 = createAndTest("Test", "Here is super cool description", "Test");
            PostDto post2 = createAndTest(RandomStringUtils.random(3), RandomStringUtils.random(10), RandomStringUtils.random(3));
            PostDto post3 = createAndTest(RandomStringUtils.random(255), RandomStringUtils.random(255), RandomStringUtils.random(255));
            PostDto post4 = createAndTest(RandomStringUtils.random(50), RandomStringUtils.random(255), RandomStringUtils.random(255));
            PostDto post5 = createAndTest(RandomStringUtils.randomAlphanumeric(255), RandomStringUtils.randomAlphanumeric(9999), RandomStringUtils.random(3));
            PostDto post6 = createAndTest(RandomStringUtils.random(2), RandomStringUtils.random(10), RandomStringUtils.random(2));



            post2 = increaseCommentsCounterForPost(post2, 2);
            post3 = increaseCommentsCounterForPost(post3, 3);
            post4 = increaseCommentsCounterForPost(post4, 4);
            post5 = increaseCommentsCounterForPost(post5, 5);
            post6 = increaseCommentsCounterForPost(post6, 6);


            newPosts.put(post1.id(), post1);
            newPosts.put(post2.id(), post2);
            newPosts.put(post3.id(), post3);
            newPosts.put(post4.id(), post4);
            newPosts.put(post5.id(), post5);
            newPosts.put(post6.id(), post6);

            PostDto[] postDtos = Arrays.stream(newPosts.values().toArray(new PostDto[0]))
                    .sorted((a,b) -> b.commentsCounter().compareTo(a.commentsCounter())).toArray(PostDto[]::new);

            //Verify if a new post exists in the database
            PostDto[] postList = restTemplate.getForObject("/api/posts", PostDto[].class);

            assertThat(postList.length).isEqualTo(newPosts.size());

            assertThat(postList).isEqualTo(postDtos);
        }

        @Test
        @Rollback
        @DirtiesContext
        public void verifySortingWithRandom(){
            Map<Long,PostDto> newPosts = new HashMap<>();
            PostDto post1 = createAndTest("Test", "Here is super cool description", "Test");
            PostDto post2 = createAndTest(RandomStringUtils.random(3), RandomStringUtils.random(10), RandomStringUtils.random(3));
            PostDto post3 = createAndTest(RandomStringUtils.random(255), RandomStringUtils.random(255), RandomStringUtils.random(255));
            PostDto post4 = createAndTest(RandomStringUtils.random(50), RandomStringUtils.random(255), RandomStringUtils.random(255));
            PostDto post5 = createAndTest(RandomStringUtils.randomAlphanumeric(255), RandomStringUtils.randomAlphanumeric(9999), RandomStringUtils.random(3));
            PostDto post6 = createAndTest(RandomStringUtils.random(2), RandomStringUtils.random(10), RandomStringUtils.random(2));

            post1 = increaseCommentsCounterForPost(post1, RandomUtils.nextInt(100));
            post2 = increaseCommentsCounterForPost(post2, RandomUtils.nextInt(100));
            post3 = increaseCommentsCounterForPost(post3, RandomUtils.nextInt(100));
            post4 = increaseCommentsCounterForPost(post4, RandomUtils.nextInt(100));
            post5 = increaseCommentsCounterForPost(post5, RandomUtils.nextInt(100));
            post6 = increaseCommentsCounterForPost(post6, RandomUtils.nextInt(100));


            newPosts.put(post1.id(), post1);
            newPosts.put(post2.id(), post2);
            newPosts.put(post3.id(), post3);
            newPosts.put(post4.id(), post4);
            newPosts.put(post5.id(), post5);
            newPosts.put(post6.id(), post6);

            PostDto[] postDtos = Arrays.stream(newPosts.values().toArray(new PostDto[0]))
                    .sorted((a,b) -> b.commentsCounter().compareTo(a.commentsCounter())).toArray(PostDto[]::new);

            //Verify if a new post exists in the database
            PostDto[] postList = restTemplate.getForObject("/api/posts", PostDto[].class);

            assertThat(postList.length).isEqualTo(newPosts.size());

            assertThat(postList).isEqualTo(postDtos);
        }

        private PostDto increaseCommentsCounterForPost(PostDto postDto, int incrementAmount) {
            for (int i = 0; i < incrementAmount; i++) {
                ResponseEntity<Void> incrementResponse = restTemplate.getForEntity(STR."/api/posts/update-comments-count/\{postDto.id()}", Void.class);
                assertThat(incrementResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            }


            ResponseEntity<PostDto> postResponse = restTemplate.exchange(STR."/api/posts/\{postDto.id()}", GET, null, PostDto.class);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            PostDto updatedPostDto = postResponse.getBody();
            assertThat(updatedPostDto).isNotNull();
            assertThat(updatedPostDto.commentsCounter()).isEqualTo(postDto.commentsCounter() + incrementAmount);

            return updatedPostDto;
        }

        private PostDto createAndTest(String title, String content, String author){
            Post blogPost = new Post(title, content, author);

            ResponseEntity<PostDto> createdPostResponse = restTemplate.exchange("/api/posts", POST, new HttpEntity<>(blogPost), PostDto.class);
            assertThat(createdPostResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            PostDto createdPost = createdPostResponse.getBody();

            LocalDateTime postCreatedTime = createdPost.createdTime();
            assertThat(createdPost.title()).isEqualTo(blogPost.getTitle());
            assertThat(createdPost.content()).isEqualTo(blogPost.getContent());
            assertThat(createdPost.author()).isEqualTo(blogPost.getAuthor());
            assertThat(createdPost.id()).isNotNull();
            assertThat(postCreatedTime).isNotNull();
            assertThat(createdPost.modifiedTime()).isNull();

            return createdPost;
        }
    }
