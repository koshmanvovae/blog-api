package com.newwek.commentservice.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for updating an existing comment's content.
 * This DTO encapsulates the necessary data to update a comment, focusing specifically on the content field.
 * The provided content is validated to ensure it adheres to specified length and content requirements, which are crucial for maintaining the quality and relevance of the comment within the application.
 *
 * @param content The new text content of the comment to replace the old one. It must be non-null, non-empty, and non-blank,
 *                and must fall within the range of 2 to 255 characters to ensure detailed yet concise commentary.
 */
public record UpdateCommentDto(
        @NotNull(message = "Content comment could not be null")
        @Size(min = 2, max = 255, message = "Content comment could not be less than 2 or bigger than 255 symbols")
        @NotEmpty(message = "Content comment could not be empty")
        @NotBlank(message = "Content comment could not be blank")
        @Schema(description = "The updated content of the comment. It is required to be a concise yet expressive string within the length constraints of 2 to 255 characters, ensuring clarity and effectiveness of the communication.",
                required = true,
                example = "Thanks for the update, much clearer now!")
        String content
) implements Serializable {
}
