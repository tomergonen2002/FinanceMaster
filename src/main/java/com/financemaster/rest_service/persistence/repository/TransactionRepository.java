package com.financemaster.rest_service.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.financemaster.rest_service.persistence.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
