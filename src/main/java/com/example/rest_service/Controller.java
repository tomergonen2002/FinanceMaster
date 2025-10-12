package com.example.rest_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class Controller {

    @GetMapping("/")
    public String index() {
        return "Welcome to FinanceMaster!";
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return List.of(
                new Category("Gehalt", "Monatliches Einkommen"),
                new Category("Lebensmittel", "Wöchentliche Einkäufe")
        );
    }
}