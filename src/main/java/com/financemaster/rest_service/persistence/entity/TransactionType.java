package com.financemaster.rest_service.persistence.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum für Transaktionstypen
 * Unterstützt Deserialisierung von Großbuchstaben und Kleinbuchstaben (z.B. "EXPENSE" oder "expense")
 */
public enum TransactionType {
    INCOME("income"),
    EXPENSE("expense");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TransactionType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (TransactionType type : TransactionType.values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}
