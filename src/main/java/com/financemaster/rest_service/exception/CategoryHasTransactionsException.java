package com.financemaster.rest_service.exception;

public class CategoryHasTransactionsException extends RuntimeException {
    public CategoryHasTransactionsException(String message) {
        super(message);
    }
}
