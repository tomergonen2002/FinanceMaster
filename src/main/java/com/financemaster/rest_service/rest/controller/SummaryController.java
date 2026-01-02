package com.financemaster.rest_service.rest.controller;

import com.financemaster.rest_service.persistence.entity.Transaction;
import com.financemaster.rest_service.persistence.entity.TransactionType;
import com.financemaster.rest_service.persistence.repository.TransactionRepository;
import com.financemaster.rest_service.rest.BalanceSummary;
import com.financemaster.rest_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SummaryController {

    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    public SummaryController(TransactionRepository transactionRepository, AuthService authService) {
        this.transactionRepository = transactionRepository;
        this.authService = authService;
    }

    @GetMapping("/summary/balance")
    public BalanceSummary getBalance(HttpServletRequest request,
                                     @RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) String from,
                                     @RequestParam(required = false) String to) {
        Long userId = authService.requireSessionUserId(request);
        var txs = filteredTransactions(userId, categoryId, from, to);
        double[] sums = calculateIncomExpense(txs);
        return new BalanceSummary(sums[0], sums[1]);
    }

    @GetMapping("/summary/by-category")
    public List<Map<String, Object>> getSummaryByCategory(HttpServletRequest request,
                                                           @RequestParam(required = false) String from,
                                                           @RequestParam(required = false) String to) {
        Long userId = authService.requireSessionUserId(request);
        var txs = filteredTransactions(userId, null, from, to);
        
        return txs.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Uncategorized",
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> buildSummaryMap(
                                    list.get(0).getCategory() != null ? list.get(0).getCategory().getName() : "Uncategorized",
                                    list
                                )
                        )
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    @GetMapping("/summary/by-date")
    public List<Map<String, Object>> getSummaryByDate(HttpServletRequest request,
                                                       @RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String to) {
        Long userId = authService.requireSessionUserId(request);
        var txs = filteredTransactions(userId, null, from, to);
        
        return txs.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate() != null ? t.getDate().toString() : "Unknown",
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> buildSummaryMap(list.get(0).getDate().toString(), list)
                        )
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    private List<Transaction> filteredTransactions(Long userId, Long categoryId, String from, String to) {
        var txs = transactionRepository.findByUserId(userId);
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        return txs.stream()
                .filter(t -> categoryId == null || (t.getCategory() != null && categoryId.equals(t.getCategory().getId())))
                .filter(t -> fromDate == null || (t.getDate() != null && !t.getDate().isBefore(fromDate)))
                .filter(t -> toDate == null || (t.getDate() != null && !t.getDate().isAfter(toDate)))
                .collect(Collectors.toList());
    }

    private LocalDate parseDate(String s) {
        try {
            return (s == null || s.isBlank()) ? null : LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private double[] calculateIncomExpense(List<Transaction> txs) {
        double income = txs.stream()
                .filter(t -> TransactionType.INCOME.equals(t.getType()))
                .mapToDouble(t -> Optional.ofNullable(t.getAmount()).orElse(0.0))
                .sum();
        double expense = txs.stream()
                .filter(t -> TransactionType.EXPENSE.equals(t.getType()))
                .mapToDouble(t -> Optional.ofNullable(t.getAmount()).orElse(0.0))
                .sum();
        return new double[]{income, expense};
    }

    private Map<String, Object> buildSummaryMap(String key, List<Transaction> list) {
        double[] sums = calculateIncomExpense(list);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(key.matches("\\d{4}-\\d{2}-\\d{2}") ? "date" : "category", key);
        result.put("income", sums[0]);
        result.put("expense", sums[1]);
        result.put("balance", sums[0] - sums[1]);
        result.put("count", list.size());
        return result;
    }
}

