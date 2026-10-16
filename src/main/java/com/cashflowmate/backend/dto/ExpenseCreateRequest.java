package com.cashflowmate.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseCreateRequest(
        String title,
        BigDecimal amount,
        LocalDate dueDate,
        String status
) {
}