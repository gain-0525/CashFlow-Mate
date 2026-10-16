package com.cashflowmate.backend.service;

import com.cashflowmate.backend.dto.ExpenseCreateRequest;
import com.cashflowmate.backend.entity.Expense;
import com.cashflowmate.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // 기존 기능: 지출 전체 조회
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // 새로운 기능: 지출 직접 등록
    public Expense createExpense(ExpenseCreateRequest request) {
        Expense expense = new Expense(
                request.title(),
                request.amount(),
                request.dueDate(),
                request.status()
        );

        return expenseRepository.save(expense);
    }
}