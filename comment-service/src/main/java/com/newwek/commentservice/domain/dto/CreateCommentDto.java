package com.newwek.commentservice.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for creating a new comment on a blog post.
 * This DTO contains all necessary information to create a comment, including the associated blog post ID and the content of the comment itself.
 * The content is validated to ensure it meets specific criteria for length and non-emptiness, thereby enforcing data integrity and usability in the comment creation process.
 *
 * @param blogPostId The ID of the blog post to which the comment will be attached. This cannot be null and must refer to an existing post.
 * @param content The text content of the comment. It must be a non-empty, non-blank string between 2 and 255 characters, providing sufficient detail to be meaningful but concise enough to ensure readability and relevance.
 */
public record CreateCommentDto(
        @NotNull(message = "Blog post id could not be null")
        @Schema(description = "The unique identifier of the blog post to which the comment is being added.", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
        Long blogPostId,

        @NotNull(message = "Content comment could not be null")
        @Size(min = 2, max = 255, message = "Content comment could not be less than 2 or bigger than 255 symbols")
        @NotEmpty(message = "Content comment could not be empty")
        @NotBlank(message = "Content comment could not be blank")
        @Schema(description = "The content of the comment. Must be a non-blank, non-empty string within 2 to 255 characters, ensuring clarity and brevity.", requiredMode = Schema.RequiredMode.REQUIRED, example = "This is a great post! Thanks for sharing.")
        String content
) implements Serializable {
}
