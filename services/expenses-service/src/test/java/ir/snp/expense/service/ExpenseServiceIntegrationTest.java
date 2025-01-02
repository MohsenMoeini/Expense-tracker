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
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class ExpenseServiceIntegrationTest {
    @MockitoBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseMapper expenseMapper;

    private Category entertainmentCategory;
    private Category foodCategory ;
    private Category transportCategory;
    private Category existingCategory;
    private Category updatedCategory;

    @BeforeEach
    void setup(){
        mockJwt();
        initCategories();
    }

    private void initCategories() {
        entertainmentCategory = new Category();
        foodCategory = new Category();
        transportCategory = new Category();
        existingCategory = new Category();
        updatedCategory = new Category();

        entertainmentCategory.setName("Entertainment");
        foodCategory.setName("Food");
        transportCategory.setName("Transport");
        existingCategory.setName("Dining");
        updatedCategory.setName("Restaurant");

        entertainmentCategory = categoryRepository.save(entertainmentCategory);
        foodCategory = categoryRepository.save(foodCategory);
        transportCategory = categoryRepository.save(transportCategory);
        existingCategory = categoryRepository.save(existingCategory);
        updatedCategory = categoryRepository.save(updatedCategory);
    }

    private void mockJwt(){
        Jwt mockJwt = Jwt.withTokenValue("test-token")
                .headers(stringObjectMap -> stringObjectMap.put("Alg","123"))
                .claim("preferred-username", "test-username")
                .build();
        when(jwtDecoder.decode(any())).thenReturn(mockJwt);
    }

    @Test
    @DisplayName("should create an expense and persist it to the database")
    public void testCreateExpense(){
        //given


        Money money = new Money(new BigDecimal("25.00"), Currency.getInstance("IRR"));

        Expense expense = new Expense();

        expense.setDescription("Movie Tickets");
        expense.setMoney(money);
        expense.setCategory(entertainmentCategory);
        expense.setDate(LocalDate.now());

        ExpenseRequestDTO expenseRequestDTO = expenseMapper.toRequestDTO(expense);

        //when
        ExpenseResponseDTO createdExpense = expenseService.createExpense(expenseRequestDTO, "user1");

        //then
        assertThat(createdExpense.getId()).isNotNull();
        assertThat(createdExpense.getCategoryName()).isEqualTo("Entertainment");
        assertThat(createdExpense.getDescription()).isEqualTo("Movie Tickets");
        assertThat(createdExpense.getMoney().getAmount()).isEqualByComparingTo("25.00");
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

        Expense expense1 = new Expense(null, "Lunch", new Money(new BigDecimal("30.00"),Currency.getInstance("IRR")),foodCategory ,new User(userId) ,LocalDate.now(), null);
        Expense expense2 = new Expense(null, "Taxi", new Money(new BigDecimal("10.00"), Currency.getInstance("IRR")), transportCategory, new User(userId), LocalDate.now(), null);
        expenseRepository.saveAll(List.of(expense1, expense2));

        //when
        List<Expense> expenses = expenseMapper.toEntities(expenseService.getExpensesByUsername(userId));

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



        Money existingMoney = new Money(new BigDecimal("20.00"), Currency.getInstance("IRR"));
        Money updatedgMoney = new Money(new BigDecimal("45.00"), Currency.getInstance("USD"));

        User user = new User(userId);

        Expense expense = new Expense(null, "Dinner", existingMoney, existingCategory, user, LocalDate.now(), null);
        Expense savedExpense = expenseRepository.save(expense);
        Expense updatedExpense = new Expense(null, "Dinner at restaurant", updatedgMoney, updatedCategory, new User(userId), LocalDate.now(), null);

        //when
        ExpenseResponseDTO result = expenseService.updateExpense(savedExpense.getId(), expenseMapper.toRequestDTO(updatedExpense),user.getUsername());

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

        Expense updatedExpense = new Expense(null, "NonExistent", money, transportCategory, user1, LocalDate.now(), null);

        //when and then
        assertThatThrownBy(() -> expenseService.updateExpense(nonExistentId, expenseMapper.toRequestDTO(updatedExpense),user1.getUsername()))
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
