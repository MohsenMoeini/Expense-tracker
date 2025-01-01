package ir.snp.expense.repository;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
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
        Category foodCategory = new Category();
        foodCategory.setName("Food");
        Money money = new Money(new BigDecimal("50.00"), Currency.getInstance("IRR"));
        User user = new User("user1");

        Expense expense = new Expense();
        expense.setDescription("Groceries");
        expense.setMoney(money);
        expense.setDate(LocalDate.now());
        expense.setCategory(foodCategory);
        expense.setUser(user);


        //when
        Expense savedExpense = expenseRepository.save(expense);
        Expense foundExpense = expenseRepository.findById(savedExpense.getId()).orElse(null);

        //then
        assertThat(foundExpense).isNotNull();
        assertThat(foundExpense.getDescription()).isEqualTo("Groceries");
        assertThat(foundExpense.getMoney().getAmount()).isEqualByComparingTo("50.00");
        assertThat(foundExpense.getUser().getUsername()).isEqualTo("user1");
        assertThat(foundExpense.getCategory().getName()).isEqualTo("Food");

    }


    @Test
    @DisplayName("Should retrieve expense by username")
    public void findByUsername(){
        //given
        String userId1 = "user1";
        String userId2 = "user2";
        Category utilitiesCategory = new Category();
        utilitiesCategory.setName("Utilities");
        Category healthyCategory = new Category();
        healthyCategory.setName("Health");

        Expense expense1 = new Expense(null, "Internet Bill", new Money(new BigDecimal("60.00"),Currency.getInstance("IRR")), utilitiesCategory, new User(userId1), LocalDate.now(), null);
        Expense expense2 = new Expense(null, "Electricity Bill", new Money(new BigDecimal("80.00"),Currency.getInstance("IRR")), utilitiesCategory, new User(userId1), LocalDate.now(), null);
        Expense expense3 = new Expense(null, "Gym Membership", new Money(new BigDecimal("30.00"), Currency.getInstance("IRR")), healthyCategory, new User(userId2), LocalDate.now(), null);

        expenseRepository.saveAll(List.of(expense1, expense2, expense3));

        //when
        List<Expense> user1Expenses = expenseRepository.findByUser_Username("user1").orElseGet(ArrayList::new);
        List<Expense> user2Expenses = expenseRepository.findByUser_Username("user2").orElseGet(ArrayList::new);

        //then
        assertThat(user1Expenses).hasSize(2)
                .extracting(Expense::getDescription)
                .containsExactlyInAnyOrder("Internet Bill", "Electricity Bill");
        assertThat(user2Expenses).hasSize(1)
                .extracting(Expense::getDescription)
                .containsExactly("Gym Membership");
    }

}