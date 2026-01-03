package com.financemaster.rest_service.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financemaster.rest_service.persistence.entity.Category;
import com.financemaster.rest_service.persistence.entity.Transaction;
import com.financemaster.rest_service.persistence.entity.TransactionType;
import com.financemaster.rest_service.persistence.repository.TransactionRepository;
import com.financemaster.rest_service.rest.BalanceSummary;
import com.financemaster.rest_service.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class SummaryControllerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SummaryController controller;

    private List<Transaction> sampleTxs() {
        Category food = new Category();
        food.setId(1L);
        food.setName("Food");

        Category transport = new Category();
        transport.setId(2L);
        transport.setName("Transport");

        Transaction t1 = new Transaction();
        t1.setType(TransactionType.INCOME);
        t1.setAmount(100.0);
        t1.setDescription("Salary");
        t1.setDate(LocalDate.parse("2025-01-01"));
        t1.setCategory(food);

        Transaction t2 = new Transaction();
        t2.setType(TransactionType.EXPENSE);
        t2.setAmount(40.0);
        t2.setDescription("Groceries");
        t2.setDate(LocalDate.parse("2025-01-01"));
        t2.setCategory(food);

        Transaction t3 = new Transaction();
        t3.setType(TransactionType.EXPENSE);
        t3.setAmount(20.0);
        t3.setDescription("Bus");
        t3.setDate(LocalDate.parse("2025-01-02"));
        t3.setCategory(transport);

        return List.of(t1, t2, t3);
    }

    @Test
    void getBalance_noFilters_sumsIncomeAndExpense() {
        when(authService.requireSessionUserId(request)).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(sampleTxs());

        BalanceSummary summary = controller.getBalance(request, null, null, null);

        assertEquals(100.0, summary.getIncome());
        assertEquals(60.0, summary.getExpense());
        assertEquals(40.0, summary.getBalance());
    }

    @Test
    void getBalance_withCategoryFilter_appliesFilter() {
        when(authService.requireSessionUserId(request)).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(sampleTxs());

        BalanceSummary summary = controller.getBalance(request, 1L, null, null);

        assertEquals(100.0, summary.getIncome());
        assertEquals(40.0, summary.getExpense());
        assertEquals(60.0, summary.getBalance());
    }

    @Test
    void getBalance_withDateRange_appliesFilter() {
        when(authService.requireSessionUserId(request)).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(sampleTxs());

        BalanceSummary summary = controller.getBalance(request, null, "2025-01-02", "2025-01-02");

        assertEquals(0.0, summary.getIncome());
        assertEquals(20.0, summary.getExpense());
        assertEquals(-20.0, summary.getBalance());
    }

    @Test
    void summaryByCategory_groupsAndSums() {
        when(authService.requireSessionUserId(request)).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(sampleTxs());

        List<Map<String, Object>> result = controller.getSummaryByCategory(request, null, null);

        assertEquals(2, result.size());
        // Find Food summary
        Map<String, Object> food = result.stream()
                .filter(m -> "Food".equals(m.get("category")))
                .findFirst().orElseThrow();
        assertEquals(100.0, (Double) food.get("income"));
        assertEquals(40.0, (Double) food.get("expense"));

        Map<String, Object> transport = result.stream()
                .filter(m -> "Transport".equals(m.get("category")))
                .findFirst().orElseThrow();
        assertEquals(0.0, (Double) transport.get("income"));
        assertEquals(20.0, (Double) transport.get("expense"));
    }

    @Test
    void summaryByDate_groupsByDay() {
        when(authService.requireSessionUserId(request)).thenReturn(1L);
        when(transactionRepository.findByUserId(1L)).thenReturn(sampleTxs());

        List<Map<String, Object>> result = controller.getSummaryByDate(request, null, null);

        assertEquals(2, result.size());
        Map<String, Object> day1 = result.stream()
                .filter(m -> "2025-01-01".equals(m.get("date")))
                .findFirst().orElseThrow();
        assertEquals(100.0, (Double) day1.get("income"));
        assertEquals(40.0, (Double) day1.get("expense"));
    }
}
