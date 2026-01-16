package financemaster.service;

import financemaster.persistence.entity.User;
import financemaster.persistence.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service für die Benutzerverwaltung und Authentifizierung.
 * Kapselt die Logik für Login, Registrierung und Session-Management.
 * WICHTIG: Wir nutzen hier Stateful Session Management (HttpSession).
 */
@Service
public class AuthService {
    
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /**
     * Authentifiziert einen Nutzer und erstellt eine neue Session.
     * * @param email Die E-Mail des Nutzers (Case-Insensitive behandelt)
     * @param password Das Klartext-Passwort vom Frontend
     * @param req Der aktuelle HTTP-Request (nötig für Session-Zugriff)
     * @return Der eingeloggte User (ohne Passwort-Hash im JSON durch Entity-Annotationen)
     * @throws ResponseStatusException (401) wenn Login fehlschlägt
     */
    public User login(String email, String password, HttpServletRequest req) {
        User user = repo.findByEmailIgnoreCase(email)
            // Prüft das eingegebene Passwort gegen den Hash in der DB (BCrypt)
            .filter(u -> encoder.matches(password, u.getPassword()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-Mail oder Passwort falsch"));
        
        // Session erstellen: "true" bedeutet, erstelle eine neue, wenn keine existiert.
        // Das Speichern der ID in der Session markiert den User als "eingeloggt".
        req.getSession(true).setAttribute("userId", user.getId());
        
        return user;
    }

    /**
     * Meldet den Nutzer ab, indem die Session serverseitig ungültig gemacht wird.
     */
    public void logout(HttpServletRequest req) {
        HttpSession s = req.getSession(false); // false = Hole nur Session, wenn eine existiert
        if (s != null) {
            s.invalidate(); // Löscht alle Daten der Session (Logout)
        }
    }

    /**
     * Hilfsmethode zur Absicherung interner Logik.
     * Prüft, ob eine gültige Session existiert und liefert die User-ID zurück.
     * Wird genutzt, wenn ein Service-Call zwingend einen User braucht.
     */
    public Long requireSessionUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sitzung abgelaufen oder nicht vorhanden");
        }
        return (Long) s.getAttribute("userId");
    }

    /**
     * Registriert einen neuen Benutzer.
     * Wirft einen Fehler (409 Conflict), wenn die E-Mail bereits existiert.
     */
    public User register(String name, String email, String password) {
        if (repo.findByEmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Diese E-Mail-Adresse wird bereits verwendet.");
        }
        
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        // Passwort wird niemals im Klartext gespeichert, wir hashen es hier.
        u.setPassword(encoder.encode(password));
        
        return repo.save(u);
    }
}