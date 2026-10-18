package com.cashflowmate.backend.service;

import com.cashflowmate.backend.entity.Sales;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SettlementValidationService {

    public String calculateValidationStatus(
            Sales sale,
            BigDecimal grossAmount,
            BigDecimal feeAmount,
            BigDecimal netAmount
    ) {
        if (grossAmount.subtract(feeAmount).compareTo(netAmount) != 0) {
            return "INVALID_DATA";
        }

        if (sale == null) {
            return "MISSING_SALE";
        }

        if (sale.getAmount().compareTo(grossAmount) != 0) {
            return "AMOUNT_MISMATCH";
        }

        return "MATCHED";
    }
}