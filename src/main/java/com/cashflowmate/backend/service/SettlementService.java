package com.cashflowmate.backend.service;

import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.repository.SettlementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;

    public SettlementService(SettlementRepository settlementRepository) {
        this.settlementRepository = settlementRepository;
    }

    public List<Settlement> getAllSettlements() {
        return settlementRepository.findAll();
    }
}