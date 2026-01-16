package financemaster.service;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import financemaster.dto.TransactionDto;
import financemaster.persistence.entity.*;
import financemaster.persistence.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepo;
    private final CategoryRepository categoryRepo;

    public TransactionService(TransactionRepository t, CategoryRepository c) {
        this.transactionRepo = t;
        this.categoryRepo = c;
    }

    /**
     * Ruft eine Liste von Transaktionen ab, gefiltert nach User, Kategorie und Datumsbereich.
     * Nutzt JPQL im Repository für die Filterlogik.
     */
    public List<Transaction> getFilteredTransactions(Long userId, Long catId, LocalDate from, LocalDate to) {
        return transactionRepo.findAllByFilter(userId, catId, from, to);
    }

    /**
     * Berechnet die Summen für Einnahmen, Ausgaben und den aktuellen Kontostand.
     * Gibt eine Map zurück, um flexibel verschiedene Kennzahlen an das Frontend zu liefern.
     */
    public Map<String, Object> getFinancialSummary(Long userId, Long catId, LocalDate from, LocalDate to) {
        Map<String, Double> res = transactionRepo.calculateFinancialSummary(userId, catId, from, to);
        
        // Null-Safe Zugriff auf die aggregierten Werte, um NullPointerExceptions zu vermeiden,
        // falls die Datenbank NULL zurückliefert (z.B. wenn keine Transaktionen vorhanden sind).
        double inc = res != null && res.get("inc") != null ? res.get("inc") : 0.0;
        double exp = res != null && res.get("exp") != null ? res.get("exp") : 0.0;
        
        return Map.of("totalIncome", inc, "totalExpense", exp, "balance", inc - exp);
    }

    /**
     * Erstellt eine neue Transaktion.
     * Prüft dabei explizit, ob die gewählte Kategorie existiert und dem Nutzer gehört.
     */
    @Transactional
    public Transaction createTransaction(Long userId, TransactionDto req) {
        // Validierung: Benutzer darf nur Kategorien verwenden, die ihm gehören.
        Category cat = categoryRepo.findById(req.categoryId())
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorie nicht gefunden oder Zugriff verweigert"));

        Transaction t = new Transaction();
        t.setDescription(req.description());
        t.setAmount(req.amount());
        
        // Validierung des Transaktionstyps:
        // Konvertiert den Eingabe-String in das Enum. Bei ungültigen Werten (z.B. Tippfehler) 
        // wird eine verständliche Fehlermeldung (400 Bad Request) zurückgegeben.
        try {
            t.setType(TransactionType.valueOf(req.type().toUpperCase())); 
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültiger Transaktionstyp: " + req.type() + ". Erlaubt sind: INCOME, EXPENSE");
        }

        // Setzt das Datum auf heute, falls keines übermittelt wurde
        t.setDate(req.date() != null ? req.date() : LocalDate.now());
        t.setCategory(cat);
        
        // Explizites Setzen des Users für die Daten-Integrität und Sicherheit
        User u = new User(); 
        u.setId(userId); 
        t.setUser(u);
        
        return transactionRepo.save(t);
    }

    /**
     * Löscht eine Transaktion sicher.
     * Führt die Löschung nur aus, wenn die Transaktion tatsächlich dem übergebenen User gehört.
     */
    public void deleteTransactionSafe(Long id, Long userId) {
        transactionRepo.findById(id)
                       .filter(t -> t.getUser().getId().equals(userId))
                       .ifPresent(transactionRepo::delete);
    }
}