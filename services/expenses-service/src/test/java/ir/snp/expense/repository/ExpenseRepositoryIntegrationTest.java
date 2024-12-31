package ir.snp.expense.repository;

import ir.snp.expense.entity.Expense;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExpenseRepositoryIntegrationTest {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    @DisplayName("Should save and retrieve an expense successfully")
    public void testSaveAndFindExpense(){
        //given
        Expense expense = new Expense();
        expense.setDescription("Groceries");
        expense.setAmount(new BigDecimal("50.00"));
        expense.setDate(LocalDate.now());
        expense.setCategory("Food");
        expense.setUsername("user1");

        //when
        Expense savedExpense = expenseRepository.save(expense);
        Expense foundExpense = expenseRepository.findById(savedExpense.getId()).orElse(null);

        //then
        assertThat(foundExpense).isNotNull();
        assertThat(foundExpense.getDescription()).isEqualTo("Groceries");
        assertThat(foundExpense.getAmount()).isEqualByComparingTo("50.00");
        assertThat(foundExpense.getUsername()).isEqualTo("user1");
        assertThat(foundExpense.getCategory()).isEqualTo("Food");

    }


    @Test
    @DisplayName("Should retrieve expense by username")
    public void findByUsername(){
        //given
        Expense expense1 = new Expense(null, "Internet Bill", new BigDecimal("60.00"), LocalDate.now(), "Utilities", "user1", null);
        Expense expense2 = new Expense(null, "Electricity Bill", new BigDecimal("80.00"), LocalDate.now(), "Utilities", "user1", null);
        Expense expense3 = new Expense(null, "Gym Membership", new BigDecimal("30.00"), LocalDate.now(), "Health", "user2", null);

        expenseRepository.saveAll(List.of(expense1, expense2, expense3));

        //when
        List<Expense> user1Expenses = expenseRepository.findByUsername("user1").orElseGet(ArrayList::new);
        List<Expense> user2Expenses = expenseRepository.findByUsername("user2").orElseGet(ArrayList::new);

        //then
        assertThat(user1Expenses).hasSize(2)
                .extracting(Expense::getDescription)
                .containsExactlyInAnyOrder("Internet Bill", "Electricity Bill");
        assertThat(user2Expenses).hasSize(1)
                .extracting(Expense::getDescription)
                .containsExactly("Gym Membership");
    }

}