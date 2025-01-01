package ir.snp.expense.mappers;

import ir.snp.expense.dto.ExpenseDTO;
import ir.snp.expense.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Currency;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "money.currencyCode", target = "money.currency", qualifiedByName = "stringToCurrency")
    @Mapping(target = "user.username", ignore = true)
    Expense toEntity(ExpenseDTO dto);
    @Mapping(source = "money.currency", target = "money.currencyCode", qualifiedByName = "currencyToString")
    ExpenseDTO toDTO(Expense expense);
    @Named("stringToCurrency")
    static Currency stringToCurrency(String currencyCode){
        return Currency.getInstance(currencyCode);
    }

    @Named("currencyToString")
    static String currencyToString(Currency currency){
        return currency.getCurrencyCode();
    }

}
