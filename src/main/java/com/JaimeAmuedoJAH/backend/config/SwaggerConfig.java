package com.JaimeAmuedoJAH.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para documentación automática de la API.
 * Accesible en: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura la documentación OpenAPI con información de la API y seguridad JWT.
     *
     * @return Configuración de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce Backend API")
                        .version("1.0")
                        .description("API REST para un sistema de e-commerce completo con gestión de usuarios, productos, categorías, carrito y órdenes"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token de autenticación. Formato: Bearer <token>")));
    }
}
