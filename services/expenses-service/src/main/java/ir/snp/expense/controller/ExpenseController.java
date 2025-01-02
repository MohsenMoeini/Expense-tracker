package ir.snp.expense.controller;

import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

}
