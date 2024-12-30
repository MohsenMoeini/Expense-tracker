package ir.snp;

import ir.snp.expense.entity.Expense;
import ir.snp.expense.repository.ExpenseRepository;
import ir.snp.expense.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;



@SpringBootTest
public class ExpenseServiceTest {
    @MockitoBean
    @SuppressWarnings("unused")
    private ExpenseRepository expenseRepository;
    @Autowired
    ExpenseService expenseService;

    @Test
    public void testCreateExpense_success(){
        //Given
        Expense expense = new Expense();
        expense.setDescription("Coffee");
        expense.setAmount(new BigDecimal("5.00"));
        expense.setDate(LocalDate.now());
        expense.setCategory("Food");
        expense.setUsername("user1");


        Expense savedExpense = new Expense(1L,"Coffee",
                new BigDecimal("5.00"), LocalDate.now(),
                "Food", "user1", 0L);

        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        //when
        Expense result = expenseService.createExpense(expense);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Coffee");
        assertThat(result.getAmount()).isEqualByComparingTo("5.00");
        assertThat(result.getCategory()).isEqualTo("Food");
        assertThat(result.getUsername()).isEqualTo("user1");

    }



    @Test
    void shouldReturnExpensesForGivenUsername(){
        //given
        String username = "user123";
        List<Expense> mockExpenses = List.of(
                new Expense(1L, "Lunch", new BigDecimal("10.50"), LocalDate.now(),
                        "Food", username, 0L),
                new Expense(2L, "Groceries", new BigDecimal("25.30"), LocalDate.now(),
                        "Shopping", username, 0L)
        );

        when(expenseRepository.findByUsername(username)).thenReturn(Optional.of(mockExpenses));

        //when
        List<Expense> expenses = expenseService.getExpensesByUsername(username);

        //then
        assertThat(expenses).hasSize(2);
        assertThat(expenses.get(0).getDescription()).isEqualTo("Lunch");
        assertThat(expenses.get(1).getDescription()).isEqualTo("Groceries");

        verify(expenseRepository, times(1)).findByUsername(username);
    }

    @Test
    void shouldReturnEmptyWhenNoExpensesFoundForUsername(){
        String username = "test_user";
        when(expenseRepository.findByUsername(username)).thenReturn(Optional.empty());

        List<Expense> expenses = expenseService.getExpensesByUsername(username);

        assertThat(expenses).isEmpty();

        verify(expenseRepository, times(1)).findByUsername(username);

    }

}
