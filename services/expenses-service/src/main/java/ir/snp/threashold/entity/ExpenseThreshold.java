package ir.snp.threashold.entity;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Data;

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

    @Embedded
    @Valid
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_threshold_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_threshold_currency", length = 3))
    })
    private Money monthlyCategoryThreshold;

    @Embedded
    @Valid
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_monthly_expenses_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "total_monthly_expenses_currency", length = 3))
    })
    private Money totalMonthlyExpensesOnCategory;

    @Version
    private Long version;

}
