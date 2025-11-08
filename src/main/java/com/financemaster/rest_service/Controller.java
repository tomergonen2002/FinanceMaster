package com.financemaster.rest_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controller {

    // in-memory stores so we can test frontend<>backend without DB (M3)
    private final CopyOnWriteArrayList<Category> categories = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Transaction> transactions = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();

    private final AtomicLong catId = new AtomicLong(0);
    private final AtomicLong txId = new AtomicLong(0);
    private final AtomicLong userId = new AtomicLong(0);

    public Controller() {
        // seed some sample data
        categories.add(new Category("Gehalt", "Monatliches Einkommen"));
        categories.add(new Category("Lebensmittel", "Wöchentliche Einkäufe"));
        // assign ids
        long i = 1;
        for (Category c : categories) c.setId(i++);
        catId.set(i);

        transactions.add(new Transaction("expense", 34.90, "Wocheneinkauf", "2025-11-01", categories.get(1)));
        transactions.add(new Transaction("income", 1500.00, "November Gehalt", "2025-11-03", categories.get(0)));
        txId.set(3);

        users.add(new User()); // empty/default user placeholder
        userId.set(1);
    }

    @GetMapping("/")
    public String index() {
        return "Welcome to FinanceMaster!";
    }

    // Categories
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categories;
    }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category c) {
        long id = catId.getAndIncrement();
        c.setId(id);
        categories.add(c);
        return c;
    }

    // Transactions
    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        return transactions;
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction t) {
        long id = txId.getAndIncrement();
        t.setId(id);
        transactions.add(t);
        return t;
    }

    // Users (very simple placeholder API for testing)
    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User u) {
        long id = userId.getAndIncrement();
        u.setId(id);
        users.add(u);
        return u;
    }
}