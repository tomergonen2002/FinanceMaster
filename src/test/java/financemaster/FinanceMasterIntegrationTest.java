package financemaster;

import com.fasterxml.jackson.databind.ObjectMapper;
import financemaster.dto.TransactionDto;
import financemaster.persistence.entity.Category;
import financemaster.persistence.entity.Transaction;
import financemaster.persistence.entity.User;
import financemaster.persistence.repository.CategoryRepository;
import financemaster.persistence.repository.TransactionRepository;
import financemaster.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstests für die gesamte Backend-Applikation.
 * Prüft das Zusammenspiel von Controller, Service, Security und Datenbank.
 * Durch @Transactional wird die Datenbank nach jedem Testfall zurückgesetzt (Rollback),
 * sodass jeder Test mit einer sauberen Umgebung startet.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FinanceMasterIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private CategoryRepository catRepo;
    @Autowired private TransactionRepository txRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private ObjectMapper objectMapper;

    private User testUser;
    private MockHttpSession session;

    /**
     * Vorbereitung vor JEDEM Test:
     * 1. Erstellt einen frischen Test-User in der Datenbank.
     * 2. Simuliert eine aktive Session für diesen User (Login-Zustand).
     */
    @BeforeEach
    void setUp() {
        User u = new User();
        u.setName("Test User");
        u.setEmail("test@test.de");
        u.setPassword(encoder.encode("password123"));
        testUser = userRepo.save(u);

        session = new MockHttpSession();
        session.setAttribute("userId", testUser.getId());
    }

    /*
     #########################################################################
     #                    TEIL 1: AUTHENTIFIZIERUNG                          #
     #########################################################################
     */

    /**
     * Testet den Registrierungs-Prozess.
     * Erwartung: API liefert HTTP 200 und das erstellte User-Objekt zurück.
     */
    @Test
    void registerUser_ShouldSucceed() throws Exception {
        String json = """
            { "name": "Neu", "email": "neu@test.de", "password": "secure" }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("neu@test.de")));
    }

    /**
     * Testet den Login mit korrekten Zugangsdaten.
     * Erwartung: API liefert HTTP 200 und bestätigt den Namen des Users.
     */
    @Test
    void login_WithCorrectCredentials_ShouldSucceed() throws Exception {
        String json = """
            { "email": "test@test.de", "password": "password123" }
        """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test User")));
    }

    /**
     * Sicherheits-Test: Zugriff auf geschützte Ressourcen ohne Login.
     * Szenario: Ein Aufruf ohne Session-Cookie.
     * Erwartung: HTTP 401 Unauthorized.
     */
    @Test
    void accessProtectedResource_WithoutLogin_ShouldFail() throws Exception {
        mockMvc.perform(get("/transactions"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Testet den Session-Check Endpunkt (/auth/me).
     * Erwartung: Liefert den aktuell eingeloggten User zurück (Id muss übereinstimmen).
     */
    @Test
    void meEndpoint_ShouldReturnCurrentUser() throws Exception {
        mockMvc.perform(get("/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())));
    }

    /*
     #########################################################################
     #               TEIL 2: KATEGORIEN & DATEN-ISOLATION                    #
     #########################################################################
     */

    /**
     * Testet das Erstellen einer neuen Kategorie.
     * Erwartung: Kategorie wird gespeichert und via API zurückgegeben.
     */
    @Test
    void createCategory_ShouldSucceed() throws Exception {
        Category cat = new Category();
        cat.setName("Urlaub");
        
        mockMvc.perform(post("/categories")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Urlaub")));
    }

    /**
     * WICHTIGER Multi-User Test: Daten-Isolation.
     * Szenario: Ein anderer User hat Kategorien erstellt.
     * Erwartung: Der aktuelle User darf diese NICHT sehen (leere Liste).
     */
    @Test
    void getCategories_ShouldNotReturnForeignData() throws Exception {
        User other = userRepo.save(createUser("other@test.de"));
        createCategory("Geheim", other);

        mockMvc.perform(get("/categories").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Testet das Löschen einer eigenen Kategorie.
     * Erwartung: HTTP 200 und Eintrag ist aus der Datenbank verschwunden.
     */
    @Test
    void deleteCategory_OwnCategory_ShouldSucceed() throws Exception {
        Category cat = createCategory("WegDamit", testUser);

        mockMvc.perform(delete("/categories/" + cat.getId()).session(session))
                .andExpect(status().isOk());

        assertTrue(catRepo.findById(cat.getId()).isEmpty());
    }

    /**
     * Sicherheits-Test: Löschen fremder Daten.
     * Szenario: Versuch, eine Kategorie-ID eines anderen Users zu löschen.
     * Erwartung: HTTP 200 (Idempotenz/Sicherheit durch Obscurity), aber der Eintrag
     * MUSS in der Datenbank erhalten bleiben.
     */
    @Test
    void deleteCategory_ForeignCategory_ShouldNotDelete() throws Exception {
        User other = userRepo.save(createUser("opfer@test.de"));
        Category foreignCat = createCategory("BleibtDa", other);

        mockMvc.perform(delete("/categories/" + foreignCat.getId()).session(session))
                .andExpect(status().isOk());

        assertTrue(catRepo.findById(foreignCat.getId()).isPresent());
    }

    /*
     #########################################################################
     #           TEIL 3: TRANSAKTIONEN & BUSINESS LOGIK                      #
     #########################################################################
     */

    /**
     * Testet das Erstellen einer Einnahme.
     * Prüft, ob das Mapping vom DTO zur Entity funktioniert.
     */
    @Test
    void createTransaction_Income_ShouldSucceed() throws Exception {
        Category cat = createCategory("Job", testUser);
        TransactionDto req = new TransactionDto(
            "Gehalt", 2000.0, "INCOME", cat.getId(), LocalDate.now()
        );

        mockMvc.perform(post("/transactions")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(2000.0)))
                .andExpect(jsonPath("$.type", is("INCOME")));
    }

    /**
     * Sicherheits-Test: Buchung auf fremde Kategorie.
     * Szenario: User versucht, eine Transaktion mit der Kategorie-ID eines anderen Users zu erstellen.
     * Erwartung: HTTP 404 Not Found (Service findet die Kategorie für diesen User nicht).
     */
    @Test
    void createTransaction_OnForeignCategory_ShouldFail() throws Exception {
        User other = userRepo.save(createUser("fremd@test.de"));
        Category foreignCat = createCategory("Fremd", other);

        TransactionDto req = new TransactionDto(
            "Betrug", 100.0, "EXPENSE", foreignCat.getId(), LocalDate.now()
        );

        mockMvc.perform(post("/transactions")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    /**
     * Validierungs-Test.
     * Szenario: Senden eines negativen Betrags.
     * Erwartung: HTTP 400 Bad Request (durch @Positive Annotation im DTO).
     */
    @Test
    void createTransaction_WithNegativeAmount_ShouldFail() throws Exception {
        String json = """
            { "description": "Fail", "amount": -100, "type": "EXPENSE", "categoryId": 1 }
        """;

        mockMvc.perform(post("/transactions")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * Logik-Test: Finanz-Zusammenfassung.
     * Szenario: 1000 Einnahme, 200 Ausgabe.
     * Erwartung: Bilanz = 800.
     */
    @Test
    void getSummary_ShouldCalculateCorrectBalance() throws Exception {
        createTx(1000.0, "INCOME", "2025-01-01");
        createTx(200.0, "EXPENSE", "2025-01-02");

        mockMvc.perform(get("/transactions/summary/balance").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome", is(1000.0)))
                .andExpect(jsonPath("$.totalExpense", is(200.0)))
                .andExpect(jsonPath("$.balance", is(800.0)));
    }

    /**
     * Filter-Test: Zeitraumeinschränkung.
     * Szenario: Eine Buchung im Januar, eine im Februar. Filter auf Januar gesetzt.
     * Erwartung: Nur die Januar-Buchung wird zurückgegeben.
     */
    @Test
    void getTransactions_FilterByDate_ShouldReturnCorrectSubset() throws Exception {
        createTx(100.0, "EXPENSE", "2025-01-01");
        createTx(100.0, "EXPENSE", "2025-02-01");

        mockMvc.perform(get("/transactions")
                .session(session)
                .param("from", "2025-01-01")
                .param("to", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date", is("2025-01-01")));
    }

    /**
     * Testet das Löschen einer eigenen Transaktion.
     * Erwartung: Eintrag wird aus der DB entfernt.
     */
    @Test
    void deleteTransaction_OwnTransaction_ShouldSucceed() throws Exception {
        Transaction tx = createTx(50.0, "EXPENSE", "2025-01-01");

        mockMvc.perform(delete("/transactions/" + tx.getId()).session(session))
                .andExpect(status().isOk());

        assertTrue(txRepo.findById(tx.getId()).isEmpty());
    }

    /**
     * Sicherheits-Test: Löschen fremder Transaktionen.
     * Szenario: Versuch, eine fremde Transaktions-ID zu löschen.
     * Erwartung: HTTP 200, aber Eintrag bleibt bestehen.
     */
    @Test
    void deleteTransaction_ForeignTransaction_ShouldNotDelete() throws Exception {
        User other = userRepo.save(createUser("victim@test.de"));
        Category c = createCategory("X", other);
        
        Transaction foreignTx = new Transaction();
        foreignTx.setUser(other);
        foreignTx.setCategory(c);
        foreignTx.setAmount(10.0);
        txRepo.save(foreignTx);

        mockMvc.perform(delete("/transactions/" + foreignTx.getId()).session(session))
                .andExpect(status().isOk());

        assertTrue(txRepo.findById(foreignTx.getId()).isPresent());
    }

    /**
     * Robustheits-Test: Leere Filter-Parameter.
     * Szenario: Frontend sendet "?from=&to=".
     * Erwartung: Server stürzt nicht ab (kein 500er), sondern ignoriert die leeren Filter.
     */
    @Test
    void getTransactions_WithEmptyFilterParams_ShouldNotCrash() throws Exception {
        mockMvc.perform(get("/transactions")
                .session(session)
                .param("from", "")
                .param("to", ""))
                .andExpect(status().isOk());
    }

    /*
     #########################################################################
     #                     HELPER METHODEN                                   #
     #        (Erzeugen Testdaten direkt in der DB ohne API-Aufruf)          #
     #########################################################################
     */

    private User createUser(String email) {
        User u = new User();
        u.setName("User");
        u.setEmail(email);
        u.setPassword(encoder.encode("pw"));
        return u;
    }

    private Category createCategory(String name, User owner) {
        Category c = new Category();
        c.setName(name);
        c.setUser(owner);
        return catRepo.save(c);
    }

    private Transaction createTx(double amount, String type, String date) {
        Category c = createCategory("TestCat", testUser);
        Transaction t = new Transaction();
        t.setUser(testUser);
        t.setCategory(c);
        t.setAmount(amount);
        t.setType(financemaster.persistence.entity.TransactionType.valueOf(type));
        t.setDate(LocalDate.parse(date));
        return txRepo.save(t);
    }
}