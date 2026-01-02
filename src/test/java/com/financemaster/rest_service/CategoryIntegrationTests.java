package com.financemaster.rest_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.persistence.entity.Category;
import com.financemaster.rest_service.persistence.entity.Transaction;
import com.financemaster.rest_service.persistence.repository.UserRepository;
import com.financemaster.rest_service.persistence.repository.CategoryRepository;
import com.financemaster.rest_service.persistence.repository.TransactionRepository;

import java.time.LocalDate;

/**
 * Integration tests for Category CRUD use cases
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private org.springframework.mock.web.MockHttpSession loginAsTestUser() throws Exception {
        userRepository.deleteAll();
        User user = new User();
        user.setName("CategoryTester");
        user.setEmail("cattest@example.com");
        user.setPassword(encoder.encode("secret"));
        userRepository.save(user);

        var resp = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"cattest@example.com\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return (org.springframework.mock.web.MockHttpSession) resp.getRequest().getSession(false);
    }

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createCategorySucceeds() throws Exception {
        // Neue Kategorie wird erfolgreich erstellt
        var session = loginAsTestUser();
        var userResp = mockMvc.perform(get("/auth/me").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Long userId = com.fasterxml.jackson.databind.ObjectMapper.class.newInstance()
                .readTree(userResp).get("id").asLong();

        mockMvc.perform(post("/categories")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Groceries\",\"description\":\"Food shopping\",\"user\":{\"id\":" + userId + "}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Groceries"))
                .andExpect(jsonPath("$.description").value("Food shopping"));
    }

    @Test
    void getCategoriesForUserSucceeds() throws Exception {
        // Alle Kategorien eines Users werden abgerufen
        var session = loginAsTestUser();
        var userResp = mockMvc.perform(get("/auth/me").session(session))
                .andReturn().getResponse().getContentAsString();
        
        Long userId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(userResp).get("id").asLong();

        User user = userRepository.findById(userId).orElseThrow();

        // Create categories
        Category cat1 = new Category();
        cat1.setName("Transport");
        cat1.setDescription("Bus, Train");
        cat1.setUser(user);
        categoryRepository.save(cat1);

        Category cat2 = new Category();
        cat2.setName("Entertainment");
        cat2.setDescription("Movies, Games");
        cat2.setUser(user);
        categoryRepository.save(cat2);

        mockMvc.perform(get("/categories?userId=" + userId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void deleteCategoryWithoutTransactionsSucceeds() throws Exception {
        // Kategorie ohne Transaktionen wird gelöscht
        var session = loginAsTestUser();
        var userResp = mockMvc.perform(get("/auth/me").session(session))
                .andReturn().getResponse().getContentAsString();
        
        Long userId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(userResp).get("id").asLong();

        User user = userRepository.findById(userId).orElseThrow();

        Category cat = new Category();
        cat.setName("ToDelete");
        cat.setDescription("Will be deleted");
        cat.setUser(user);
        cat = categoryRepository.save(cat);

        mockMvc.perform(delete("/categories/" + cat.getId()).session(session))
                .andExpect(status().isOk());

        // Verify deletion
        assert categoryRepository.findById(cat.getId()).isEmpty();
    }

    @Test
    void deleteCategoryWithTransactionsFails() throws Exception {
        // Kategorie mit Transaktionen kann nicht gelöscht werden
        var session = loginAsTestUser();
        var userResp = mockMvc.perform(get("/auth/me").session(session))
                .andReturn().getResponse().getContentAsString();
        
        Long userId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(userResp).get("id").asLong();

        User user = userRepository.findById(userId).orElseThrow();

        Category cat = new Category();
        cat.setName("WithTransactions");
        cat.setDescription("Has transactions");
        cat.setUser(user);
        cat = categoryRepository.save(cat);

        Transaction tx = new Transaction();
        tx.setType(com.financemaster.rest_service.persistence.entity.TransactionType.EXPENSE);
        tx.setAmount(50.0);
        tx.setDescription("Test expense");
        tx.setDate(LocalDate.now());
        tx.setCategory(cat);
        tx.setUser(user);
        transactionRepository.save(tx);

        mockMvc.perform(delete("/categories/" + cat.getId()).session(session))
                .andExpect(status().isConflict());

        // Verify category still exists
        assert categoryRepository.findById(cat.getId()).isPresent();
    }

    @Test
    void deleteAllCategoriesForUserSucceeds() throws Exception {
        // Alle Kategorien eines Users werden gelöscht
        var session = loginAsTestUser();
        var userResp = mockMvc.perform(get("/auth/me").session(session))
                .andReturn().getResponse().getContentAsString();
        
        Long userId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(userResp).get("id").asLong();

        User user = userRepository.findById(userId).orElseThrow();

        // Create multiple categories
        Category cat1 = new Category();
        cat1.setName("Cat1");
        cat1.setUser(user);
        categoryRepository.save(cat1);

        Category cat2 = new Category();
        cat2.setName("Cat2");
        cat2.setUser(user);
        categoryRepository.save(cat2);

        mockMvc.perform(delete("/categories?userId=" + userId).session(session))
                .andExpect(status().isOk());

        // Verify all deleted
        assert categoryRepository.findByUserId(userId).isEmpty();
    }

    @Test
    void userCannotAccessOtherUserCategories() throws Exception {
        // User kann keine fremden Kategorien abrufen (Isolation)
        // Create two users
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        user1.setPassword(encoder.encode("pass1"));
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        user2.setPassword(encoder.encode("pass2"));
        user2 = userRepository.save(user2);

        // Create category for user2
        Category cat = new Category();
        cat.setName("User2Category");
        cat.setUser(user2);
        categoryRepository.save(cat);

        // Login as user1
        var resp = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user1@example.com\",\"password\":\"pass1\"}"))
                .andExpect(status().isOk())
                .andReturn();
        var session = (org.springframework.mock.web.MockHttpSession) resp.getRequest().getSession(false);

        // Try to get user2's categories
        mockMvc.perform(get("/categories?userId=" + user2.getId()).session(session))
                .andExpect(status().isForbidden());
    }
}
