
package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.dto.SettlementCreateRequest;
import com.cashflowmate.backend.entity.Settlement;
import com.cashflowmate.backend.service.SettlementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping
    public List<Settlement> getAllSettlements() {
        return settlementService.getAllSettlements();
    }

    @PostMapping
    public ResponseEntity<Settlement> createSettlement(
            @RequestBody SettlementCreateRequest request
    ) {
        Settlement settlement = settlementService.createSettlement(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(settlement);
    }
}
