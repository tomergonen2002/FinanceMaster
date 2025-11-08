package com.financemaster.rest_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Erst frontend.url aus application.properties, sonst FRONTEND_URL env var, sonst leer
    @Value("${frontend.url:${FRONTEND_URL:}}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Wenn keine URL gesetzt ist, erlauben wir localhost für lokale Entwicklung (dev profile),
        // sonst exakt die angegebene Origin(s).
        String[] allowed;
        if (frontendUrl == null || frontendUrl.isBlank()) {
            allowed = new String[] { "http://localhost:5173", "http://localhost:3000" };
        } else {
            // Falls mehrere Origins (kommagetrennt) konfiguriert wurden, splitten
            allowed = frontendUrl.split("\\s*,\\s*");
        }

        registry.addMapping("/**")
                .allowedOrigins(allowed)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
