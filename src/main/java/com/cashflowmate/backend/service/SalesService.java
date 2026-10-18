package com.cashflowmate.backend.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.cashflowmate.backend.dto.SalesCreateRequest;
import com.cashflowmate.backend.entity.Sales;
import com.cashflowmate.backend.repository.SalesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalesService {

    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    public List<Sales> getAllSales() {
        return salesRepository.findAll();
    }

    // 매출 직접 등록
    public Sales createSale(SalesCreateRequest request) {

        Sales sale = new Sales(
                request.transactionId(),
                request.saleDate(),
                request.amount(),
                request.paymentMethod(),
                null
        );

        try {
            return salesRepository.saveAndFlush(sale);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 존해자는 거래번호입니다."
            );
        }
    }

    @Transactional
    public Sales updateSale(Long id, SalesCreateRequest request) {
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "매출 내역을 찾을 수 없습니다. id=" +id
                        )
                );
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
        salesRepository.delete(sale);
    }
}