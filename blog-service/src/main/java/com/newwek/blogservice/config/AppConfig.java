package com.newwek.blogservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * The {@code AppConfig} class configures beans for the application's context, specifically
 * defining configuration for REST client behavior. This class is annotated with {@code @Configuration},
 * indicating that it provides one or more {@code @Bean} definitions. This setup is typically used
 * in Spring Boot applications where such configuration classes provide centralized and
 * version-controlled bean definitions.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures a {@code RestTemplate} that is load-balanced by Spring Cloud.
     * The {@code @LoadBalanced} annotation is crucial for microservices architecture,
     * indicating that the RestTemplate should use a Ribbon load balancer (or another compatible
     * client-side load balancer) to distribute requests across multiple instances of a service,
     * based on the Eureka client or a similar service registry.
     *
     * <p>The {@code @Bean} annotation marks this method as a bean producer, so the returned
     * {@code RestTemplate} instance is automatically configured and managed by the Spring
     * container. This means it will leverage Spring's exception handling, message converters,
     * and more, integrated into the context's lifecycle.
     *
     * <p>Use this configured {@code RestTemplate} to communicate with other services in a
     * load-balanced manner. It's particularly useful in environments where service instances
     * might scale up and down dynamically, ensuring that requests are evenly distributed among
     * available instances.
     *
     * @return a load-balanced {@code RestTemplate} ready for use
     */
    @Bean
    @LoadBalanced
    public RestTemplate template() {
        return new RestTemplate();
    }
}

