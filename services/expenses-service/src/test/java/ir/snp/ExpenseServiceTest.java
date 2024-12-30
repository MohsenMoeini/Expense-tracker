package ir.snp;

import ir.snp.expense.entity.Expense;
import ir.snp.expense.exception.ExpenseNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void shouldUpdateExistingExpense(){
        //given
        Long expenseId = 1L;
        Expense existingExpense = new Expense(expenseId, "Lunch", new BigDecimal("10.50"), LocalDate.now(), "Food", "user1", 0L);
        Expense updatedExpenseDetails = new Expense(null, "Dinner", new BigDecimal("15.00"), LocalDate.now(), "Dining", "user1", null);

        Expense updatedExpense = new Expense(expenseId, "Dinner", new BigDecimal("15.00"), LocalDate.now(),"Dining", "user1", 1L);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        //when
        Expense result = expenseService.updateExpense(expenseId, updatedExpenseDetails);

        //then
        assertThat(result.getId()).isEqualTo(expenseId);
        assertThat(result.getDescription()).isEqualTo("Dinner");
        assertThat(result.getAmount()).isEqualByComparingTo("15.00");
        assertThat(result.getCategory()).isEqualTo("Dining");

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void shouldThrowExceptionWhenExpenseNotFound(){
        //given
        Long expenseId = 99L;
        Expense updatedExpenseDetails = new Expense(null, "Dinner", new BigDecimal("15.00"), LocalDate.now(), "Dining", "user1", null);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());
        //when then
        assertThatThrownBy(()-> expenseService.updateExpense(expenseId, updatedExpenseDetails))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id: " + expenseId);

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void shouldDeleteExistingExpense(){
        //given
        Long expenseId = 1L;
        when(expenseRepository.existsById(expenseId)).thenReturn(true);

        //when
        expenseService.deleteExpense(expenseId);

        //then

        verify(expenseRepository, times(1)).existsById(expenseId);
        verify(expenseRepository, times(1)).deleteById(expenseId);

    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingExpense(){
        //given
        Long expenseId = 99L;

        when(expenseRepository.existsById(expenseId)).thenReturn(false);

        //when / then
        assertThatThrownBy(() -> expenseService.deleteExpense(expenseId))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id: " + expenseId);

        verify(expenseRepository, times(1)).existsById(expenseId);
        verify(expenseRepository, never()).deleteById(expenseId);


    }
}
