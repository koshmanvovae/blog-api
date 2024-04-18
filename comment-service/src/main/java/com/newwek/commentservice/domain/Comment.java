package com.newwek.commentservice.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a comment made on a blog post. This class is mapped to the "comment" table in the database.
 * It includes details such as the comment ID, blog post ID it is associated with, the username of the commenter,
 * the content of the comment, and timestamps that record the creation and modification times.
 *
 * Comments have a limited time during which they can be updated, set to 60 minutes post-creation. This is enforced
 * in the onUpdate lifecycle method, which checks if the comment is still within the allowable update period.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
@JsonDeserialize
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "blog_post_id", nullable = false)
    private Long blogPostId;

    @Column(nullable = false)
    private String username;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "enable_to_update_till", nullable = false, updatable = false)
    private LocalDateTime enableToUpdateTill;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    /**
     * Creates a new comment for a blog post with the specified blog post ID, username, and content.
     * This constructor initializes the comment with the provided values.
     * @param blogPostId the ID of the blog post to which the comment is being added
     * @param username the username of the individual who is making the comment
     * @param content the textual content of the comment
     */
    public Comment(Long blogPostId, String username, String content) {
        this.blogPostId = blogPostId;
        this.username = username;
        this.content = content;
    }

    /**
     * Lifecycle hook method that is called when a new comment entity is persisted.
     * It sets the creation time to the current date and time and initializes the update expiration time to 60 minutes later.
     */
    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        enableToUpdateTill = createdTime.plusMinutes(60);
    }

    /**
     * Lifecycle hook method that is called when an existing comment entity is updated.
     * This method enforces the rule that comments cannot be updated after 60 minutes from their creation.
     * If the current time is past the enable-to-update till timestamp, it throws a TimeExpiredException.
     */
    @PreUpdate
    protected void onUpdate() {
        if (LocalDateTime.now().isAfter(enableToUpdateTill)) {
            throw new TimeExpiredException("Update not allowed as the enable-to-update period has expired.");
        }
        modifiedTime = LocalDateTime.now();
    }
}
