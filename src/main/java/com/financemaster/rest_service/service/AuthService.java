package com.financemaster.rest_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.persistence.repository.UserRepository;
import com.financemaster.rest_service.exception.EntityNotFoundException;
import com.financemaster.rest_service.exception.InvalidInputException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String email, String password, HttpServletRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidInputException("Invalid password");
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        user.setPassword(null);
        return user;
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        Long userId = getSessionUserId(request);
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    public Long getSessionUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute("userId");
    }

    public Long requireSessionUserId(HttpServletRequest request) {
        Long userId = getSessionUserId(request);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login erforderlich");
        }
        return userId;
    }

    public User register(String name, String email, String password) {
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new InvalidInputException("Email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }
}
