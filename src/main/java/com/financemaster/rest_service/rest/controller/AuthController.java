package com.financemaster.rest_service.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.service.AuthService;
import com.financemaster.rest_service.exception.EntityNotFoundException;
import com.financemaster.rest_service.exception.InvalidInputException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * REST Controller für Authentifizierung ohne Spring Security
 */
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /auth/login - User anmelden
     */
    @PostMapping("/auth/login")
    public User login(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.getOrDefault("email", "").trim();
        String password = payload.getOrDefault("password", "");
        
        try {
            User user = authService.login(email, password, request);
            return user;
        } catch (EntityNotFoundException | InvalidInputException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    /**
     * GET /auth/me - Aktuellen User abrufen
     */
    @GetMapping("/auth/me")
    public User me(HttpServletRequest request) {
        User user = authService.getCurrentUser(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        user.setPassword(null);
        return user;
    }

    /**
     * POST /auth/register - Neuen User registrieren
     */
    @PostMapping("/auth/register")
    public User register(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String name = payload.getOrDefault("name", "").trim();
        String email = payload.getOrDefault("email", "").trim();
        String password = payload.getOrDefault("password", "");
        
        try {
            User user = authService.register(name, email, password);
            
            // Automatisch einloggen nach Registrierung
            authService.login(email, password, request);
            
            user.setPassword(null);
            return user;
        } catch (InvalidInputException ex) {
            if (ex.getMessage().contains("Email already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
