package com.newwek.blogservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.newwek.blogservice.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a data transfer object for a blog post. This DTO encapsulates detailed information about a blog post,
 * including its identification details, content, authorship, and associated timestamps for creation and modification.
 * It is primarily used to transfer blog post data efficiently across different layers of the application and to the
 * client via API endpoints, ensuring a consistent and validated data structure.
 *
 * The record is also responsible for transforming entities into DTOs, hence providing a clean separation of concerns
 * between the persistence layer and the API exposure layer. This abstraction helps maintain data integrity and security
 * by exposing only necessary information to the client.
 */
@JsonRootName("post")
@Schema(description = "Data Transfer Object for a blog post, encapsulating all relevant details including metadata such as creation and modification times.")
public record PostDto(
        @JsonProperty("id")
        @Schema(description = "The unique identifier of the blog post.", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Size(min = 2, max = 255, message = "Title could not be less than 2 or bigger than 255 symbols")
        @NotEmpty(message = "Title could not be empty")
        @JsonProperty("title")
        @Schema(description = "The title of the blog post, a concise summary of the content.", example = "Exploring OpenAPI", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Size(min = 10, max = 10000, message = "Content could not be less than 10 or bigger than 10000 symbols")
        @NotEmpty(message = "Content could not be empty")
        @JsonProperty("content")
        @Schema(description = "The full content of the blog post, detailing the subject matter in depth.", example = "Detailed exploration of OpenAPI for documenting RESTful APIs.", requiredMode = Schema.RequiredMode.REQUIRED)
        String content,

        @Size(min = 2, max = 255, message = "Author could not be less than 2 or bigger than 255 symbols")
        @NotEmpty(message = "Author could not be empty")
        @JsonProperty("author")
        @Schema(description = "The name of the author of the blog post, providing attribution.", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
        String author,

        @JsonProperty("created_time")
        @Schema(description = "The timestamp when the blog post was first created.", example = "2024-04-12T14:30:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdTime,

        @JsonProperty("modified_time")
        @Schema(description = "The timestamp of the last modification of the blog post, reflecting the most recent update.", example = "2024-04-12T15:00:00Z")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime modifiedTime,

        @JsonProperty("comments_counter")
        @Schema(description = "The count of comments on the blog post, indicating the level of engagement.", example = "150")
        Long commentsCounter

) implements Serializable {

    public PostDto(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.getCommentsCounter());
    }

    public static List<PostDto> of(List<Post> posts) {
        return posts.stream().map(PostDto::new).toList();
    }
}
