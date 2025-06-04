package ir.snp.threashold.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThresholdRequestDTO {
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotNull(message = "Threshold amount is required")
    @Positive(message = "Threshold amount must be positive")
    private BigDecimal thresholdAmount;
}
