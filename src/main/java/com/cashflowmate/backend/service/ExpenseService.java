package com.cashflowmate.backend.service;

import com.cashflowmate.backend.dto.ExpenseCreateRequest;
import com.cashflowmate.backend.entity.Expense;
import com.cashflowmate.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Expense updateExpense(Long id, ExpenseCreateRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("지출 내역을 찾을 수 없습니다. id=" + id)
                );
        expense.update(
                request.title(),
                request.amount(),
                request.dueDate(),
                request.status()
        );
        return expense;
    }

    @Transactional
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("지출 내역을 찾을 수 없습니다. id=" + id)
                );
        expenseRepository.delete(expense);
    }


}