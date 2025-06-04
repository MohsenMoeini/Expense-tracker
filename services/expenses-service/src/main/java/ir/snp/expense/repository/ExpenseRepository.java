package ir.snp.expense.repository;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<List<Expense>> findByUser_Username(String username);
    Optional<List<Expense>> findByUser_UsernameAndCategoryAndDateBetween(String username, Category category, LocalDate startOfMonth, LocalDate endOfMonth);
    
    @Query("SELECT e FROM Expense e WHERE e.user.username = :username AND e.date BETWEEN :startDate AND :endDate")
    List<Expense> findByUserAndDateBetween(
            @Param("username") String username,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
