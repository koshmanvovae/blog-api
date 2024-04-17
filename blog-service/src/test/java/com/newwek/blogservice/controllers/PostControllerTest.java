package com.newwek.blogservice.controllers;

import com.newwek.blogservice.domain.Post;
import com.newwek.blogservice.services.PostService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.StringTemplate.STR;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    List<Post> posts = new ArrayList<>();

    @Test
    void getAllPosts_commonScenario() throws Exception {
        posts = List.of(
                new Post(1L,"Blog Post", "This is the blog content.", "Vladimir", LocalDateTime.of(2024,4, 5,21,0),  LocalDateTime.of(2024,4, 14,23,0)),
                new Post(2L, "Animals", "Animals blog", "Lion" , LocalDateTime.of(2024,4, 15,23,0),  LocalDateTime.of(2024,4, 15,23,0))
        );

        String expectedResult = """
                [
                    {
                        "id": 1,
                        "title": "Blog Post",
                        "content": "This is the blog content.",
                        "author": "Vladimir",
                        "createdTime": "2024-04-05T21:00:00",
                        "modifiedTime": "2024-04-14T23:00:00"
                    },
                    {
                        "id": 2,
                        "title": "Animals",
                        "content": "Animals blog",
                        "author": "Lion",
                        "createdTime": "2024-04-15T23:00:00",
                        "modifiedTime": "2024-04-15T23:00:00"
                    }
                ]
                """;

        when(postService.findAll()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult, true));
    }

    @Test
    void getAllPosts_emptyFields() throws Exception {
        posts = List.of(
                new Post(1L,"", "This is the blog content.", "Vladimir", LocalDateTime.of(2024,4, 5,21,0),  LocalDateTime.of(2024,4, 14,23,0)),
                new Post(2L,"", "", "Vladimir", LocalDateTime.of(2024,4, 5,21,0),  LocalDateTime.of(2024,4, 14,23,0)),
                new Post(13L,"", "", "", LocalDateTime.of(2024,4, 5,21,0),  LocalDateTime.of(2024,4, 14,23,0)),
                new Post(54L,"", "", "", null,  null),
                new Post(233L,"Blog", null, "Vladimir", LocalDateTime.of(2024,4, 5,21,0),  LocalDateTime.of(2024,4, 14,23,0)),
                new Post(312L, null, null, null , null, null)
        );

        String expectedResult = """
                [
                     {
                         "id": 1,
                         "title": "",
                         "content": "This is the blog content.",
                         "author": "Vladimir",
                         "createdTime": "2024-04-05T21:00:00",
                         "modifiedTime": "2024-04-14T23:00:00"
                     },
                     {
                         "id": 2,
                         "title": "",
                         "content": "",
                         "author": "Vladimir",
                         "createdTime": "2024-04-05T21:00:00",
                         "modifiedTime": "2024-04-14T23:00:00"
                     },
                     {
                         "id": 13,
                         "title": "",
                         "content": "",
                         "author": "",
                         "createdTime": "2024-04-05T21:00:00",
                         "modifiedTime": "2024-04-14T23:00:00"
                     },
                     {
                         "id": 54,
                         "title": "",
                         "content": "",
                         "author": "",
                         "createdTime": null,
                         "modifiedTime": null
                     },
                     {
                         "id": 233,
                         "title": "Blog",
                         "content": null,
                         "author": "Vladimir",
                         "createdTime": "2024-04-05T21:00:00",
                         "modifiedTime": "2024-04-14T23:00:00"
                     },
                     {
                         "id": 312,
                         "title": null,
                         "content": null,
                         "author": null,
                         "createdTime": null,
                         "modifiedTime": null
                     }
                 ]
                """;

        when(postService.findAll()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult, true));
    }

    @Test
    void getPostById() throws Exception {
        posts = List.of(
                new Post(1L,"Blog Post", "This is the blog content.", "Vladimir", LocalDateTime.of(2024,4, 5,21,0),  LocalDateTime.of(2024,4, 14,23,0)),
                new Post(2L, "Animals", "Animals blog", "Lion" , LocalDateTime.of(2024,4, 15,23,0),  LocalDateTime.of(2024,4, 15,23,0))
        );


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        Post firstPost = posts.get(0);
        String expectedResult1 = STR."""
            {
                "id":\{firstPost.id()},
                "title":"\{firstPost.title()}",
                "content":"\{firstPost.content()}",
                "author":"\{firstPost.author()}",
                "createdTime":"\{firstPost.createdTime().format(dtf)}",
                "modifiedTime":"\{firstPost.modifiedTime().format(dtf)}"
            }
            """;

        Post secondPost = posts.get(1);
        String expectedResult2 = STR."""
            {
                "id":\{secondPost.id()},
                "title":"\{secondPost.title()}",
                "content":"\{secondPost.content()}",
                "author":"\{secondPost.author()}",
                "createdTime":"\{secondPost.createdTime().format(dtf)}",
                "modifiedTime":"\{secondPost.modifiedTime().format(dtf)}"
            }
            """;

        when(postService.findById(1L)).thenReturn(firstPost);
        when(postService.findById(2L)).thenReturn(secondPost);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult1, true));

        mockMvc.perform(get("/api/posts/2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult2, true));


    }

    @Test
    void getPostById_notFoundException() throws Exception {
        long randId = 999L;
        when(postService.findById(randId)).thenReturn(null);

        mockMvc.perform(get(STR."/api/posts/\{randId}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPost() throws Exception {
        Post post = new Post("NEW Blog Post", "NEW This is the blog content.", "Vladimir");
        Post updatedPost = new Post(1L,
                "NEW Blog Post",
                "NEW This is the blog content.",
                "Vladimir",
                LocalDateTime.of(2024, 4, 5, 21, 0),
                LocalDateTime.of(2024, 4, 14, 23, 0));

        when(postService.save(post)).thenReturn(updatedPost);

        sendAndTestRequest(post, status().isCreated());


        testValidPost("Title", RandomStringUtils.randomAlphabetic(9999), "Author");
        testValidPost("Test",RandomStringUtils.randomAlphabetic(7500), "Author");
        testValidPost("Test",RandomStringUtils.randomAlphabetic(5000), "Author");
        testValidPost("Test",RandomStringUtils.randomAlphabetic(2500), "Author");
        testValidPost("Test",RandomStringUtils.randomAlphabetic(1225), "Author");
        testValidPost("duo",RandomStringUtils.randomAlphabetic(11), "utc");
        testValidPost("duo",RandomStringUtils.randomAlphabetic(10), "utc");
    }
    @Test
    void shouldNotCreatePostIfDataInvalid() throws Exception {
        testPostWithInvalidData("N", "N", "V");
        testPostWithInvalidData( "Ns", "Ns", "Vs");
        testPostWithInvalidData( "N1111", "N  ", "Author");
        testPostWithInvalidData("N", "N  N  N  N  N TEST", "Author");
        testPostWithInvalidData("", "", "");
        testPostWithInvalidData("", "", "V");
        testPostWithInvalidData("", "N", "V");
        testPostWithInvalidData( "","N", "Vladimir");
        testPostWithInvalidData( "","N", "Vladimir");
        testPostWithInvalidData("Title",RandomStringUtils.random(10000), "Author");
        testPostWithInvalidData("Title",RandomStringUtils.random(10001), "Author");
        testPostWithInvalidData("Title",RandomStringUtils.random(9), "Author");
        testPostWithInvalidData("Title",RandomStringUtils.random(8), "Author");
    }

    private void testPostWithInvalidData(String title, String content, String author) throws Exception {
        testPost(title,content,author, status().isBadRequest());
    }

    private void testValidPost(String title, String content, String author) throws Exception {
        Post post = new Post(title, content, author);
        when(postService.save(post)).thenReturn(post);
        sendAndTestRequest(post, status().isCreated());
    }

    private void testPost(String title, String content, String author, ResultMatcher expectedMatcher) throws Exception {
        Post post = new Post(title, content, author);
        sendAndTestRequest(post, expectedMatcher);
    }

    private void sendAndTestRequest(Post post, ResultMatcher expectedMatcher) throws Exception {
        String requestBody = STR."""
                {
                    "title": "\{post.title()}",
                    "content": "\{post.content()}",
                    "author": "\{post.author()}"
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(expectedMatcher);
    }

    @Test
    void updatePost() throws Exception {
        Post post = new Post(1L,
                "NEW Blog Post",
                "NEW This is the blog content.",
                "Vladimir",
                LocalDateTime.of(2024, 4, 5, 21, 0),
                LocalDateTime.of(2024, 4, 14, 23, 0));
        String requestBody = STR."""
                {
                    "title": "\{post.title()}",
                    "content": "\{post.content()}",
                    "author": "\{post.author()}"
                }
                """;
        when(postService.findById(post.id())).thenReturn(post);

        mockMvc.perform(put(STR."/api/posts/\{post.id()}")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        //TODO add boundary tests
    }

    @Test
    void shouldNotUpdateNonExistentPost() throws Exception {
        String requestBody = """
                {
                    "title": "test",
                    "content": "testt testt",
                    "author": "author"
                }
                """;
        when(postService.findById(any())).thenReturn(null);

        mockMvc.perform(put("/api/posts/100")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/posts/1")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/posts/1000")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateInvalidPost() throws Exception {
        Post post = new Post(1L,
                "NEW Blog Post",
                "NEW This is the blog content.",
                "Vladimir",
                LocalDateTime.of(2024, 4, 5, 21, 0),
                LocalDateTime.of(2024, 4, 14, 23, 0));
        String requestBody = """
                {
                    "title": "1",
                    "content": "testt tes",
                    "author": "auth"
                }
                """;
        when(postService.findById(post.id())).thenReturn(post);

        mockMvc.perform(put(STR."/api/posts/\{post.id()}")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        //todo add more tests cases
    }

    @Test
    void deletePost() throws Exception {
        Post post = new Post(1L,
                "NEW Blog Post",
                "NEW This is the blog content.",
                "Vladimir",
                LocalDateTime.of(2024, 4, 5, 21, 0),
                LocalDateTime.of(2024, 4, 14, 23, 0));
        when(postService.findById(post.id())).thenReturn(post);

        mockMvc.perform(delete(STR."/api/posts/\{post.id()}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteNonExistentPost() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/posts/999"))
                .andExpect(status().isNotFound());
    }
}
