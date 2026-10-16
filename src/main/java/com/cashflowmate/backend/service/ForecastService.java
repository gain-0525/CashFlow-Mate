
package com.cashflowmate.backend.service;

import com.cashflowmate.backend.dto.ForecastResponse;
import com.cashflowmate.backend.entity.BusinessSettings;
import com.cashflowmate.backend.entity.Expense;
import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.repository.BusinessSettingsRepository;
import com.cashflowmate.backend.repository.ExpenseRepository;
import com.cashflowmate.backend.repository.SettlementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForecastService {

    private final BusinessSettingsRepository settingsRepository;
    private final SettlementRepository settlementRepository;
    private final ExpenseRepository expenseRepository;

    public ForecastService(
            BusinessSettingsRepository settingsRepository,
            SettlementRepository settlementRepository,
            ExpenseRepository expenseRepository
    ) {
        this.settingsRepository = settingsRepository;
        this.settlementRepository = settlementRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<ForecastResponse> getForecast() {

        BusinessSettings settings = settingsRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("사업장 설정이 없습니다."));

        List<Settlement> settlements = settlementRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();

        LocalDate baseDate = settings.getBalanceDate();
        BigDecimal balance = settings.getCurrentBalance();
        BigDecimal minimumCash = settings.getMinimumOperatingCash();

        List<ForecastResponse> results = new ArrayList<>();

        for (int day = 1; day <= 30; day++) {

            LocalDate date = baseDate.plusDays(day);
            BigDecimal inflow = BigDecimal.ZERO;
            BigDecimal outflow = BigDecimal.ZERO;

            for (Settlement settlement : settlements) {
                if (settlement.getSettlementDate().equals(date)
                        && settlement.getStatus().equals("SCHEDULED")
                        && settlement.getValidationStatus().equals("MATCHED")) {

                    inflow = inflow.add(settlement.getNetAmount());
                }
            }

            for (Expense expense : expenses) {
                if (expense.getDueDate().equals(date)
                        && expense.getStatus().equals("SCHEDULED")) {

                    outflow = outflow.add(expense.getAmount());
                }
            }

            balance = balance.add(inflow).subtract(outflow);

            String riskLevel;

            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                riskLevel = "DANGER";
            } else if (balance.compareTo(minimumCash) < 0) {
                riskLevel = "WARNING";
            } else {
                riskLevel = "NORMAL";
            }

            results.add(new ForecastResponse(
                    date,
                    inflow,
                    outflow,
                    balance,
                    riskLevel
            ));
        }

        return results;
    }
}
