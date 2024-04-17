package com.newwek.blogservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringFoxConfig {

    /**
     * Configures a grouped API specification for blog-related operations.
     * This group encapsulates all API endpoints under the 'blog' category,
     * providing a centralized documentation hub for managing blog posts including
     * creation, retrieval, updating, and deletion of posts. The grouping helps
     * in segregating blog service operations from other service operations within the same application.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("blog")
                .packagesToScan("com.newwek.blogservice.controllers")
                .build();
    }

    /**
     * Provides the general OpenAPI configuration for the application's API documentation.
     * This configuration sets up the basic meta-information about the API such as its title,
     * description, and version. It also includes licensing information which specifies
     * the Apache 2.0 License, providing users with details on how the API can be used legally.
     * This API offers functionalities for cloud-based operations, geared towards streamlining
     * cloud service integrations and operations.
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Cloud API")
                        .description("This API serves as a backbone for cloud service operations, " +
                                     "providing an interface for the management of cloud resources. " +
                                     "It includes endpoints for blog management, user authentication, " +
                                     "and other cloud-based services, designed to streamline operations " +
                                     "and enhance user interaction with the cloud platform.")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
