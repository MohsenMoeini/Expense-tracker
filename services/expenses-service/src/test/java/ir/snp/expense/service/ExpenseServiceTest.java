package ir.snp.expense.service;

import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import ir.snp.expense.exception.ExpenseNotFoundException;
import ir.snp.expense.mappers.ExpenseMapper;
import ir.snp.expense.repository.CategoryRepository;
import ir.snp.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
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

    @MockitoBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @MockitoBean
    @SuppressWarnings("unused")
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    ExpenseService expenseService;


    @BeforeEach
    void setup(){
        mockJwt();
    }
    private void mockJwt(){
        Jwt mockJwt = Jwt.withTokenValue("test-token")
                .headers(stringObjectMap -> stringObjectMap.put("Alg","123"))
                .claim("preferred-username", "test-username")
                .build();
        when(jwtDecoder.decode(any())).thenReturn(mockJwt);
    }

    @Test
    public void testCreateExpense_success(){
        //Given
        Expense expense = new Expense();

        Category foodCategory = new Category();
        foodCategory.setName("Food");

        Money money = new Money(new BigDecimal("5.00"),Currency.getInstance("IRR"));

        User user = new User("user1");

        expense.setDescription("Coffee");
        expense.setMoney(money);
        expense.setDate(LocalDate.now());
        expense.setCategory(foodCategory);
        expense.setUser(user);
        ExpenseRequestDTO expenseRequestDTO = expenseMapper.toRequestDTO(expense);

        Expense savedExpense = new Expense(1L,"Coffee",
                money,foodCategory, user, LocalDate.now(),
                 0L);

        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);
        when(categoryRepository.findById(any())).thenReturn(Optional.of(foodCategory));


        //when
        ExpenseResponseDTO result = expenseService.createExpense(expenseRequestDTO, user.getUsername());

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Coffee");
        assertThat(result.getMoney().getAmount()).isEqualByComparingTo("5.00");
        assertThat(Currency.getInstance(result.getMoney().getCurrencyCode())).isEqualTo(Currency.getInstance("IRR"));
        assertThat(result.getCategoryName()).isEqualTo("Food");
        assertThat(result.getUsername()).isEqualTo("user1");

    }



    @Test
    void shouldReturnExpensesForGivenUsername(){
        //given
        String username = "user123";
        Category foodCategory = new Category(1L, "Food", null);
        Category shoppingCategory = new Category(2L, "Shopping", null);

        List<Expense> mockExpenses = List.of(
                new Expense(1L, "Lunch", new Money(new BigDecimal("10.50"), Currency.getInstance("IRR"))
                                        , foodCategory, new User(username)
                                        , LocalDate.now(), 0L),
                new Expense(2L, "Groceries", new Money(new BigDecimal("25.30"), Currency.getInstance("IRR")),
                        shoppingCategory, new User(username)
                        ,LocalDate.now(), 0L)
        );

        when(expenseRepository.findByUser_Username(username)).thenReturn(Optional.of(mockExpenses));

        //when
        List<Expense> expenses = expenseMapper.toEntities(expenseService.getExpensesByUsername(username));

        //then
        assertThat(expenses).hasSize(2);
        assertThat(expenses.get(0).getDescription()).isEqualTo("Lunch");
        assertThat(expenses.get(0).getMoney().getAmount()).isEqualByComparingTo("10.50");
        assertThat(expenses.get(0).getCategory().getName()).isEqualTo("Food");
        assertThat(expenses.get(0).getUser().getUsername()).isEqualTo(username);

        assertThat(expenses.get(1).getDescription()).isEqualTo("Groceries");
        assertThat(expenses.get(1).getMoney().getAmount()).isEqualByComparingTo("25.30");
        assertThat(expenses.get(1).getCategory().getName()).isEqualTo("Shopping");
        assertThat(expenses.get(1).getUser().getUsername()).isEqualTo(username);

        verify(expenseRepository, times(1)).findByUser_Username(username);
    }

    @Test
    void shouldReturnEmptyWhenNoExpensesFoundForUsername(){
        String username = "test_user";
        when(expenseRepository.findByUser_Username(username)).thenReturn(Optional.empty());

        List<Expense> expenses = expenseMapper.toEntities(expenseService.getExpensesByUsername(username));

        assertThat(expenses).isEmpty();

        verify(expenseRepository, times(1)).findByUser_Username(username);

    }

    @Test
    void shouldUpdateExistingExpense(){
        //given
        Long expenseId = 1L;

        User user = new User("user1");

        Category existingCategory = new Category(1L,"Food", null);
        Category updatedCategory = new Category(2L,"Dining", null);

        Money existingMoney = new Money(new BigDecimal("10.50"), Currency.getInstance("IRR"));
        Money updatedgMoney = new Money(new BigDecimal("15.00"), Currency.getInstance("USD"));

        Expense existingExpense = new Expense(expenseId, "Lunch",
                        existingMoney, existingCategory, user, LocalDate.now(), 0L);
        Expense updatedExpenseDetails = new Expense(null, "Dinner", updatedgMoney, existingCategory, user, LocalDate.now(), null);

        Expense updatedExpense = new Expense(expenseId, "Dinner", updatedgMoney, updatedCategory, user, LocalDate.now(), 1L);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);
        when(categoryRepository.findById(existingCategory.getId())).thenReturn(Optional.of(updatedCategory));


        //when
        ExpenseResponseDTO result = expenseService.updateExpense(expenseId, expenseMapper.toRequestDTO(updatedExpenseDetails), user.getUsername());

        //then
        assertThat(result.getId()).isEqualTo(expenseId);
        assertThat(result.getDescription()).isEqualTo("Dinner");
        assertThat(result.getMoney().getAmount()).isEqualByComparingTo("15.00");
        assertThat(result.getMoney().getCurrencyCode()).isEqualTo("USD");
        assertThat(result.getCategoryName()).isEqualTo("Dining");
        assertThat(result.getVersion()).isEqualTo(1L);

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void shouldThrowExceptionWhenExpenseNotFound(){
        //given
        Long expenseId = 99L;
        Category category = new Category(2L, "Dining", null);
        Money money = new Money(new BigDecimal("15.00"),Currency.getInstance("IRR"));
        User user = new User("user1");

        Expense updatedExpenseDetails = new Expense(null, "Dinner", money, category, user, LocalDate.now(), null);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new Category()));
        //when then
        assertThatThrownBy(()-> expenseService.updateExpense(expenseId, expenseMapper.toRequestDTO(updatedExpenseDetails),user.getUsername()))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id: " + expenseId);

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void shouldDeleteExistingExpense(){
        //given
        Long expenseId = 1L;

        Category category = new Category(2L, "Dining", null);
        Money money = new Money(new BigDecimal("15.00"),Currency.getInstance("IRR"));
        User user = new User("user1");

        Expense expense = new Expense(null, "Dinner", money, category, user, LocalDate.now(), null);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        //when
        expenseService.deleteExpense(expenseId, "user1");

        //then

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).deleteById(expenseId);

    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingExpense(){
        //given
        Long expenseId = 99L;

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        //when / then
        assertThatThrownBy(() -> expenseService.deleteExpense(expenseId, "username"))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id: " + expenseId);

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).deleteById(expenseId);


    }
}
