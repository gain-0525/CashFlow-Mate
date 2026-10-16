
package com.cashflowmate.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "sale_id", unique = true)
    private Long saleId;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "fee_amount", nullable = false)
    private BigDecimal feeAmount;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "validation_status", nullable = false)
    private String validationStatus;

    @Column(name = "upload_batch_id", nullable = false)
    private Long uploadBatchId;

    protected Settlement() {
    }

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public Long getUploadBatchId() {
        return uploadBatchId;
    }

    public Settlement(
            String transactionId,
            Long saleId,
            LocalDate settlementDate,
            BigDecimal grossAmount,
            BigDecimal feeAmount,
            BigDecimal netAmount,
            String status,
            String validationStatus,
            Long uploadBatchId
    ) {
        this.transactionId = transactionId;
        this.saleId = saleId;
        this.settlementDate = settlementDate;
        this.grossAmount = grossAmount;
        this.feeAmount = feeAmount;
        this.netAmount = netAmount;
        this.status = status;
        this.validationStatus = validationStatus;
        this.uploadBatchId = uploadBatchId;
    }
}
