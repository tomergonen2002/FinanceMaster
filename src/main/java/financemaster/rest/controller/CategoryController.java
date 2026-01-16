package financemaster.rest.controller;

import financemaster.persistence.entity.Category;
import financemaster.persistence.entity.User;
import financemaster.persistence.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST-Schnittstelle für Kategorien.
 * Jeder Benutzer verwaltet seine eigenen Kategorien.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryRepository repo;
    
    public CategoryController(CategoryRepository repo) { 
        this.repo = repo;
    }

    /**
     * Validiert die Session und gibt die User-ID zurück.
     */
    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userId;
    }

    /**
     * Gibt alle Kategorien des angemeldeten Benutzers zurück.
     */
    @GetMapping
    public List<Category> get(HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        return repo.findByUserId(userId);
    }

    /**
     * Erstellt eine neue Kategorie und verknüpft sie mit dem aktuellen User.
     */
    @PostMapping
    public Category create(@RequestBody Category cat, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        
        // Verknüpfung zum User herstellen
        User u = new User();
        u.setId(userId);
        cat.setUser(u);
        
        return repo.save(cat);
    }

    /**
     * Löscht eine Kategorie, sofern sie dem Benutzer gehört.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        
        // Zuerst prüfen, ob die Kategorie existiert und dem User gehört
        repo.findById(id)
            .filter(c -> c.getUser().getId().equals(userId))
            .ifPresent(repo::delete);
    }
}