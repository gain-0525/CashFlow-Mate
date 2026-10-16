package com.cashflowmate.backend.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.cashflowmate.backend.dto.SettlementCreateRequest;
import com.cashflowmate.backend.entity.Sales;
import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.repository.SalesRepository;
import com.cashflowmate.backend.repository.SettlementRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SalesRepository salesRepository;

    public SettlementService(
            SettlementRepository settlementRepository,
            SalesRepository salesRepository
    ) {
        this.settlementRepository = settlementRepository;
        this.salesRepository = salesRepository;
    }

    public List<Settlement> getAllSettlements() {
        return settlementRepository.findAll();
    }

    public Settlement createSettlement(SettlementCreateRequest request) {

        // 거래번호로 기존 매출 찾기
        Sales sale = salesRepository
                .findByTransactionId(request.transactionId())
                .orElse(null);

        Long saleId = sale != null ? sale.getId() : null;

        // 검증 상태 결정
        String validationStatus;

        if (request.grossAmount().subtract(request.feeAmount())
                .compareTo(request.netAmount()) != 0) {
            validationStatus = "INVALID_DATA";

        } else if (sale == null) {
            validationStatus = "MISSING_SALE";

        } else if (sale.getAmount().compareTo(request.grossAmount()) != 0) {
            validationStatus = "AMOUNT_MISMATCH";

        } else {
            validationStatus = "MATCHED";
        }

        Settlement settlement = new Settlement(
                request.transactionId(),
                saleId,
                request.settlementDate(),
                request.grossAmount(),
                request.feeAmount(),
                request.netAmount(),
                request.status(),
                validationStatus,
                null
        );

        try {
            return settlementRepository.saveAndFlush(settlement);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 존재하는 거래번호입니다."
            );
        }
    }
}