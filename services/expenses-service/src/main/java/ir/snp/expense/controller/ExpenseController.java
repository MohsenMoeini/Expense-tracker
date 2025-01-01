package ir.snp.expense.controller;

import ir.snp.expense.dto.ExpenseDTO;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.User;
import ir.snp.expense.mappers.ExpenseMapper;
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
    @Autowired
    public ExpenseController(ExpenseService expenseService, ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @PostMapping("/save")
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO){
        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense.setUser(new User("username"));
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.ok(createdExpense);
    }
}
