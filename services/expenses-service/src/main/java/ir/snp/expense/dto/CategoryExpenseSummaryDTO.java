package ir.snp.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryExpenseSummaryDTO {
    private String categoryName;
    private BigDecimal totalAmount;
}
