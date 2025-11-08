package com.financemaster.rest_service.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.financemaster.rest_service.persistence.entity.Category;
import com.financemaster.rest_service.persistence.entity.Transaction;
import com.financemaster.rest_service.persistence.entity.User;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controller {

    private final List<Category> categories = Collections.synchronizedList(new ArrayList<>());
    private final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());
    private final List<User> users = Collections.synchronizedList(new ArrayList<>());

    private final AtomicLong catId = new AtomicLong(1);
    private final AtomicLong txId = new AtomicLong(1);
    private final AtomicLong userId = new AtomicLong(1);

    public Controller() {
        Category c1 = new Category("Lebensmittel", "Einkaufen"); c1.setId(catId.getAndIncrement());
        Category c2 = new Category("Gehalt", "Monatliches Gehalt"); c2.setId(catId.getAndIncrement());
        Category c3 = new Category("Transport", ""); c3.setId(catId.getAndIncrement());
        categories.add(c1); categories.add(c2); categories.add(c3);

        Transaction t1 = new Transaction("expense", 34.90, "Lebensmittel", "2025-11-01", c1); t1.setId(txId.getAndIncrement());
        Transaction t2 = new Transaction("income", 1500.00, "Gehalt", "2025-11-03", c2); t2.setId(txId.getAndIncrement());
        Transaction t3 = new Transaction("expense", 2.80, "Ticket", "2025-11-04", c3); t3.setId(txId.getAndIncrement());
        transactions.add(t1); transactions.add(t2); transactions.add(t3);

        User u1 = new User("Tomer Gonen", "tomer@example.com"); u1.setId(userId.getAndIncrement());
        User u2 = new User("Kolja Schollmeyer", "kolja@example.com"); u2.setId(userId.getAndIncrement());
        users.add(u1); users.add(u2);
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
        return categories;
    }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category c) {
        c.setId(catId.getAndIncrement());
        categories.add(c);
        return c;
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        return transactions;
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction t) {
        t.setId(txId.getAndIncrement());
        transactions.add(t);
        return t;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User u) {
        u.setId(userId.getAndIncrement());
        users.add(u);
        return u;
    }
}