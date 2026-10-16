
package com.cashflowmate.backend.service;

import com.cashflowmate.backend.dto.ReconciliationResponse;
import com.cashflowmate.backend.entity.Sales;
import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.repository.SalesRepository;
import com.cashflowmate.backend.repository.SettlementRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReconciliationService {

    private final SalesRepository salesRepository;
    private final SettlementRepository settlementRepository;

    public ReconciliationService(
            SalesRepository salesRepository,
            SettlementRepository settlementRepository
    ) {
        this.salesRepository = salesRepository;
        this.settlementRepository = settlementRepository;
    }

    public List<ReconciliationResponse> getReconciliationResults() {

        List<Sales> sales = salesRepository.findAll();
        List<Settlement> settlements = settlementRepository.findAll();

        List<ReconciliationResponse> results = new ArrayList<>();

        for (Sales sale : sales) {

            Settlement matchedSettlement = null;

            for (Settlement settlement : settlements) {
                if (sale.getTransactionId().equals(settlement.getTransactionId())) {
                    matchedSettlement = settlement;
                    break;
                }
            }

            if (matchedSettlement == null) {
                results.add(new ReconciliationResponse(
                        sale.getTransactionId(),
                        sale.getAmount(),
                        null,
                        "MISSING_SETTLEMENT"
                ));
            } else {

                String status;


                if (matchedSettlement.getGrossAmount().subtract(
                        matchedSettlement.getFeeAmount()
                ).compareTo(matchedSettlement.getNetAmount()) != 0) {
                    status = "INVALID_DATA";
                } else if (sale.getAmount().compareTo(
                        matchedSettlement.getGrossAmount()
                ) != 0) {
                    status = "AMOUNT_MISMATCH";
                } else {
                    status = "MATCHED";
                }


                results.add(new ReconciliationResponse(
                        sale.getTransactionId(),
                        sale.getAmount(),
                        matchedSettlement.getGrossAmount(),
                        status
                ));
            }
        }


        for (Settlement settlement : settlements) {

            boolean saleExists = false;

            for (Sales sale : sales) {
                if (settlement.getTransactionId().equals(sale.getTransactionId())) {
                    saleExists = true;
                    break;
                }
            }

            if (!saleExists) {
                results.add(new ReconciliationResponse(
                        settlement.getTransactionId(),
                        null,
                        settlement.getGrossAmount(),
                        "MISSING_SALE"
                ));
            }
        }


        return results;
    }
}
