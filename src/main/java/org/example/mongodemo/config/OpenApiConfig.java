package org.example.mongodemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mongo Demo Product API")
                        .description("Spring Boot CRUD, pagination, and MongoDB aggregation API")
                        .version("v1")
                        .contact(new Contact().name("API Support").email("support@example.org")));
    }
}

