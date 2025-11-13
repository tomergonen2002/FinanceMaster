package com.financemaster.rest_service.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.financemaster.rest_service.persistence.entity.Category;
import com.financemaster.rest_service.persistence.entity.Transaction;
import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.persistence.repository.CategoryRepository;
import com.financemaster.rest_service.persistence.repository.TransactionRepository;
import com.financemaster.rest_service.persistence.repository.UserRepository;
import java.util.List;

@RestController
public class Controller {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public Controller(CategoryRepository categoryRepository,
                      TransactionRepository transactionRepository,
                      UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
                return """
                        <!DOCTYPE html>
                        <html>
                            <head>
                                <meta charset=\"utf-8\" />
                                <title>FinanceMaster API</title>
                            </head>
                            <body>
                                <h1>FinanceMaster API</h1>
                                <ul>
                                    <li><a href=\"/categories\">/categories</a></li>
                                    <li><a href=\"/transactions\">/transactions</a></li>
                                    <li><a href=\"/users\">/users</a></li>
                                </ul>
                            </body>
                        </html>
                        """;
    }

    @GetMapping("/categories")
    public List<Category> getCategories(@RequestParam(name = "userId", required = false) Long userId) {
        return (userId != null) ? categoryRepository.findByUserId(userId) : categoryRepository.findAll();
    }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category c) {
        // Enforce user id-only reference for categories
        requireUserId(c.getUser());
        return categoryRepository.save(c);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestParam(name = "userId", required = false) Long userId) {
        return (userId != null) ? transactionRepository.findByUserId(userId) : transactionRepository.findAll();
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction t) {
        // Enforce id-only references
        requireCategoryId(t.getCategory());
        requireUserId(t.getUser());
        return transactionRepository.save(t);
    }

    @DeleteMapping("/transactions")
    public void deleteTransactionsByUser(@RequestParam(name = "userId") Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        transactionRepository.deleteByUserId(userId);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    @SuppressWarnings("null")
    public User createUser(@RequestBody User u) {
        return userRepository.save(u);
    }

    @DeleteMapping("/categories")
    public void deleteCategoriesByUser(@RequestParam(name = "userId") Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        // Ensure transactions are deleted first to avoid FK violations if constraints exist
        transactionRepository.deleteByUserId(userId);
        categoryRepository.deleteByUserId(userId);
    }

    // --- helpers -----------------------------------------------------------
    private void requireUserId(User u) {
        if (u == null || u.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user.id is required");
        }
    }
    private void requireCategoryId(Category c) {
        if (c != null && c.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category.id is required");
        }
    }
}