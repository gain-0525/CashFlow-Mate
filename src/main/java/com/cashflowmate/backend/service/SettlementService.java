package com.cashflowmate.backend.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.cashflowmate.backend.dto.SettlementCreateRequest;
import com.cashflowmate.backend.entity.Sales;
import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.repository.SalesRepository;
import com.cashflowmate.backend.repository.SettlementRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SalesRepository salesRepository;
    private final SettlementValidationService settlementValidationService;

    public SettlementService(
            SettlementRepository settlementRepository,
            SalesRepository salesRepository,
            SettlementValidationService settlementValidationService
    ) {
        this.settlementRepository = settlementRepository;
        this.salesRepository = salesRepository;
        this.settlementValidationService = settlementValidationService;
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
        String validationStatus = settlementValidationService.calculateValidationStatus(
                sale,
                request.grossAmount(),
                request.feeAmount(),
                request.netAmount()
        );

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

    @Transactional
    public Settlement updateSettlement(
            Long id,
            SettlementCreateRequest request
    ) {
        // 1. 수정할 정산 조회
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "정산 내역을 찾을 수 없습니다. id=" + id
                        )
                );

        // 2. 거래번호로 매출 다시 조회
        Sales sale = salesRepository
                .findByTransactionId(request.transactionId())
                .orElse(null);

        Long saleId = sale != null ? sale.getId() : null;

        // 3. 정산 대조 상태 다시 계산
        String validationStatus = settlementValidationService.calculateValidationStatus(
                sale,
                request.grossAmount(),
                request.feeAmount(),
                request.netAmount()
        );

        // 4. 정산 데이터 수정
        settlement.update(
                request.transactionId(),
                saleId,
                request.settlementDate(),
                request.grossAmount(),
                request.feeAmount(),
                request.netAmount(),
                request.status(),
                validationStatus
        );

        // 5. DB에 반영
        try {
            settlementRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "거래번호 또는 연결된 매출이 이미 다른 정산에 사용되고 있습니다."
            );
        }

        return settlement;
    }

    @Transactional
    public void deleteSettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "정산 내역을 찾을 수 없습니다. id=" + id
                        )
                );

        settlementRepository.delete(settlement);
    }
}