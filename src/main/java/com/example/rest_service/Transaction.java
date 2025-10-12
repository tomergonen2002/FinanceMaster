package com.example.rest_service;

import java.time.LocalDate;

public class Transaction {
    private Long id;
    private String type;
    private Double amount;
    private String description;
    private LocalDate date;
    private Category category;

    public Transaction() {}

    public Transaction(String type, Double amount, String description, String date, Category category) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = LocalDate.parse(date);
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}