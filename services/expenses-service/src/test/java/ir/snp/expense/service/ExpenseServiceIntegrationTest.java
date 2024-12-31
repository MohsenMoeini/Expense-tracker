package ir.snp.expense.service;

import ir.snp.expense.entity.Expense;
import ir.snp.expense.exception.ExpenseNotFoundException;
import ir.snp.expense.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class ExpenseServiceIntegrationTest {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    @DisplayName("should create an expense and persist it to the database")
    public void testCreateExpense(){
        //given
        Expense expense = new Expense();
        expense.setDescription("Movie Tickets");
        expense.setAmount(new BigDecimal("25.00"));
        expense.setCategory("Entertainment");
        expense.setUsername("user1");
        expense.setDate(LocalDate.now());

        //when
        Expense createdExpense = expenseService.createExpense(expense);

        //then
        assertThat(createdExpense.getId()).isNotNull();
        assertThat(createdExpense.getCategory()).isEqualTo("Entertainment");
        assertThat(createdExpense.getDescription()).isEqualTo("Movie Tickets");
        assertThat(createdExpense.getAmount()).isEqualByComparingTo("25.00");
        assertThat(createdExpense.getUsername()).isEqualTo("user1");

        //verify persistence
        Expense foundExpense = expenseRepository.findById(createdExpense.getId()).orElse(null);
        assertThat(foundExpense).isNotNull();
        assertThat(foundExpense.getDescription()).isEqualTo("Movie Tickets");

    }

    @Test
    @DisplayName("should retrieve expenses for a specific user")
    void testGetExpenseByUser(){
        //given
        String userId = "user1";
        Expense expense1 = new Expense(null, "Lunch", new BigDecimal("30.00"), LocalDate.now(),"Food" ,userId, null);
        Expense expense2 = new Expense(null, "Taxi", new BigDecimal("10.00"),LocalDate.now(), "Transport", userId, null);
        expenseRepository.saveAll(List.of(expense1, expense2));

        //when
        List<Expense> expenses =expenseService.getExpensesByUsername(userId);

        //then
        assertThat(expenses).hasSize(2)
                .extracting(Expense::getCategory)
                .containsExactlyInAnyOrder("Food", "Transport");
    }

    @Test
    @DisplayName("should update an existing expense")
    void testUpdateExpense(){
        //given
        String userId = "user1";
        Expense expense = new Expense(null, "Dinner", new BigDecimal("20.00"), LocalDate.now(), "Food", userId, null);
        Expense savedExpense = expenseRepository.save(expense);
        Expense updatedExpense = new Expense(null, "Dinner at restaurant", new BigDecimal("45.00"), LocalDate.now(), "Food", userId,null);

        //when
        Expense result = expenseService.updateExpense(savedExpense.getId(), updatedExpense);

        //then
        assertThat(result.getDescription()).isEqualTo("Dinner at restaurant");
        assertThat(result.getAmount()).isEqualByComparingTo("45.00");

        //verify persistence
        Expense foundExpense = expenseRepository.findById(savedExpense.getId()).orElse(null);
        assertThat(foundExpense).isNotNull();
        assertThat(foundExpense.getDescription()).isEqualTo("Dinner at restaurant");
        assertThat(foundExpense.getAmount()).isEqualByComparingTo("45.00");
    }

    @Test
    @DisplayName("Should delete an existing expense")
    void testDeleteExpense(){
        //given
        String userId = "user1";
        Expense expense =  new Expense(null, "Coffee", new BigDecimal("5.00"), LocalDate.now(), "Food", userId, null);
        Expense savedExpense = expenseRepository.save(expense);

        //when
        expenseService.deleteExpense(savedExpense.getId());

        //then
        Optional<Expense> deletedExpense = expenseRepository.findById(savedExpense.getId());
        assertThat(deletedExpense).isEmpty();
    }
    @Test
    @DisplayName("should throw ExpenseNotFoundException when updating non-existing expense")
    void testUpdateExpenseNotFoundException(){
        //given
        Long nonExistentId = 99L;
        Expense updatedExpense = new Expense(null, "NonExistent", new BigDecimal("6.00"), LocalDate.now(), "Transport", "user78", null);

        //when and then
        assertThatThrownBy(() -> expenseService.updateExpense(nonExistentId, updatedExpense))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("should throw ExpenseNotFoundException when deleting non-existent expense")
    void testDeleteExpenseNotFoundException(){
        //given
        Long nonExistentId = 99L;

        //when and then
        assertThatThrownBy(() -> expenseService.deleteExpense(nonExistentId))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id: "+ nonExistentId);
    }
}
