package com.cashflowmate.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SettlementCreateRequest(
        String transactionId,
        LocalDate settlementDate,
        BigDecimal grossAmount,
        BigDecimal feeAmount,
        BigDecimal netAmount,
        String status
) {
}