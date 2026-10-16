package com.cashflowmate.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "business_settings")
public class BusinessSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    @Column(name = "minimum_operating_cash", nullable = false)
    private BigDecimal minimumOperatingCash;

    protected BusinessSettings() {
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public BigDecimal getMinimumOperatingCash() {
        return minimumOperatingCash;
    }
}
