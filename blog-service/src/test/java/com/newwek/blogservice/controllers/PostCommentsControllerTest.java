package com.newwek.blogservice.controllers;

import com.newwek.blogservice.services.PostCounterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostCommentsController.class)
@ContextConfiguration(classes = PostCommentsController.class)
class PostCommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostCounterService postCounterService;

    @Test
    void incrementPostCommentsCounterTest() throws Exception {
        Long postId = 1L;

        // We simulate the behavior that would occur when this method is called
        doNothing().when(postCounterService).incrementPostCommentsCounter(postId);

        // Perform the get request
        mockMvc.perform(get(STR."/api/posts/update-comments-count/\{postId}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify that the service method was called once
        verify(postCounterService, times(1)).incrementPostCommentsCounter(postId);
    }

    @Test
    void decrementPostCommentsCounterTest() throws Exception {
        Long postId = 1L;

        // We simulate the behavior that would occur when this method is called
        doNothing().when(postCounterService).decrementPostCommentsCounter(postId);

        // Perform the delete request
        mockMvc.perform(delete(STR."/api/posts/update-comments-count/\{postId}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify that the service method was called once
        verify(postCounterService, times(1)).decrementPostCommentsCounter(postId);
    }

    @Test
    void incrementPostCommentsCounter_NotFound() throws Exception {
        Long postId = 999L;

        // Assume the post does not exist or other issues arise
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")).when(postCounterService).incrementPostCommentsCounter(postId);

        mockMvc.perform(get(STR."/api/posts/update-comments-count/\{postId}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());  // Assuming a 500 error for simplicity, adjust based on actual error handling

        verify(postCounterService, times(1)).incrementPostCommentsCounter(postId);
    }

    @Test
    void decrementPostCommentsCounter_NotFound() throws Exception {
        Long postId = 999L;

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")).when(postCounterService).decrementPostCommentsCounter(postId);

        mockMvc.perform(delete("/api/posts/update-comments-count/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(postCounterService, times(1)).decrementPostCommentsCounter(postId);
    }
}
