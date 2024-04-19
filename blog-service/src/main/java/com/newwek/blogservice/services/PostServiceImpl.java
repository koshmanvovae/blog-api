package com.newwek.blogservice.services;

import com.newwek.blogservice.domain.Post;
import com.newwek.blogservice.repositories.PostRepository;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import static com.newwek.blogservice.config.CacheNames.POST_CACHE;
import static com.newwek.blogservice.config.CacheNames.POST_LIST_CACHE;

/**
 * The {@code PostServiceImpl} class is the implementation of the {@link PostService} interface.
 * This service is marked with {@code @Service} to indicate that it's a Spring managed service class.
 * It uses a {@link PostRepository} for persistence operations.
 */
@Service
@CacheConfig(cacheNames = {POST_CACHE, POST_LIST_CACHE})
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    /**
     * Constructs a new instance of {@code PostServiceImpl} with the necessary repository.
     *
     * @param postRepository the repository used for data access operations. Must not be null.
     */
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = POST_LIST_CACHE, keyGenerator = "customKeyGenerator")
    public List<Post> findAllSortedByCommentCountDesc() {
        List<Post> allPosts = findAll();
        allPosts.sort((a, b) -> b.getCommentsCounter().compareTo(a.getCommentsCounter()));
        return allPosts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = POST_CACHE, key = "#id")
    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Caching(
            evict = {@CacheEvict(value = POST_LIST_CACHE, allEntries = true)},
            put = {@CachePut(value = POST_CACHE, key = "#post.id")}
    )
    public Post save(Post post) {
        return postRepository.save(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Caching(
            evict = {@CacheEvict(value = POST_LIST_CACHE, allEntries = true),
                     @CacheEvict(value = POST_CACHE, key = "#id")}
    )
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}

