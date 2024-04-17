package com.newwek.commentservice.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(fluent = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "blog_post_id", nullable = false)
    private Long blogPostId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "enable_to_update_till", nullable = false, updatable = false)
    private LocalDateTime enableToUpdateTill;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    public Comment(Long blogPostId, String username, String content) {
        this.blogPostId = blogPostId;
        this.username = username;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        enableToUpdateTill = createdTime.plusMinutes(60);
    }

    @PreUpdate
    protected void onUpdate() {
        if (LocalDateTime.now().isAfter(enableToUpdateTill)) {
            throw new TimeExpiredException("Update not allowed as the enable-to-update period has expired.");
        }
        modifiedTime = LocalDateTime.now();
    }
}
