package ir.snp.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDTO {
    private String description;
    private MoneyDTO money;
    private String categoryName;
    private LocalDate date;
}
