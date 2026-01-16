package financemaster.persistence.repository;

import financemaster.persistence.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

/**
 * Schnittstelle zur Datenbank für Transaktionen.
 * Nutzt Spring Data JPA für Standard-Methoden (save, delete, findById)
 * und benutzerdefinierte JPQL-Queries für komplexe Filter und Auswertungen.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Sucht Transaktionen mit flexiblen Filtern.
     * * Erläuterung der JPQL-Logik:
     * Die Bedingung "(:param IS NULL OR t.feld = :param)" wirkt wie ein optionaler Schalter.
     * - Wenn der Parameter NULL ist, ist der erste Teil wahr -> Die Bedingung wird ignoriert (Filter inaktiv).
     * - Wenn der Parameter gesetzt ist, muss er mit dem Datenbankfeld übereinstimmen (Filter aktiv).
     * * "cast(... as date)" stellt sicher, dass der Datenbank-Datentyp korrekt interpretiert wird,
     * da JPQL manchmal Datumsvergleiche unterschiedlich handhabt.
     * * @param userId Zwingend: Der Benutzer, dem die Daten gehören.
     * @param catId Optional: Filter nach Kategorie-ID.
     * @param from Optional: Startdatum (inklusiv).
     * @param to Optional: Enddatum (inklusiv).
     * @return Eine Liste der passenden Transaktionen, sortiert nach Datum (neueste zuerst).
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:catId IS NULL OR t.category.id = :catId) " +
           "AND (cast(:from as date) IS NULL OR t.date >= :from) " +
           "AND (cast(:to as date) IS NULL OR t.date <= :to) " +
           "ORDER BY t.date DESC")
    List<Transaction> findAllByFilter(@Param("userId") Long userId, 
                                      @Param("catId") Long catId, 
                                      @Param("from") LocalDate from, 
                                      @Param("to") LocalDate to);

    /**
     * Berechnet die Finanz-Zusammenfassung direkt in der Datenbank.
     * * Warum hier und nicht in Java?
     * Es ist viel performanter, die Datenbank rechnen zu lassen (Aggregation), 
     * als tausende Transaktionen in den Speicher zu laden und in einer Schleife zu addieren.
     * * Funktionsweise:
     * - SUM(CASE WHEN...): Addiert nur Beträge, wenn der Typ stimmt (INCOME oder EXPENSE).
     * - Das Ergebnis ist eine Map, z.B. { "inc": 1500.0, "exp": 450.50 }.
     * * Die gleichen Filter wie oben werden angewendet, damit die Summen zum 
     * angezeigten Zeitraum/Kategorie passen.
     */
    @Query("SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as inc, " +
           "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as exp " +
           "FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:catId IS NULL OR t.category.id = :catId) " +
           "AND (cast(:from as date) IS NULL OR t.date >= :from) " +
           "AND (cast(:to as date) IS NULL OR t.date <= :to)")
    Map<String, Double> calculateFinancialSummary(@Param("userId") Long userId, 
                                                  @Param("catId") Long catId, 
                                                  @Param("from") LocalDate from,
                                                  @Param("to") LocalDate to);
}