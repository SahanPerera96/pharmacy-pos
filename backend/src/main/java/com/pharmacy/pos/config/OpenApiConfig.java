package com.pharmacy.pos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig
 *
 * Configures Swagger UI available at: http://localhost:8080/swagger-ui.html
 *
 * To test authenticated endpoints in Swagger:
 *   1. Call POST /api/v1/auth/login
 *   2. Copy the accessToken from the response
 *   3. Click "Authorize" button → paste "Bearer <token>"
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Pharmacy POS API",
        version     = "1.0",
        description = "REST API for the multi-branch Pharmacy Point of Sale system"
    ),
    servers = {
        @Server(url = "/", description = "Current server")
    }
)
@SecurityScheme(
    name   = "bearerAuth",
    type   = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Paste your access token here (without the 'Bearer' prefix)"
)
public class OpenApiConfig {}
