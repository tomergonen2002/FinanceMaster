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
                                <title>FinanceMaster</title>
                            </head>
                            <body>
                                <h1>FinanceMaster</h1>
                                <ul>
                                    <li><a href=\"/categories\">/categories</a></li>
                                    <li><a href=\"/transactions\">/transactions</a></li>
                                    <li><a href=\"/users\">/users</a></li>
                                    <li><a href=\"/categories?userId=\">/categories?userId=</a></li>
                                    <li><a href=\"/transactions?userId=\">/transactions?userId=</a></li>
                                </ul>
                            </body>
                        </html>
                        """;
    }

    /* GET (/categories, /transactions, /users)
    *  Ohne userId: alle Einträge zurückgeben
    *  Mit userId: nur Einträge des Users zurückgeben */
    @GetMapping("/categories")
    public List<Category> getCategories(@RequestParam(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return categoryRepository.findAll();
        }
        return categoryRepository.findByUserId(userId);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestParam(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return transactionRepository.findAll();
        }
        return transactionRepository.findByUserId(userId);
    }

    @GetMapping("/users")
    public Object getUsers(@RequestParam(name = "id", required = false) Long userId) {
        if (userId == null) {
            return userRepository.findAll();
        }
        return userRepository.findById(userId);
    }

    /* POST (/categories, /transactions, /users)
    *  Kategorien müssen userId haben
    *  Transaktionen müssen categoryId + userId haben
    *  Users müssen eine neue Email haben (unique constraint) */
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category c) {
        requireUserId(c.getUser());
        return categoryRepository.save(c);
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction t) {
        requireCategoryId(t.getCategory());
        requireUserId(t.getUser());
        return transactionRepository.save(t);
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User u) {
        if (userRepository.findByEmailIgnoreCase(u.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        return userRepository.save(u);
    }

    /* DELETE (/categories, /categories/{id}, /transactions, /transactions/{id})
    *  deleteCategoriesByUser -> löscht zuerst alle Transaktionen, dann Kategorien
    *  deleteCategory -> löscht nur eine Kategorie
    *  deleteTransactionsByUser -> löscht alle Transaktionen
    *  deleteTransaction -> löscht nur eine Transaktion */
    @DeleteMapping("/categories")
    public void deleteCategoriesByUser(@RequestParam(name = "userId") Long userId) {
        transactionRepository.deleteByUserId(userId);
        categoryRepository.deleteByUserId(userId);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable("id") long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found");
        }
        categoryRepository.deleteById(id);
    }

    @DeleteMapping("/transactions")
    public void deleteTransactionsByUser(@RequestParam(name = "userId") Long userId) {
        transactionRepository.deleteByUserId(userId);
    }

    @DeleteMapping("/transactions/{id}")
    public void deleteTransaction(@PathVariable("id") long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    /* Helper Methoden 
     * Stellen sicher, dass notwendige IDs vorhanden sind */
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