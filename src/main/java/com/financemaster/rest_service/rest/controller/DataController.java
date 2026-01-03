package com.financemaster.rest_service.rest.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.financemaster.rest_service.persistence.entity.Category;
import com.financemaster.rest_service.persistence.entity.Transaction;
import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.service.AuthService;
import com.financemaster.rest_service.persistence.repository.CategoryRepository;
import com.financemaster.rest_service.persistence.repository.TransactionRepository;
import com.financemaster.rest_service.exception.InvalidInputException;
import com.financemaster.rest_service.exception.EntityNotFoundException;
import com.financemaster.rest_service.exception.AccessDeniedException;
import com.financemaster.rest_service.exception.CategoryHasTransactionsException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class DataController {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    public DataController(CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository,
                         AuthService authService) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.authService = authService;
    }

    @GetMapping("/categories")
    public List<Category> getCategories(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        Long sessionUserId = authService.requireSessionUserId(request);
        if (userId != null && !userId.equals(sessionUserId)) {
            throw new AccessDeniedException("Access denied");
        }
        return categoryRepository.findByUserId(sessionUserId);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestParam(required = false) Long userId, 
                                            @RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) String from,
                                            @RequestParam(required = false) String to,
                                            HttpServletRequest request) {
        Long sessionUserId = authService.requireSessionUserId(request);
        if (userId != null && !userId.equals(sessionUserId)) {
            throw new AccessDeniedException("Access denied");
        }
        return filterTransactions(sessionUserId, categoryId, from, to);
    }

    @PostMapping("/categories")
    @Transactional
    public Category createCategory(@RequestBody Category category, HttpServletRequest request) {
        Long userId = authService.requireSessionUserId(request);
        
        if (category.getName() == null || category.getName().isBlank()) {
            throw new InvalidInputException("Category name is required");
        }
        
        User user = new User();
        user.setId(userId);
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @PostMapping("/transactions")
    @Transactional
    public Transaction createTransaction(@RequestBody Transaction transaction, HttpServletRequest request) {
        Long userId = authService.requireSessionUserId(request);
        
        validateTransaction(transaction);
        
        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Category does not belong to this user");
        }

        User user = new User();
        user.setId(userId);
        transaction.setUser(user);
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    @DeleteMapping("/categories")
    @Transactional
    public void deleteCategoriesByUser(HttpServletRequest request) {
        Long userId = authService.requireSessionUserId(request);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        transactionRepository.deleteAll(transactions);
        List<Category> categories = categoryRepository.findByUserId(userId);
        categoryRepository.deleteAll(categories);
    }

    @DeleteMapping("/categories/{id}")
    @Transactional
    public void deleteCategory(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = authService.requireSessionUserId(request);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        boolean hasTransactions = transactionRepository.existsByCategory_Id(id);
        if (hasTransactions) {
            throw new CategoryHasTransactionsException(
                "Kategorie kann nicht gelöscht werden, weil sie noch zugeordnete Transaktionen hat.");
        }

        categoryRepository.delete(category);
    }

    @DeleteMapping("/transactions")
    @Transactional
    public void deleteTransactionsByUser(HttpServletRequest request) {
        Long userId = authService.requireSessionUserId(request);
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        transactionRepository.deleteAll(transactions);
    }

    @DeleteMapping("/transactions/{id}")
    @Transactional
    public void deleteTransaction(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = authService.requireSessionUserId(request);
        
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        transactionRepository.delete(transaction);
    }

    private List<Transaction> filterTransactions(Long userId, Long categoryId, String from, String to) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        
        if (categoryId != null) {
            transactions = transactions.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId))
                .toList();
        }
        
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        
        if (fromDate != null || toDate != null) {
            final LocalDate finalFromDate = fromDate;
            final LocalDate finalToDate = toDate;
            transactions = transactions.stream()
                .filter(t -> {
                    if (t.getDate() == null) return false;
                    if (finalFromDate != null && t.getDate().isBefore(finalFromDate)) return false;
                    if (finalToDate != null && t.getDate().isAfter(finalToDate)) return false;
                    return true;
                })
                .toList();
        }
        
        return transactions;
    }

    private LocalDate parseDate(String s) {
        try {
            return (s == null || s.isBlank()) ? null : LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }
        if (transaction.getDate() == null) {
            throw new InvalidInputException("Date is required");
        }
        if (transaction.getType() == null) {
            throw new InvalidInputException("Type is required");
        }
        if (transaction.getCategory() == null || transaction.getCategory().getId() == null) {
            throw new InvalidInputException("Category is required");
        }
    }
}
