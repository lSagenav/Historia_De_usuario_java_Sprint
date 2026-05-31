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
                        .version("4.0.0")
                        .description("API relacional para eventos, venues y categorias con Flyway, Slice, DTO records, filtros y soft delete auditable")
                        .contact(new Contact().name("Equipo Eventify")));
    }
}
