
package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.dto.ReconciliationResponse;
import com.cashflowmate.backend.service.ReconciliationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(
            ReconciliationService reconciliationService
    ) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping
    public List<ReconciliationResponse> getReconciliationResults() {
        return reconciliationService.getReconciliationResults();
    }
}
