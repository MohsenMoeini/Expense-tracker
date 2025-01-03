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
import ir.snp.threashold.entity.ExpenseThreshold;
import ir.snp.threashold.repository.ExpenseThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseThresholdRepository expenseThresholdRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository, ExpenseThresholdRepository expenseThresholdRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.expenseThresholdRepository = expenseThresholdRepository;
        this.expenseMapper = expenseMapper;
    }

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO expenseRequestDTO, String username) {
        Expense expense = expenseMapper.toEntity(expenseRequestDTO);
        Category category = categoryRepository.findById(expenseRequestDTO.getCategoryId()).orElseThrow(()->new RuntimeException("Category not found"));
        addToThresholdIfExist(category, username, expense.getMoney().getAmount());
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
                    BigDecimal differenceAmount = updatedExpenseDetails.getMoney().getAmount().subtract(existingExpense.getMoney().getAmount());

                    existingExpense.setDescription(updatedExpenseDetails.getDescription());
                    existingExpense.setMoney(updatedExpenseDetails.getMoney());
                    existingExpense.setDate(updatedExpenseDetails.getDate());
                    existingExpense.setCategory(category);
                    addToThresholdIfExist(category, username, differenceAmount);
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

    public boolean addToThresholdIfExist(Category category, String username, BigDecimal amount) {
        Optional<ExpenseThreshold> threshold = expenseThresholdRepository.
                findByUserAndCategory(new User(username), category);

        return threshold.map(expenseThreshold -> {
            expenseThreshold.setTotalMonthlyExpenses(expenseThreshold.getTotalMonthlyExpenses().add(amount));
            expenseThresholdRepository.save(expenseThreshold);
            return true;
        }).orElse(false);
    }

}
