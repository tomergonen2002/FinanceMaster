package com.financemaster.rest_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Lies zuerst application.properties key 'frontend.url', fallback auf env 'FRONTEND_URL', sonst leer
    @Value("${frontend.url:${FRONTEND_URL:}}")
    private String frontendUrl;

    @Override
    @SuppressWarnings("null")
    public void addCorsMappings(CorsRegistry registry) {
    final String[] allowedOrigins = (frontendUrl == null || frontendUrl.isBlank())
        ? new String[] {"http://localhost:5173", "http://localhost:3000"}
        : frontendUrl.split("\\s*,\\s*");

    registry.addMapping("/**")
        .allowedOriginPatterns(allowedOrigins)
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("Content-Disposition")
        .allowCredentials(true)
        .maxAge(3600);
    }
}
