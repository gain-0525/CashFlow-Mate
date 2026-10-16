package com.cashflowmate.backend.repository;
import java.util.Optional;
import com.cashflowmate.backend.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    boolean existsByTransactionId(String transactionId);
    Optional<Sales> findByTransactionId(String transactionId);
}