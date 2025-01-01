package ir.snp.expense.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequestDTO {
    @NotBlank(message = "Description is mandatory")
    private String description;
    @Valid
    @NotNull(message = "Money details are mandatory")
    private MoneyDTO money;
    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;
    @NotNull(message = "Date is mandatory")
    private LocalDate date;
}
