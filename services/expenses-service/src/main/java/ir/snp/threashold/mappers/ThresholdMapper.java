package ir.snp.threashold.mappers;

import ir.snp.threashold.controller.ThresholdResponseDTO;
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

    @Named("stringToCurrency")
    static Currency stringToCurrency(String currencyCode){
        return Currency.getInstance(currencyCode);
    }

    @Named("currencyToString")
    static String currencyToString(Currency currency){
        return currency.getCurrencyCode();
    }

}
