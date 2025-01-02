package ir.snp.expense.service;

import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.User;
import ir.snp.expense.exception.ExpenseNotFoundException;
import ir.snp.expense.exception.UnauthorizedActionException;
import ir.snp.expense.mappers.ExpenseMapper;
import ir.snp.expense.repository.CategoryRepository;
import ir.snp.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.expenseMapper = expenseMapper;
    }


    public ExpenseResponseDTO createExpense(ExpenseRequestDTO expenseRequestDTO, String username) {
        Expense expense = expenseMapper.toEntity(expenseRequestDTO);
        Category category = categoryRepository.findById(expenseRequestDTO.getCategoryId()).orElseThrow(()->new RuntimeException("Category not found"));
        expense.setCategory(category);
        expense.setUser(new User(username));
        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponseDTO(savedExpense);
    }


    public List<ExpenseResponseDTO> getExpensesByUsername(String username) {
        List<Expense> expenses = expenseRepository.findByUser_Username(username).
                orElse(Collections.emptyList());
        return expenseMapper.toResponseDTOs(expenses);
    }

    public ExpenseResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO updatedExpenseDetailsDTO,String username) {
        Expense updatedExpenseDetails = expenseMapper.toEntity(updatedExpenseDetailsDTO);
        Category category = categoryRepository.findById(updatedExpenseDetailsDTO.getCategoryId()).orElseThrow(()->new RuntimeException("Category not found"));
        Expense updatedExpense = expenseRepository.findById(expenseId)
                .map(existingExpense -> {
                    if (!existingExpense.getUser().getUsername().equals(username)){
                        throw new UnauthorizedActionException("You do not have permissions to update this expense");
                    }
                    existingExpense.setDescription(updatedExpenseDetails.getDescription());
                    existingExpense.setMoney(updatedExpenseDetails.getMoney());
                    existingExpense.setDate(updatedExpenseDetails.getDate());
                    existingExpense.setCategory(category);
                    return expenseRepository.save(existingExpense);
                }).orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        return expenseMapper.toResponseDTO(updatedExpense);
    }

    public void deleteExpense(Long expenseId, String username) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(()-> new ExpenseNotFoundException(expenseId));
        if (!expense.getUser().getUsername().equals(username)){
            throw new UnauthorizedActionException("You do not have permissions to delete this expense");
        }
        expenseRepository.deleteById(expenseId);
    }
}
