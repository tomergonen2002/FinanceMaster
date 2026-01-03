package com.financemaster.rest_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS aktivieren - nutzt CorsConfig
            .cors(cors -> cors.configure(http))
            
            // CSRF deaktivieren für REST API (SPA verwendet kein CSRF)
            .csrf(csrf -> csrf.disable())
            
            // Authorization Rules - ALLE Endpoints public
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Alle Endpoints ohne Auth
            )
            
            // HTTP Basic Auth deaktivieren
            .httpBasic(basic -> basic.disable())
            
            // Form Login deaktivieren (REST API)
            .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setSameSite("None"); // Für Cross-Domain
        serializer.setUseSecureCookie(true); // HTTPS only
        serializer.setUseHttpOnlyCookie(true); // Security
        return serializer;
    }
}
