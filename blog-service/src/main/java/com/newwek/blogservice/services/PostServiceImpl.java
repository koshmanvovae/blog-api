package com.newwek.blogservice.services;

import com.newwek.blogservice.domain.Post;
import com.newwek.blogservice.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * The {@code PostServiceImpl} class is the implementation of the {@link PostService} interface.
 * This service is marked with {@code @Service} to indicate that it's a Spring managed service class.
 * It uses a {@link PostRepository} for persistence operations.
 */
@Service
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
    public List<Post> findAllSortedByCommentCountDesc() {
        List<Post> allPosts = findAll();
        allPosts.sort((a, b) -> b.getCommentsCounter().compareTo(a.getCommentsCounter()));
        return allPosts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}

