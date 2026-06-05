package com.eventify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventifyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eventify API")
                        .version("5.0.0")
                        .description("API profesional para eventos con DTOs completos, MapStruct, validacion Jakarta, ProblemDetail RFC 7807, Slice, Flyway y soft delete")
                        .contact(new Contact().name("Equipo Eventify")));
    }
}
