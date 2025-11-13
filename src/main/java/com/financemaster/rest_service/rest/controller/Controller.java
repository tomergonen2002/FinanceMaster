package com.financemaster.rest_service.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import com.financemaster.rest_service.persistence.entity.Category;
import com.financemaster.rest_service.persistence.entity.Transaction;
import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.persistence.repository.CategoryRepository;
import com.financemaster.rest_service.persistence.repository.TransactionRepository;
import com.financemaster.rest_service.persistence.repository.UserRepository;
import java.util.List;
import java.util.Optional;

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
        requireUserId(c.getUser());
        return categoryRepository.save(c);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestParam(name = "userId", required = false) Long userId) {
        return (userId != null) ? transactionRepository.findByUserId(userId) : transactionRepository.findAll();
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction t) {
        requireCategoryId(t.getCategory());
        requireUserId(t.getUser());
        return transactionRepository.save(t);
    }

    @DeleteMapping("/transactions")
    public void deleteTransactionsByUser(@RequestParam(name = "userId") Long userId) {
        transactionRepository.deleteByUserId(userId);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/search")
    public User getUserByEmail(@RequestParam(name = "email") String email) {
        Optional<User> u = userRepository.findByEmailIgnoreCase(email);
        return u.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
    }

    @PostMapping("/users")
    @SuppressWarnings("null")
    public User createUser(@RequestBody User u) {
        try {
            return userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already exists");
        }
    }

    @DeleteMapping("/categories")
    public void deleteCategoriesByUser(@RequestParam(name = "userId") Long userId) {
        transactionRepository.deleteByUserId(userId);
        categoryRepository.deleteByUserId(userId);
    }

    @DeleteMapping("/categories/{id}")
    @SuppressWarnings("null")
    public void deleteCategory(@PathVariable Long id, @RequestParam(name = "userId") Long userId) {
        Category c = categoryRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
        if (!c.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not your category");
        }
        categoryRepository.deleteById(id);
    }

    @DeleteMapping("/transactions/{id}")
    @SuppressWarnings("null")
    public void deleteTransaction(@PathVariable Long id, @RequestParam(name = "userId") Long userId) {
        Transaction t = transactionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found"));
        if (!t.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not your transaction");
        }
        transactionRepository.deleteById(id);
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