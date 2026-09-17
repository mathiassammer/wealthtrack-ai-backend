package com.wealthtrack.api.domain.transaction.repository;

import com.wealthtrack.api.domain.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}