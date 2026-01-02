package com.financemaster.rest_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SummaryIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TransactionRepository transactionRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Map<String, Object> setupTestData() throws Exception {
        User user = new User();
        user.setName("Tester");
        user.setEmail("sum@example.com");
        user.setPassword(encoder.encode("secret"));
        user = userRepository.save(user);

        Category foodCat = new Category();
        foodCat.setName("Food");
        foodCat.setDescription("Groceries");
        foodCat.setUser(user);
        foodCat = categoryRepository.save(foodCat);

        Category transportCat = new Category();
        transportCat.setName("Transport");
        transportCat.setDescription("Bus, train");
        transportCat.setUser(user);
        transportCat = categoryRepository.save(transportCat);

        Transaction t1 = new Transaction();
        t1.setType(com.financemaster.rest_service.persistence.entity.TransactionType.INCOME);
        t1.setAmount(2500.0);
        t1.setDescription("Salary");
        t1.setDate(LocalDate.parse("2025-12-01"));
        t1.setCategory(foodCat);
        t1.setUser(user);
        transactionRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setType(com.financemaster.rest_service.persistence.entity.TransactionType.EXPENSE);
        t2.setAmount(50.0);
        t2.setDescription("Groceries");
        t2.setDate(LocalDate.parse("2025-12-02"));
        t2.setCategory(foodCat);
        t2.setUser(user);
        transactionRepository.save(t2);

        Transaction t3 = new Transaction();
        t3.setType(com.financemaster.rest_service.persistence.entity.TransactionType.EXPENSE);
        t3.setAmount(30.0);
        t3.setDescription("Bus ticket");
        t3.setDate(LocalDate.parse("2025-12-05"));
        t3.setCategory(transportCat);
        t3.setUser(user);
        transactionRepository.save(t3);

        Transaction t4 = new Transaction();
        t4.setType(com.financemaster.rest_service.persistence.entity.TransactionType.INCOME);
        t4.setAmount(500.0);
        t4.setDescription("Freelance");
        t4.setDate(LocalDate.parse("2025-11-15"));
        t4.setCategory(foodCat);
        t4.setUser(user);
        transactionRepository.save(t4);

        var resp = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"email\":\"sum@example.com\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andReturn();
        
        Map<String, Object> result = new HashMap<>();
        result.put("session", resp.getRequest().getSession(false));
        result.put("foodCatId", foodCat.getId());
        return result;
    }

    @Test
    void balanceSummaryWorks() throws Exception {
        var data = setupTestData();
        var session = data.get("session");
        mockMvc.perform(get("/summary/balance").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(3000.0))
                .andExpect(jsonPath("$.expense").value(80.0))
                .andExpect(jsonPath("$.balance").value(2920.0));
    }

    @Test
    void balanceSummaryWithDateRangeFilter() throws Exception {
        var data = setupTestData();
        var session = data.get("session");
        mockMvc.perform(get("/summary/balance")
                .session((org.springframework.mock.web.MockHttpSession) session)
                .param("from", "2025-12-01")
                .param("to", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(2500.0))
                .andExpect(jsonPath("$.expense").value(80.0))
                .andExpect(jsonPath("$.balance").value(2420.0));
    }

    @Test
    void balanceSummaryWithCategoryFilter() throws Exception {
        var data = setupTestData();
        var session = data.get("session");
        Long foodCatId = (Long) data.get("foodCatId");

        mockMvc.perform(get("/summary/balance")
                .session((org.springframework.mock.web.MockHttpSession) session)
                .param("categoryId", String.valueOf(foodCatId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(3000.0))
                .andExpect(jsonPath("$.expense").value(50.0))
                .andExpect(jsonPath("$.balance").value(2950.0));
    }

    @Test
    void summaryByCategoryShowsBreakdown() throws Exception {
        var data = setupTestData();
        var session = data.get("session");
        mockMvc.perform(get("/summary/by-category").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void summaryByDateShowsMonthlyBreakdown() throws Exception {
        var data = setupTestData();
        var session = data.get("session");
        mockMvc.perform(get("/summary/by-date").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void summaryByDateWithDateRangeFilter() throws Exception {
        var data = setupTestData();
        var session = data.get("session");
        mockMvc.perform(get("/summary/by-date")
                .session((org.springframework.mock.web.MockHttpSession) session)
                .param("from", "2025-12-01")
                .param("to", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
