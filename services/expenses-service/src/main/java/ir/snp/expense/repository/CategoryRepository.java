package ir.snp.expense.repository;

import ir.snp.expense.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser_Username(String username);
    Optional<Category> findByIdAndUser_Username(Long id, String username);
    Optional<Category> findByNameAndUser_Username(String name, String username);
}
