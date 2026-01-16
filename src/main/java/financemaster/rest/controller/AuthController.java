package financemaster.rest.controller;

import financemaster.persistence.entity.User;
import financemaster.persistence.repository.UserRepository;
import financemaster.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// DTOs (Data Transfer Objects) für die Eingabedaten
// Records eignen sich hier perfekt für unveränderliche Datenpakete.
record LoginRequest(String email, String password) {}
record RegisterRequest(String name, String email, String password) {}

/**
 * Controller für Authentifizierung (Login, Logout, Registrierung)
 * und Abfrage des aktuellen Benutzerstatus.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // Konstruktor-Injektion der benötigten Services und Repositories
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Meldet einen Benutzer an.
     * Erstellt eine HTTP-Session, wenn die Daten korrekt sind.
     */
    @PostMapping("/login")
    public User login(@RequestBody LoginRequest req, HttpServletRequest request) {
        return authService.login(req.email(), req.password(), request);
    }

    /**
     * Registriert einen neuen Benutzer und meldet ihn direkt im Anschluss an.
     */
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest req, HttpServletRequest request) {
        authService.register(req.name(), req.email(), req.password());
        // Automatischer Login nach erfolgreicher Registrierung
        return authService.login(req.email(), req.password(), request);
    }

    /**
     * Prüft, ob der aktuelle Benutzer eine aktive Sitzung hat.
     * Wird vom Frontend beim Start aufgerufen, um den Login-Status zu prüfen.
     */
    @GetMapping("/me")
    public User me(HttpSession session) {
        // Die userId wird direkt aus der Session gelesen
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            // Keine Session vorhanden -> 401 Unauthorized
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        
        // Benutzer aus der Datenbank laden
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Beendet die Sitzung des Benutzers (Logout).
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }
}