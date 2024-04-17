package com.newwek.blogservice.domain.dto;

import com.newwek.blogservice.domain.Post;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Data Transfer Object (DTO) for creating a new {@link Post} in the Blog Service.
 * This record is used to encapsulate the required data needed to create a new blog post,
 * including the post's title, content, and author details. Each field is validated to ensure
 * that the data conforms to the expected formats and length constraints before being processed
 * by the service layer. This DTO is an integral part of the data layer abstraction, which helps
 * to ensure that only valid data is accepted and processed into new {@link Post} instances.
 *
 * <ul>
 *   <li><b>title</b>: The title of the blog post. It is a brief yet descriptive headline for the blog post.
 *       The title must not be empty and should be between 2 and 255 characters to ensure clarity and readability.</li>
 *   <li><b>content</b>: The main text of the blog post. This is a detailed and substantive part of the post,
 *       ranging from 10 to 10,000 characters. It allows for comprehensive expression of thoughts, experiences,
 *       or informative content.</li>
 *   <li><b>author</b>: The name of the person who wrote the blog post. It ensures attribution and credibility
 *       of the content. The author's name must be between 2 and 255 characters, supporting most name formats.</li>
 * </ul>
 *
 * Using this DTO in the API's interface layer simplifies data handling and enhances security by segregating
 * the API layer from the service layer and allowing for pre-validation of inputs.
 */
@Schema(description = "Data Transfer Object for creating a new blog post")
public record CreatePostDto(
        @Schema(description = "The title of the post, must be between 2 and 255 characters",
                example = "Introduction to Blogging",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 2, max = 255, message = "Title could not be less than 2 or bigger than 255 symbols")
        @NotEmpty(message = "Title could not be empty")
        String title,

        @Schema(description = "The content of the post, must be between 10 and 10,000 characters",
                example = "Blogging is a way to share your thoughts and ideas with the world...",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 10, max = 10000, message = "Content could not be less than 10 or bigger than 10000 symbols")
        @NotEmpty(message = "Content could not be empty")
        String content,

        @Schema(description = "The author of the post, must be between 2 and 255 characters",
                example = "John Doe",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 2, max = 255, message = "Author could not be less than 2 or bigger than 255 symbols")
        @NotEmpty(message = "Author could not be empty")
        String author) implements Serializable {
}

