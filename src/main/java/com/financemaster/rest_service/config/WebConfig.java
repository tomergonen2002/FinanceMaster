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
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins;
        if (frontendUrl == null || frontendUrl.isBlank()) {
            // Entwicklungs-Defaults
            allowedOrigins = new String[] {"http://localhost:5173", "http://localhost:3000"};
        } else {
            allowedOrigins = frontendUrl.split("\\s*,\\s*");
        }

    registry.addMapping("/**")
        .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
