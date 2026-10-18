package com.cashflowmate.backend.service;

import com.cashflowmate.backend.repository.SettlementRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.cashflowmate.backend.dto.SalesCreateRequest;
import com.cashflowmate.backend.entity.Sales;
import com.cashflowmate.backend.repository.SalesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.repository.SettlementRepository;

import java.util.List;

@Service
public class SalesService {

    private final SalesRepository salesRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementValidationService settlementValidationService;

    public SalesService(
            SalesRepository salesRepository,
            SettlementRepository settlementRepository,
            SettlementValidationService settlementValidationService
    ) {
        this.salesRepository = salesRepository;
        this.settlementRepository = settlementRepository;
        this.settlementValidationService = settlementValidationService;
    }

    public List<Sales> getAllSales() {
        return salesRepository.findAll();
    }

    // 매출 직접 등록
    @Transactional
    public Sales createSale(SalesCreateRequest request) {

        // 1. 새로운 매출 생성
        Sales sale = new Sales(
                request.transactionId(),
                request.saleDate(),
                request.amount(),
                request.paymentMethod(),
                null
        );

        // 2. 매출 DB 저장
        try {
            salesRepository.saveAndFlush(sale);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 존재하는 거래번호입니다."
            );
        }

        // 3. 같은 거래번호를 가진 기존 정산 조회
        settlementRepository.findByTransactionId(sale.getTransactionId())
                .ifPresent(settlement -> {

                    // 4. 새로 등록된 매출 기준으로 대조 상태 재계산
                    String validationStatus =
                            settlementValidationService.calculateValidationStatus(
                                    sale,
                                    settlement.getGrossAmount(),
                                    settlement.getFeeAmount(),
                                    settlement.getNetAmount()
                            );

                    // 5. 정산에 매출 연결 및 검증 상태 갱신
                    settlement.updateReconciliation(
                            sale.getId(),
                            validationStatus
                    );
                });

        return sale;
    }

    @Transactional
    public Sales updateSale(Long id, SalesCreateRequest request) {
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "매출 내역을 찾을 수 없습니다. id=" + id
                        )
                );

        // 1. 수정 전 거래번호 저장
        String oldTransactionId = sale.getTransactionId();

        // 2. 매출 정보 수정
        sale.update(
                request.transactionId(),
                request.saleDate(),
                request.amount(),
                request.paymentMethod()
        );

        try {
            salesRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 존재하는 거래번호입니다."
            );
        }

        // 3. 기존 거래번호에 연결된 정산 갱신
        settlementRepository.findByTransactionId(oldTransactionId)
                .ifPresent(settlement -> {
                    Sales matchedSale = oldTransactionId.equals(sale.getTransactionId())
                            ? sale
                            : null;

                    String validationStatus =
                            settlementValidationService.calculateValidationStatus(
                                    matchedSale,
                                    settlement.getGrossAmount(),
                                    settlement.getFeeAmount(),
                                    settlement.getNetAmount()
                            );

                    settlement.updateReconciliation(
                            matchedSale != null ? matchedSale.getId() : null,
                            validationStatus
                    );
                });

        // 4. 거래번호가 변경되었다면 새 거래번호의 정산도 갱신
        if (!oldTransactionId.equals(sale.getTransactionId())) {
            settlementRepository.findByTransactionId(sale.getTransactionId())
                    .ifPresent(settlement -> {
                        String validationStatus =
                                settlementValidationService.calculateValidationStatus(
                                        sale,
                                        settlement.getGrossAmount(),
                                        settlement.getFeeAmount(),
                                        settlement.getNetAmount()
                                );

                        settlement.updateReconciliation(
                                sale.getId(),
                                validationStatus
                        );
                    });
        }

        return sale;
    }

    @Transactional
    public void deleteSale(Long id) {
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "매출 내역을 찾을 수 없습니다. id=" + id
                        )
                );

        // 1. 삭제할 매출과 연결된 정산 조회
        settlementRepository.findByTransactionId(sale.getTransactionId())
                .ifPresent(settlement -> {

                    // 2. 매출이 없어진 상태로 다시 검증
                    String validationStatus =
                            settlementValidationService.calculateValidationStatus(
                                    null,
                                    settlement.getGrossAmount(),
                                    settlement.getFeeAmount(),
                                    settlement.getNetAmount()
                            );

                    // 3. 매출 연결 해제 및 대조 상태 갱신
                    settlement.updateReconciliation(null, validationStatus);
                });

        // 4. 정산 변경사항 먼저 DB에 반영
        settlementRepository.flush();

        // 5. 매출 삭제
        salesRepository.delete(sale);
    }
}