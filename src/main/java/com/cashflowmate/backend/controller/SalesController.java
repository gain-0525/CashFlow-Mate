
package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.dto.SalesCreateRequest;
import com.cashflowmate.backend.entity.Sales;
import com.cashflowmate.backend.service.SalesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public List<Sales> getAllSales() {
        return salesService.getAllSales();
    }

    @PostMapping
    public ResponseEntity<Sales> createSale(
            @RequestBody SalesCreateRequest request
    ) {
        Sales sale = salesService.createSale(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sale);
    }
}
