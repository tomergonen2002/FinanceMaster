package com.financemaster.rest_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.financemaster.rest_service.exception.EntityNotFoundException;
import com.financemaster.rest_service.exception.InvalidInputException;
import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.persistence.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_success_setsSessionAndReturnsUser() {
        User user = new User();
        user.setId(42L);
        user.setEmail("test@example.com");
        user.setPassword("encoded");

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(request.getSession(true)).thenReturn(session);

        User result = authService.login("test@example.com", "secret", request);

        verify(session).setAttribute("userId", 42L);
        assertEquals("test@example.com", result.getEmail());
        assertNull(result.getPassword(), "Password must be cleared on return");
    }

    @Test
    void login_wrongPassword_throwsInvalidInput() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encoded");

        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(InvalidInputException.class, () -> authService.login("test@example.com", "wrong", request));
        verify(request, never()).getSession(true);
    }

    @Test
    void login_userNotFound_throwsEntityNotFound() {
        when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> authService.login("missing@example.com", "pw", request));
    }

    @Test
    void requireSessionUserId_withoutSession_throws401() {
        when(request.getSession(false)).thenReturn(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.requireSessionUserId(request));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void requireSessionUserId_withSession_returnsId() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(7L);
        Long id = authService.requireSessionUserId(request);
        assertEquals(7L, id);
    }

    @Test
    void getCurrentUser_returnsUserWhenPresent() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5L);

        User user = new User();
        user.setId(5L);
        user.setEmail("u@example.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        User result = authService.getCurrentUser(request);
        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void getCurrentUser_returnsNullWhenNoSession() {
        when(request.getSession(false)).thenReturn(null);
        assertNull(authService.getCurrentUser(request));
    }
}
