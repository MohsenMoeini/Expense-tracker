package ir.snp.threashold.repository;

import ir.snp.expense.entity.Category;
import ir.snp.threashold.entity.ExpenseThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseThresholdRepository extends JpaRepository<ExpenseThreshold, Long> {
    Optional<ExpenseThreshold> findByUser_UsernameAndCategory(String username, Category category);
    Optional<List<ExpenseThreshold>> findByUser_Username(String username);
}
