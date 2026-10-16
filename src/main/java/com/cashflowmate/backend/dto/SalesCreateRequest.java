package com.cashflowmate.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesCreateRequest(
        String transactionId,
        LocalDate saleDate,
        BigDecimal amount,
        String paymentMethod
) {
}
