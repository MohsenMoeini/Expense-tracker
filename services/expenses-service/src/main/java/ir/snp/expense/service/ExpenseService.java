package ir.snp.expense.service;

import ir.snp.expense.entity.Expense;
import ir.snp.expense.exception.ExpenseNotFoundException;
import ir.snp.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }


    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }


    public List<Expense> getExpensesByUsername(String username) {
        return expenseRepository.findByUsername(username).orElse(Collections.emptyList());
    }

    public Expense updateExpense(Long expenseId, Expense updatedExpenseDetails) {
        return expenseRepository.findById(expenseId)
                .map(existingExpense -> {
                    existingExpense.setDescription(updatedExpenseDetails.getDescription());
                    existingExpense.setAmount(updatedExpenseDetails.getAmount());
                    existingExpense.setDate(updatedExpenseDetails.getDate());
                    existingExpense.setUsername(updatedExpenseDetails.getUsername());
                    existingExpense.setCategory(updatedExpenseDetails.getCategory());
                    return expenseRepository.save(existingExpense);
                }).orElseThrow(() -> new ExpenseNotFoundException(expenseId));
    }

    public void deleteExpense(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)){
            throw new ExpenseNotFoundException(expenseId);
        }
        expenseRepository.deleteById(expenseId);

    }
}
