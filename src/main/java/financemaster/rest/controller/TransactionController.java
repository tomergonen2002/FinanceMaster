package financemaster.rest.controller;

import financemaster.dto.TransactionDto;
import financemaster.persistence.entity.Transaction;
import financemaster.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * REST-Controller für Transaktionen.
 * Behandelt alle HTTP-Anfragen für Einnahmen und Ausgaben.
 * Stellt sicher, dass User nur auf ihre eigenen Daten zugreifen (Session-Check).
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    /**
     * Extrahiert die User-ID aus der Session.
     * Schützt Endpunkte vor unbefugtem Zugriff (gibt 401 zurück, wenn nicht eingeloggt).
     */
    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bitte anmelden.");
        }
        return userId;
    }

    /**
     * Konvertiert Datums-Strings sicher in LocalDate Objekte.
     * Verhindert Server-Fehler bei leeren oder falschen Formaten.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültiges Datumsformat.");
        }
    }

    /**
     * Liefert eine Liste aller Transaktionen des angemeldeten Nutzers.
     * Unterstützt Filterung nach Kategorie und Datum.
     */
    @GetMapping
    public List<Transaction> get(HttpSession session,
                                 @RequestParam(required = false) Long categoryId,
                                 @RequestParam(required = false) String from,
                                 @RequestParam(required = false) String to) {
        Long userId = getUserIdOrThrow(session);
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);

        return service.getFilteredTransactions(userId, categoryId, fromDate, toDate);
    }

    /**
     * Liefert zusammengefasste Finanzdaten (Einnahmen, Ausgaben, Bilanz).
     * Wird für die Anzeige im Dashboard verwendet.
     */
    @GetMapping("/summary/balance")
    public Map<String, Object> getSummary(HttpSession session,
                                          @RequestParam(required = false) Long categoryId,
                                          @RequestParam(required = false) String from,
                                          @RequestParam(required = false) String to) {
        Long userId = getUserIdOrThrow(session);
        return service.getFinancialSummary(userId, categoryId, parseDate(from), parseDate(to));
    }

    /**
     * Erstellt eine neue Transaktion.
     * Validiert den Input (@Valid) und prüft im Service die Kategorie-Berechtigung.
     */
    @PostMapping
    public Transaction create(@Valid @RequestBody TransactionDto req, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        return service.createTransaction(userId, req);
    }

    /**
     * Löscht eine Transaktion anhand ihrer ID.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpSession session) {
        Long userId = getUserIdOrThrow(session);
        service.deleteTransactionSafe(id, userId);
    }
}