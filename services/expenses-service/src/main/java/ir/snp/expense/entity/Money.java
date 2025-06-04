package ir.snp.expense.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Money {

    @NotNull
    @DecimalMin(value = "0.0", message = "Amount must be positive")
    private BigDecimal amount;
    @NotNull(message = "Currency is mandatory")
    private Currency currency;

}