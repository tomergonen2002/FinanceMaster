package com.financemaster.rest_service.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
    return "<html><head><title>FinanceMaster API</title><style>body{font-family:system-ui,Arial,sans-serif;margin:2rem;line-height:1.4}a{color:#0645ad;text-decoration:none}a:hover{text-decoration:underline}h1{margin-top:0}ul{list-style:disc;margin-left:1.5rem}</style></head><body>" +
        "<h1>FinanceMaster API</h1>" +
        "<ul>" +
        "<li><a href=\"/categories\">/categories</a></li>" +
        "<li><a href=\"/transactions\">/transactions</a></li>" +
        "<li><a href=\"/users\">/users</a></li>" +
        "</ul>" +
        "</body></html>";
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category c) {
        // Enforce user id-only reference for categories
        if (c.getUser() == null || c.getUser().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user.id is required");
        }
        return categoryRepository.save(c);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction t) {
        // Enforce category id-only reference
        if (t.getCategory() != null && t.getCategory().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category.id is required");
        }
        // Enforce user id-only reference
        if (t.getUser() == null || t.getUser().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user.id is required");
        }
        return transactionRepository.save(t);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User u) {
        return userRepository.save(u);
    }
}