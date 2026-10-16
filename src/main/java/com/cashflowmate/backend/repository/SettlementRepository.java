package com.cashflowmate.backend.repository;

import com.cashflowmate.backend.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    boolean existsByTransactionId(String transactionId);
}