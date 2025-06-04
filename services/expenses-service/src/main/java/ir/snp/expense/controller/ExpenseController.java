package ir.snp.expense.controller;

import ir.snp.expense.dto.CategoryExpenseSummaryDTO;
import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Validated
public class ExpenseController {
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/save")
    public ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO expenseRequestDTO, @AuthenticationPrincipal Jwt jwtToken){
        String username = jwtToken.getClaimAsString("preferred_username");
        ExpenseResponseDTO createdExpense = expenseService.createExpense(expenseRequestDTO, username);
        return ResponseEntity.ok(createdExpense);
    }

    @GetMapping("/get-expense/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long expenseId, @AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        ExpenseResponseDTO expense = expenseService.getExpenseById(expenseId, username);
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/get-expenses")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpenses(@AuthenticationPrincipal Jwt jwtToken){
        String username = jwtToken.getClaimAsString("preferred_username");
        List<ExpenseResponseDTO> expenses = expenseService.getExpensesByUsername(username);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/current-month-by-category")
    public ResponseEntity<List<CategoryExpenseSummaryDTO>> getCurrentMonthExpensesByCategory(@AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        List<CategoryExpenseSummaryDTO> categorySummaries = expenseService.getCurrentMonthExpensesByCategory(username);
        return ResponseEntity.ok(categorySummaries);
    }

    @PutMapping("/update/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long expenseId, @RequestBody ExpenseRequestDTO updatedExpenseDetailsDTO, @AuthenticationPrincipal Jwt jwtToken){
        String username = jwtToken.getClaimAsString("preferred_username");
        ExpenseResponseDTO updatedExpense =expenseService.updateExpense(expenseId,updatedExpenseDetailsDTO, username);
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/delete/{expenseId}")
    public void deleteExpense(@PathVariable Long expenseId, @AuthenticationPrincipal Jwt jwtToken){
        String username = jwtToken.getClaimAsString("preferred_username");
        expenseService.deleteExpense(expenseId, username);
    }
}
