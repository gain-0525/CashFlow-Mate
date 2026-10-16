
package com.cashflowmate.backend.dto;

import java.math.BigDecimal;

public record ReconciliationResponse(
        String transactionId,
        BigDecimal saleAmount,
        BigDecimal settlementAmount,
        String validationStatus
) {
}
