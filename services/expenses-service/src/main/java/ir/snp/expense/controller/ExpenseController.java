package ir.snp.expense.controller;

import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.User;
import ir.snp.expense.mappers.ExpenseMapper;
import ir.snp.expense.repository.CategoryRepository;
import ir.snp.expense.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
@Validated
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;
    private final CategoryRepository categoryRepository;
    @Autowired
    public ExpenseController(ExpenseService expenseService, ExpenseMapper expenseMapper, CategoryRepository categoryRepository) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO expenseRequestDTO){
        Expense expense = expenseMapper.toEntity(expenseRequestDTO);
        Category category = categoryRepository.findById(expenseRequestDTO.getCategoryId()).orElseThrow(()->new RuntimeException("Category not found"));
        expense.setCategory(category);
        expense.setUser(new User("username"));
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.ok(expenseMapper.toResponseDTO(createdExpense));
    }
}
