package ir.snp.expense.service;

import ir.snp.expense.dto.CategoryExpenseSummaryDTO;
import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import ir.snp.expense.exception.CategoryNotFoundException;
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
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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
        Category category = categoryRepository.findByIdAndUser_Username(expenseRequestDTO.getCategoryId(), username)
                .orElseThrow(() -> new CategoryNotFoundException(expenseRequestDTO.getCategoryId()));
        addToThresholdIfExist(category, username, expense.getMoney().getAmount());
        expense.setCategory(category);
        expense.setUser(new User(username));
        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponseDTO(savedExpense);
    }

    public ExpenseResponseDTO getExpenseById(Long expenseId, String username) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        
        if (!expense.getUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You do not have permissions to view this expense");
        }
        
        return expenseMapper.toResponseDTO(expense);
    }

    public List<ExpenseResponseDTO> getExpensesByUsername(String username) {
        List<Expense> expenses = expenseRepository.findByUser_Username(username).
                orElse(Collections.emptyList());
        return expenseMapper.toResponseDTOs(expenses);
    }
    
    public List<CategoryExpenseSummaryDTO> getCurrentMonthExpensesByCategory(String username) {
        // Get current month's start and end dates
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        // Get all expenses for the current month
        List<Expense> currentMonthExpenses = expenseRepository.findByUserAndDateBetween(
                username, startDate, endDate);
        
        // Group expenses by category and sum amounts
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        
        for (Expense expense : currentMonthExpenses) {
            String categoryName = expense.getCategory().getName();
            BigDecimal amount = expense.getMoney().getAmount();
            
            // Convert all amounts to the same currency (assuming USD for simplicity)
            // In a real application, you might want to implement currency conversion
            
            categoryTotals.merge(categoryName, amount, BigDecimal::add);
        }
        
        // Convert to list of DTOs
        return categoryTotals.entrySet().stream()
                .map(entry -> new CategoryExpenseSummaryDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO updatedExpenseDetailsDTO, String username) {
        Expense updatedExpenseDetails = expenseMapper.toEntity(updatedExpenseDetailsDTO);
        Category category = categoryRepository.findByIdAndUser_Username(updatedExpenseDetailsDTO.getCategoryId(), username)
                .orElseThrow(() -> new CategoryNotFoundException(updatedExpenseDetailsDTO.getCategoryId()));
        Expense updatedExpense = expenseRepository.findById(expenseId)
                .map(existingExpense -> {
                    if (!existingExpense.getUser().getUsername().equals(username)) {
                        throw new UnauthorizedActionException("You do not have permissions to update this expense");
                    }
                    updateEngagedThresholds(existingExpense.getCategory(), category, existingExpense.getMoney().getAmount(), updatedExpenseDetails.getMoney().getAmount(), username);
                    existingExpense.setDescription(updatedExpenseDetails.getDescription());
                    existingExpense.setMoney(updatedExpenseDetails.getMoney());
                    existingExpense.setDate(updatedExpenseDetails.getDate());
                    existingExpense.setCategory(category);
                    return expenseRepository.save(existingExpense);
                }).orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        return expenseMapper.toResponseDTO(updatedExpense);
    }

    @Transactional
    public void deleteExpense(Long expenseId, String username) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        if (!expense.getUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You do not have permissions to delete this expense");
        }
        expenseRepository.deleteById(expenseId);
    }

    public boolean addToThresholdIfExist(Category category, String username, BigDecimal amount) {
        Optional<ExpenseThreshold> threshold = expenseThresholdRepository.
                findByUser_UsernameAndCategory(username, category);

        return threshold.map(expenseThreshold -> {
            Money totalMonthlyExpenses = expenseThreshold.getTotalMonthlyExpensesOnCategory();
            totalMonthlyExpenses.setAmount(expenseThreshold.getTotalMonthlyExpensesOnCategory().getAmount().add(amount));
            expenseThreshold.setTotalMonthlyExpensesOnCategory(totalMonthlyExpenses);
            expenseThresholdRepository.save(expenseThreshold);
            return true;
        }).orElse(false);
    }

    public boolean updateEngagedThresholds(Category existingCategory, Category updatingCategory, BigDecimal existingAmount, BigDecimal updatingAmount, String username) {
        if (existingCategory.getId().equals(updatingCategory.getId())) {
            BigDecimal difference = updatingAmount.subtract(existingAmount);
            return updateThreshold(existingCategory, username, difference);
        }

        boolean subtracted = updateThreshold(existingCategory, username, existingAmount.negate());
        boolean added = updateThreshold(updatingCategory, username, updatingAmount);

        return subtracted && added;
    }

    private boolean updateThreshold(Category category, String username, BigDecimal amountToAdd) {
        return expenseThresholdRepository.findByUser_UsernameAndCategory(username, category)
                .map(threshold -> {
                    Money totalMoneyExpenses = threshold.getTotalMonthlyExpensesOnCategory();
                    totalMoneyExpenses.setAmount(totalMoneyExpenses.getAmount().add(amountToAdd));
                    threshold.setTotalMonthlyExpensesOnCategory(totalMoneyExpenses);
                    expenseThresholdRepository.save(threshold);
                    return true;
                }).orElse(false);
    }
}
