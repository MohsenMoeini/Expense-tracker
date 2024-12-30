package ir.snp.expense.service;

import ir.snp.expense.entity.Expense;
import ir.snp.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }


    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }


}
