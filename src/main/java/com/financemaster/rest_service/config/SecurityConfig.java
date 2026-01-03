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
            
            // Authorization Rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register").permitAll() // Public endpoints
                .anyRequest().authenticated() // Alle anderen Endpoints brauchen Auth
            )
            
            // Exception Handling - 401 statt 403 für unauthenticated
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Unauthorized");
                })
            )
            
            // Session Management
            .sessionManagement(session -> session
                .sessionFixation().newSession() // Neue Session nach Login
                .maximumSessions(1) // Max 1 Session pro User
            )
            
            // Logout
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200);
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
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
