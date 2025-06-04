package ir.snp.threashold.dto;

import ir.snp.expense.dto.MoneyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThresholdResponseDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private MoneyDTO monthlyThreshold;
    private MoneyDTO totalMonthlyExpenses;
}
