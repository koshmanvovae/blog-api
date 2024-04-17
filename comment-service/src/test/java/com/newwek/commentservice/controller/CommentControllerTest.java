package com.newwek.commentservice.controller;

import com.newwek.commentservice.domain.Comment;
import com.newwek.commentservice.domain.dto.UpdateCommentDto;
import com.newwek.commentservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
@ContextConfiguration(classes = CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    void getAllComments() throws Exception {
        List<Comment> comments = List.of(new Comment(1L, 101L, "user1", "Nice post!", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()));
        when(commentService.findAll()).thenReturn(comments);

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].content").value("Nice post!"));
    }

    @Test
    void getCommentById() throws Exception {
        Comment comment = new Comment(1L, 101L, "user1", "Interesting comment", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        when(commentService.findById(1L)).thenReturn(comment);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Interesting comment"));
    }

    @Test
    void getCommentById_NotFound() throws Exception {
        when(commentService.findById(1L)).thenReturn(null);
        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment() throws Exception {
        Comment savedComment = new Comment(1L, 101L, "user1", "Great post!", LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), LocalDateTime.now());
        when(commentService.save(any(Comment.class))).thenReturn(savedComment);

        mockMvc.perform(post("/api/comments")
                        .header("X-Username", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"blogPostId\": 101, \"content\": \"Great post!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Great post!"));
    }

    @Test
    void updateComment() throws Exception {
        Comment existingComment = new Comment(1L, 101L, "user1", "Old content", LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), LocalDateTime.now());
        when(commentService.findById(1L)).thenReturn(existingComment);
        when(commentService.save(any(Comment.class))).thenReturn(existingComment);

        mockMvc.perform(put("/api/comments/1")
                        .header("X-Username", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Updated content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void shouldNotUpdateComment() throws Exception {
        Comment existingComment = new Comment(1L, 101L, "user1", "Old content", LocalDateTime.now().minusMinutes(60), LocalDateTime.now().minusMinutes(60), null);
        when(commentService.findById(1L)).thenReturn(existingComment);
        when(commentService.save(any(Comment.class))).thenReturn(existingComment);

        mockMvc.perform(put("/api/comments/1")
                        .header("X-Username", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Updated content\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteComment() throws Exception {
        Comment comment = new Comment(1L, 101L, "user1", "Some content", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        when(commentService.findById(1L)).thenReturn(comment);
        doNothing().when(commentService).deleteById(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteNonExistentComment() throws Exception {
        doNothing().when(commentService).deleteById(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNotFound());

        doNothing().when(commentService).deleteById(12L);

        mockMvc.perform(delete("/api/comments/12"))
                .andExpect(status().isNotFound());

        doNothing().when(commentService).deleteById(1000L);

        mockMvc.perform(delete("/api/comments/1000"))
                .andExpect(status().isNotFound());
    }


}

