package com.example.bankcards.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bank management system",
                description = "api for managing user's cards",
                version = "1.0.1",
                contact = @Contact(
                        name = "Panasik Uladzislau",
                        email = "panasikvladislav1@gmail.com",
                        url = "https://github.com/Vladislav3421730"
                )
        )
)
@SecurityScheme(
        name = "BearerAuthentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
