package com.newwek.commentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configures the SpringFox (Swagger API documentation for Spring Boot) setup for the application,
 * specifying the configuration details for the API groups and their metadata.
 * This configuration class is annotated with {@code @Configuration}, indicating that it is a source
 * of bean definitions.
 *
 * <p>The class defines two primary configurations:</p>
 * <ul>
 *   <li>A public API group specific to comment handling in blog posts.</li>
 *   <li>Global OpenAPI information that describes the overall Comments API.</li>
 * </ul>
 */
@Configuration
public class SpringFoxConfig {

    /**
     * Creates and configures a {@code GroupedOpenApi} bean that specifies how Swagger should group
     * the API endpoints for the comment service in the application. This method is marked with
     * {@code @Bean}, signifying that the returned object is to be managed by the Spring container.
     *
     * @return a {@code GroupedOpenApi} object configured for the comment service APIs.
     * The API grouping includes:
     * <ul>
     *   <li>Group name set to "comment" for easy identification.</li>
     *   <li>Scans packages specifically containing controllers for the comment service.</li>
     * </ul>
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("comment")
                .packagesToScan("com.newwek.commentservice.controller")
                .build();
    }

    /**
     * Creates and configures an {@code OpenAPI} bean that defines meta-information for the
     * Comments API using Swagger. This method is also marked with {@code @Bean}, indicating
     * that the returned {@code OpenAPI} object is a Spring managed bean.
     *
     * @return an {@code OpenAPI} instance filled with API information including:
     * <ul>
     *   <li>API title and description that elaborates the functionality offered related to
     *       comments on blog posts.</li>
     *   <li>API versioning details to manage version control and future updates.</li>
     *   <li>License information under which the API is published, specifically pointing to
     *       the Apache 2.0 license to inform users of the usage rights and restrictions.</li>
     * </ul>
     * The description details the operations allowed on comments, including creation, modification,
     * and deletion restrictions, emphasizing the editable time window of 60 minutes for comments.
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Comments API")
                        .description("Comments API, provides different opportunities with working with blog post comments. " +
                                     "This comments could be added by specific user which required to be passed to create and edit endpoints. " +
                                     "Comments could be read and write, and also comments could be deleted by specific blog post id. " +
                                     "Also you could not modify comments after 60 minutes expires.")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
