package ir.snp.config;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import ir.snp.expense.repository.CategoryRepository;
import ir.snp.expense.repository.ExpenseRepository;
import ir.snp.threashold.entity.ExpenseThreshold;
import ir.snp.threashold.repository.ExpenseThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashSet;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initData(
            CategoryRepository categoryRepository,
            ExpenseRepository expenseRepository,
            ExpenseThresholdRepository thresholdRepository) {
        
        return args -> {
            // Create user
            User moeini = new User("moeini");
            
            // Create categories for moeini
            Category foodCategory = new Category();
            foodCategory.setName("Food");
            foodCategory.setUser(moeini);
            foodCategory.setExpenses(new HashSet<>());
            
            Category transportCategory = new Category();
            transportCategory.setName("Transportation");
            transportCategory.setUser(moeini);
            transportCategory.setExpenses(new HashSet<>());
            
            Category entertainmentCategory = new Category();
            entertainmentCategory.setName("Entertainment");
            entertainmentCategory.setUser(moeini);
            entertainmentCategory.setExpenses(new HashSet<>());
            
            Category utilitiesCategory = new Category();
            utilitiesCategory.setName("Utilities");
            utilitiesCategory.setUser(moeini);
            utilitiesCategory.setExpenses(new HashSet<>());
            
            // Save categories
            foodCategory = categoryRepository.save(foodCategory);
            transportCategory = categoryRepository.save(transportCategory);
            entertainmentCategory = categoryRepository.save(entertainmentCategory);
            utilitiesCategory = categoryRepository.save(utilitiesCategory);
            
            // Create expenses
            Expense groceryExpense = new Expense();
            groceryExpense.setDescription("Weekly grocery shopping");
            groceryExpense.setUser(moeini);
            groceryExpense.setCategory(foodCategory);
            groceryExpense.setDate(LocalDate.now().minusDays(2));
            Money groceryMoney = new Money();
            groceryMoney.setAmount(new BigDecimal("85.50"));
            groceryMoney.setCurrency(Currency.getInstance("USD"));
            groceryExpense.setMoney(groceryMoney);
            
            Expense restaurantExpense = new Expense();
            restaurantExpense.setDescription("Dinner at Italian restaurant");
            restaurantExpense.setUser(moeini);
            restaurantExpense.setCategory(foodCategory);
            restaurantExpense.setDate(LocalDate.now().minusDays(1));
            Money restaurantMoney = new Money();
            restaurantMoney.setAmount(new BigDecimal("45.75"));
            restaurantMoney.setCurrency(Currency.getInstance("USD"));
            restaurantExpense.setMoney(restaurantMoney);
            
            Expense taxiExpense = new Expense();
            taxiExpense.setDescription("Taxi to airport");
            taxiExpense.setUser(moeini);
            taxiExpense.setCategory(transportCategory);
            taxiExpense.setDate(LocalDate.now().minusDays(3));
            Money taxiMoney = new Money();
            taxiMoney.setAmount(new BigDecimal("32.00"));
            taxiMoney.setCurrency(Currency.getInstance("USD"));
            taxiExpense.setMoney(taxiMoney);
            
            Expense movieExpense = new Expense();
            movieExpense.setDescription("Movie tickets");
            movieExpense.setUser(moeini);
            movieExpense.setCategory(entertainmentCategory);
            movieExpense.setDate(LocalDate.now().minusDays(3));
            Money movieMoney = new Money();
            movieMoney.setAmount(new BigDecimal("24.50"));
            movieMoney.setCurrency(Currency.getInstance("USD"));
            movieExpense.setMoney(movieMoney);
            
            Expense electricityExpense = new Expense();
            electricityExpense.setDescription("Monthly electricity bill");
            electricityExpense.setUser(moeini);
            electricityExpense.setCategory(utilitiesCategory);
            electricityExpense.setDate(LocalDate.now().minusDays(4));
            Money electricityMoney = new Money();
            electricityMoney.setAmount(new BigDecimal("75.20"));
            electricityMoney.setCurrency(Currency.getInstance("USD"));
            electricityExpense.setMoney(electricityMoney);
            
            // Save expenses
            expenseRepository.save(groceryExpense);
            expenseRepository.save(restaurantExpense);
            expenseRepository.save(taxiExpense);
            expenseRepository.save(movieExpense);
            expenseRepository.save(electricityExpense);
            
            // Create thresholds
            ExpenseThreshold foodThreshold = new ExpenseThreshold();
            foodThreshold.setUser(moeini);
            foodThreshold.setCategory(foodCategory);
            Money foodThresholdMoney = new Money();
            foodThresholdMoney.setAmount(new BigDecimal("300.00"));
            foodThresholdMoney.setCurrency(Currency.getInstance("USD"));
            foodThreshold.setMonthlyCategoryThreshold(foodThresholdMoney);
            Money foodTotalMoney = new Money();
            foodTotalMoney.setAmount(new BigDecimal("131.25")); // sum of food expenses
            foodTotalMoney.setCurrency(Currency.getInstance("USD"));
            foodThreshold.setTotalMonthlyExpensesOnCategory(foodTotalMoney);
            
            ExpenseThreshold transportThreshold = new ExpenseThreshold();
            transportThreshold.setUser(moeini);
            transportThreshold.setCategory(transportCategory);
            Money transportThresholdMoney = new Money();
            transportThresholdMoney.setAmount(new BigDecimal("150.00"));
            transportThresholdMoney.setCurrency(Currency.getInstance("USD"));
            transportThreshold.setMonthlyCategoryThreshold(transportThresholdMoney);
            Money transportTotalMoney = new Money();
            transportTotalMoney.setAmount(new BigDecimal("32.00")); // taxi expense
            transportTotalMoney.setCurrency(Currency.getInstance("USD"));
            transportThreshold.setTotalMonthlyExpensesOnCategory(transportTotalMoney);
            
            ExpenseThreshold entertainmentThreshold = new ExpenseThreshold();
            entertainmentThreshold.setUser(moeini);
            entertainmentThreshold.setCategory(entertainmentCategory);
            Money entertainmentThresholdMoney = new Money();
            entertainmentThresholdMoney.setAmount(new BigDecimal("100.00"));
            entertainmentThresholdMoney.setCurrency(Currency.getInstance("USD"));
            entertainmentThreshold.setMonthlyCategoryThreshold(entertainmentThresholdMoney);
            Money entertainmentTotalMoney = new Money();
            entertainmentTotalMoney.setAmount(new BigDecimal("24.50")); // movie expense
            entertainmentTotalMoney.setCurrency(Currency.getInstance("USD"));
            entertainmentThreshold.setTotalMonthlyExpensesOnCategory(entertainmentTotalMoney);
            
            // Save thresholds
            thresholdRepository.save(foodThreshold);
            thresholdRepository.save(transportThreshold);
            thresholdRepository.save(entertainmentThreshold);
            
            System.out.println("Sample data initialized for user 'moeini'");
        };
    }
}
