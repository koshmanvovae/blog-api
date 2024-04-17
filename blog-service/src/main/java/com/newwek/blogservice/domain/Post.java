package com.newwek.blogservice.domain;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;

/**
 * Represents a blog post entity in the blog service system.
 * The {@code Post} class encapsulates all details of a blog post including its content, author, and timestamps.
 * This class is annotated to be recognized as an entity in a JPA context, and to interact with Hibernate for ORM capabilities.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonRootName("post")
public class Post {

    /**
     * The unique identifier for the blog post. This field is automatically generated in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The title of the blog post. This field is a simple string.
     */
    private String title;

    /**
     * The content of the blog post, capable of storing up to 10,000 characters.
     */
    @Column(length = 10000)
    private String content;

    /**
     * The author's name associated with this blog post.
     */
    private String author;

    /**
     * The timestamp representing when the post was initially created. This field is not updatable.
     */
    @Column(updatable = false)
    private LocalDateTime createdTime;

    /**
     * The timestamp representing when the post was last updated.
     */
    private LocalDateTime modifiedTime;

    /**
     * A counter to track the number of comments on this post. Initialized to 0 upon creation.
     */
    @Column
    private Long commentsCounter;

    /**
     * Lifecycle method to set initial values prior to persisting a new post entity.
     * Automatically sets {@code createdTime} to the current date and time and initializes {@code commentsCounter} to 0.
     */
    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        commentsCounter = 0L;
    }

    /**
     * Lifecycle method to update the {@code modifiedTime} to the current date and time whenever a post is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        modifiedTime = LocalDateTime.now();
    }

    /**
     * Custom constructor to create a new post with specified title, content, and author.
     *
     * @param title   the title of the post
     * @param content the main content of the post
     * @param author  the name of the post's author
     */
    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    /**
     * Determines equality based on the post's {@code id} field.
     * This method takes into account Hibernate proxy objects, to ensure equality check is consistent in all scenarios.
     *
     * @param o the object to be compared for equality
     * @return true if both objects are the same or both are posts with the same {@code id}, false otherwise
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Post post = (Post) o;
        return getId() != null && getId().equals(post.getId());
    }

    /**
     * Generates a hash code based on the post's class and {@code id} value.
     * This method considers Hibernate proxy objects to ensure consistent behavior in all ORM scenarios.
     *
     * @return the hash code value for the object
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
