package ir.snp.threashold.entity;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class ExpenseThreshold {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Valid
    private User user;

    @ManyToOne
    private Category category;

    private BigDecimal monthlyThreshold;
    private BigDecimal totalMonthlyExpenses;


}
