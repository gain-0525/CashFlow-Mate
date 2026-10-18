
package com.cashflowmate.backend.entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "upload_batch_id")
    private Long uploadBatchId;

    protected Sales() {
    }


    public Sales(
            String transactionId,
            LocalDate saleDate,
            BigDecimal amount,
            String paymentMethod,
            Long uploadBatchId
    ) {
        this.transactionId = transactionId;
        this.saleDate = saleDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.uploadBatchId = uploadBatchId;
    }


    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Long getUploadBatchId() {
        return uploadBatchId;
    }
    public void update(String transactionId, LocalDate saleDate, BigDecimal amount, String paymentMethod
    ) {
        this.transactionId = transactionId;
        this.saleDate = saleDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
}
