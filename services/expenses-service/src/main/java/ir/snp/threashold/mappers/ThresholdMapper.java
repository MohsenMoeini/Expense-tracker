package ir.snp.threashold.mappers;

import ir.snp.expense.dto.MoneyDTO;
import ir.snp.expense.entity.Money;
import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.entity.ExpenseThreshold;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Currency;

@Mapper(componentModel = "spring")
public interface ThresholdMapper {
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "monthlyThreshold.currency", target = "monthlyThreshold.currencyCode", qualifiedByName = "currencyToString")
    @Mapping(source = "totalMonthlyExpenses.currency", target = "totalMonthlyExpenses.currencyCode", qualifiedByName = "currencyToString")
    ThresholdResponseDTO toResponseDTO(ExpenseThreshold expenseThreshold);
    @Mapping(source = "currency.currencyCode", target = "currencyCode", qualifiedByName = "currencyToString")
    MoneyDTO toMoneyDTO(Money money);
    @Named("stringToCurrency")
    static Currency stringToCurrency(String currencyCode){
        return Currency.getInstance(currencyCode);
    }

    @Named("currencyToString")
    static String currencyToString(Currency currency){
        return currency.getCurrencyCode();
    }

}
