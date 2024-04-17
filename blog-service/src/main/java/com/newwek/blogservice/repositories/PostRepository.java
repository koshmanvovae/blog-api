package com.newwek.blogservice.repositories;

import com.newwek.blogservice.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * The {@code PostRepository} interface provides an abstraction layer to manage
 * {@link Post} entities in the database. It extends {@link JpaRepository}, leveraging Spring Data JPA's
 * repository abstraction to simplify the data access layer and provide common data operations like saving,
 * deleting, and finding blog posts without the need for boilerplate code.
 *
 * Through this interface, custom queries that are specific to the blog post context can be declared.
 *
 * Usage Example:
 * <pre>
 * {@code
 *     // Autowired in a Spring Service to interact with blog posts
 *     @Autowired
 *     private PostRepository postRepository;
 *
 *     public Post saveNewPost(Post post) {
 *         return postRepository.save(post);
 *     }
 *
 *     public Optional<Post> findPostById(Long id) {
 *         return postRepository.findById(id);
 *     }
 * }
 * </pre>
 *
 * <b>Note:</b> This interface is marked with {@link Repository}, indicating that it is a candidate
 * for Spring's component scanning to detect and register it as a bean in the Spring context.
 * This allows Spring to handle data access exceptions translation appropriate to the configured
 * persistence technology (here JPA).
 *
 * @see JpaRepository
 * @see Post
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}

