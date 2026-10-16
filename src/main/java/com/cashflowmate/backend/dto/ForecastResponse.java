
package com.cashflowmate.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ForecastResponse(
        LocalDate date,
        BigDecimal inflow,
        BigDecimal outflow,
        BigDecimal projectedBalance,
        String riskLevel
) {
}
