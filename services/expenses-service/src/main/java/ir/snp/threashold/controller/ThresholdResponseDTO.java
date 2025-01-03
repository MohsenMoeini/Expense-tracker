package ir.snp.threashold.controller;

import ir.snp.expense.dto.MoneyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThresholdResponseDTO {
    private String categoryName;
    private MoneyDTO monthlyThreshold;
    private MoneyDTO totalMonthlyExpenses;
}
