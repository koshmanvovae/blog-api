package com.newwek.commentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Arrays;

import static com.newwek.commentservice.config.CacheNames.*;


@EnableCaching
@Configuration
public class RedisConfig {


    @Value("${cache.config.entryTtl:60}")
    private int entryTtl;


    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(entryTtl))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache blogPostCache = new ConcurrentMapCache(COMMENT_CACHE);
        Cache listBlogPostCache = new ConcurrentMapCache(COMMENTS_LIST_CACHE);
        Cache blogCommentsCache = new ConcurrentMapCache(BLOG_COMMENTS_CACHE);
        cacheManager.setCaches(Arrays.asList(blogPostCache, listBlogPostCache, blogCommentsCache));
        return cacheManager;
    }

    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}
