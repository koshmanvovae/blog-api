package com.newwek.commentservice.domain.dto;

import com.newwek.commentservice.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link Comment}
 */
public record UpdateCommentDto(@NotNull(message = "Content comment could not be null") @Size(message = "Content comment could not be less than 2 or bigger than 255 symbols", min = 2, max = 255) @NotEmpty(message = "Content comment could not be empty") @NotBlank(message = "Content comment could not be blank") String content) implements Serializable {
}
