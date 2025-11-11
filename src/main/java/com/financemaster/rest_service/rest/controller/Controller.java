package com.financemaster.rest_service.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        return "Welcome to FinanceMaster!\n" +
                "Available routes:\n" +
                "GET  /categories       - list categories\n" +
                "GET  /transactions     - list transactions\n" +
                "GET  /users            - list users\n" +
                "POST /categories       - create category (JSON)\n" +
                "POST /transactions     - create transaction (JSON)\n" +
                "POST /users            - create user (JSON)\n";
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category c) {
        return categoryRepository.save(c);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction t) {
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