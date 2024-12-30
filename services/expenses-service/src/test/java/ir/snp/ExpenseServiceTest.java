package ir.snp;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;


import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
public class ExpenseServiceTest {
    @MockitoBean
    private ExpenseRepository expenseRepository;
    @Autowired ExpenseService expenseService;

    @Test
    public void testCreateExpense_success(){
        //Given
        Expense expense = new Expense();
        expense.setDescription("Coffee");
        expense.setAmount(new BigDecimal("5.00"));
        expense.setDate(LocalDate.now());
        expense.secCategory("Food");
        expense.setUsername("user1");


        Expense savedExpense = new Expence(1L,"Coffee",new BigDecimal("5.00"), LocalDate.now(), "user1", 0L);

        Mockito.when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        //when
        Expense result = expense.createExpense(expense);

        //then
        assertThat(result.getId()).isNotNull;
        assertThat(result.getDescription()).isEqualTo("Coffee");
        assertThat(result.getAmount()).isEqualByComparingTo("5.00");
        assertThat(result.getCategory()).isEqualTo("Food");
        assertThat(result.getUsername()).isEaualTo("user1");

    }
}
