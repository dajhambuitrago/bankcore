package com.dajham.bankcore.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("BankCore API")
                                                .version("1.0")
                                                .description("API transaccional bancaria Hexagonal"))
                                .addServersItem(new Server()
                                                .url("http://localhost:8080")
                                                .description("Servidor local"))
                                // Agregar soporte para JWT Bearer token
                                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes("bearer-jwt",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Ingresa tu token JWT obtenido en /api/v1/auth/login")));
        }
}
