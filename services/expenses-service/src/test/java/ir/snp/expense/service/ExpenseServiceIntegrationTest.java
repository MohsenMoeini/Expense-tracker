package ir.snp.expense.service;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import ir.snp.expense.exception.ExpenseNotFoundException;
import ir.snp.expense.repository.CategoryRepository;
import ir.snp.expense.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
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

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    @DisplayName("should create an expense and persist it to the database")
    public void testCreateExpense(){
        //given
        Category entertainmentCategory = new Category();
        entertainmentCategory.setName("Entertainment");

        Money money = new Money(new BigDecimal("25.00"), Currency.getInstance("IRR"));

        User user = new User("user1");

        Expense expense = new Expense();

        expense.setDescription("Movie Tickets");
        expense.setMoney(money);
        expense.setCategory(entertainmentCategory);
        expense.setUser(user);
        expense.setDate(LocalDate.now());

        //when
        Expense createdExpense = expenseService.createExpense(expense);

        //then
        assertThat(createdExpense.getId()).isNotNull();
        assertThat(createdExpense.getCategory().getName()).isEqualTo("Entertainment");
        assertThat(createdExpense.getDescription()).isEqualTo("Movie Tickets");
        assertThat(createdExpense.getMoney().getAmount()).isEqualByComparingTo("25.00");
        assertThat(createdExpense.getUser().getUsername()).isEqualTo("user1");

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

        Category foodCategory = new Category();
        foodCategory.setName("Food");

        Category transportCategory = new Category();
        transportCategory.setName("Transport");

        Expense expense1 = new Expense(null, "Lunch", new Money(new BigDecimal("30.00"),Currency.getInstance("IRR")),foodCategory ,new User(userId) ,LocalDate.now(), null);
        Expense expense2 = new Expense(null, "Taxi", new Money(new BigDecimal("10.00"), Currency.getInstance("IRR")), transportCategory, new User(userId), LocalDate.now(), null);
        expenseRepository.saveAll(List.of(expense1, expense2));

        //when
        List<Expense> expenses =expenseService.getExpensesByUsername(userId);

        //then
        assertThat(expenses).hasSize(2)
                .extracting(expense -> expense.getCategory().getName())
                .containsExactlyInAnyOrder("Food", "Transport");
    }

    @Test
    @DisplayName("should update an existing expense")
    void testUpdateExpense(){
        //given
        String userId = "user1";

        Category existingCategory = new Category(null,"Food", null);
        categoryRepository.save(existingCategory);

        Category updatedCategory = new Category(null,"Dining", null);
        categoryRepository.save(updatedCategory);


        Money existingMoney = new Money(new BigDecimal("20.00"), Currency.getInstance("IRR"));
        Money updatedgMoney = new Money(new BigDecimal("45.00"), Currency.getInstance("USD"));

        User user = new User(userId);

        Expense expense = new Expense(null, "Dinner", existingMoney, existingCategory, user, LocalDate.now(), null);
        Expense savedExpense = expenseRepository.save(expense);
        Expense updatedExpense = new Expense(null, "Dinner at restaurant", updatedgMoney, updatedCategory, new User(userId), LocalDate.now(), null);

        //when
        Expense result = expenseService.updateExpense(savedExpense.getId(), updatedExpense);

        //then
        assertThat(result.getDescription()).isEqualTo("Dinner at restaurant");
        assertThat(result.getMoney().getAmount()).isEqualByComparingTo("45.00");

        //verify persistence
        Expense foundExpense = expenseRepository.findById(savedExpense.getId()).orElse(null);
        assertThat(foundExpense).isNotNull();
        assertThat(foundExpense.getDescription()).isEqualTo("Dinner at restaurant");
        assertThat(foundExpense.getMoney().getAmount()).isEqualByComparingTo("45.00");
    }

    @Test
    @DisplayName("Should delete an existing expense")
    void testDeleteExpense(){
        //given
        String userId = "user1";
        User user1 = new User(userId);
        Money money = new Money(new BigDecimal("5.00"), Currency.getInstance("IRR"));
        Category foodCategory = new Category();
        foodCategory.setName("Food");

        Expense expense =  new Expense(null, "Coffee", money, foodCategory, user1, LocalDate.now(), null);
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
        String userId = "user1";
        User user1 = new User(userId);
        Money money = new Money(new BigDecimal("6.00"), Currency.getInstance("IRR"));
        Category transportCategory = new Category();
        transportCategory.setName("Transport");
        Expense updatedExpense = new Expense(null, "NonExistent", money, transportCategory, user1, LocalDate.now(), null);

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