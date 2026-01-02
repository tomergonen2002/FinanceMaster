package com.financemaster.rest_service.rest;

public class BalanceSummary {
    private final double income;
    private final double expense;
    private final double balance;

    public BalanceSummary(double income, double expense) {
        this.income = income;
        this.expense = expense;
        this.balance = income - expense;
    }

    public double getIncome() {
        return income;
    }

    public double getExpense() {
        return expense;
    }

    public double getBalance() {
        return balance;
    }
}
