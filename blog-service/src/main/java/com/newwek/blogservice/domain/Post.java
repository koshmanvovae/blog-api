package com.newwek.blogservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Accessors(fluent = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonRootName("post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("id")
    private Long id;

    @NotEmpty(message = "Title could not be empty")
    @Size(min = 2, max = 255, message = "Title could not be less than 2 or bigger than 255 symbols")
    @JsonProperty("title")
    private String title;

    @NotEmpty(message = "Content could not be empty")
    @Size(min = 10, max = 10000, message = "Content could not be less than 2 or bigger than 10000 symbols")
    @JsonProperty("content")
    private String content;

    @NotEmpty(message = "Author could not be empty")
    @Size(min = 2, max = 255, message = "Author could not be less than 2 or bigger than 255 symbols")
    @JsonProperty("author")
    private String author;

    @Column(updatable = false)
    @JsonProperty("created_time")
    private LocalDateTime createdTime;

    @JsonProperty("modified_time")
    private LocalDateTime modifiedTime;

    @Column
    @JsonProperty("comments_counter")
    private Long commentsCounter;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        commentsCounter = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedTime = LocalDateTime.now();
    }

    @Builder
    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Post post = (Post) o;
        return id() != null && Objects.equals(id(), post.id());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

