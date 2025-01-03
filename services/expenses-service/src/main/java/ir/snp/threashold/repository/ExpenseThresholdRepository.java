package ir.snp.threashold.repository;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.User;
import ir.snp.threashold.entity.ExpenseThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseThresholdRepository extends JpaRepository<ExpenseThreshold, Long> {
    Optional<ExpenseThreshold> findByUserAndCategory(User user, Category category);
}
