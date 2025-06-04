package ir.snp.threashold.mappers;

import ir.snp.expense.dto.MoneyDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import ir.snp.threashold.dto.ThresholdRequestDTO;
import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.entity.ExpenseThreshold;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Currency;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ThresholdMapper {
    @Mapping(source = "currency.currencyCode", target = "currencyCode", qualifiedByName = "currencyToString")
    MoneyDTO toMoneyDTO(Money money);

    @Named("currencyToString")
    static String currencyToString(Currency currency){
        return currency.getCurrencyCode();
    }
    
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "monthlyCategoryThreshold", target = "monthlyThreshold")
    @Mapping(source = "totalMonthlyExpensesOnCategory", target = "totalMonthlyExpenses")
    ThresholdResponseDTO toResponseDTO(ExpenseThreshold threshold);
    
    List<ThresholdResponseDTO> toResponseDTOs(List<ExpenseThreshold> thresholds);
    
    default Money createMoneyFromRequest(ThresholdRequestDTO requestDTO, Currency currency) {
        Money money = new Money();
        money.setAmount(requestDTO.getThresholdAmount());
        money.setCurrency(currency);
        return money;
    }
    
    default ExpenseThreshold createThresholdEntity(ThresholdRequestDTO requestDTO, Category category, String username, Currency defaultCurrency) {
        ExpenseThreshold threshold = new ExpenseThreshold();
        threshold.setCategory(category);
        threshold.setUser(new User(username));
        
        Money thresholdMoney = new Money();
        thresholdMoney.setAmount(requestDTO.getThresholdAmount());
        thresholdMoney.setCurrency(defaultCurrency);
        threshold.setMonthlyCategoryThreshold(thresholdMoney);
        
        Money totalExpensesMoney = new Money();
        totalExpensesMoney.setAmount(java.math.BigDecimal.ZERO);
        totalExpensesMoney.setCurrency(defaultCurrency);
        threshold.setTotalMonthlyExpensesOnCategory(totalExpensesMoney);
        
        return threshold;
    }
}
