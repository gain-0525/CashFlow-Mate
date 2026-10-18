
package com.cashflowmate.backend.controller;

import com.cashflowmate.backend.entity.Expense;
import com.cashflowmate.backend.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cashflowmate.backend.dto.ExpenseCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(
            @RequestBody ExpenseCreateRequest request
    ) {
        Expense expense = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable long id,
            @RequestBody ExpenseCreateRequest request
    ) {
        Expense expense = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
