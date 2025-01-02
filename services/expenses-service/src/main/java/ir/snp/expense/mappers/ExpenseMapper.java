package ir.snp.expense.mappers;

import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Currency;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(source = "money.currencyCode", target = "money.currency", qualifiedByName = "stringToCurrency")
    @Mapping(target = "user.username", ignore = true)
    @Mapping(target = "category.id", ignore = true)
    Expense toEntity(ExpenseRequestDTO dto);

    @Mapping(source = "money.currencyCode", target = "money.currency", qualifiedByName = "stringToCurrency")
    @Mapping(source = "username", target = "user.username")
    @Mapping(source = "categoryName", target = "category.name")
    Expense toEntity(ExpenseResponseDTO dto);

    List<Expense> toEntities(List<ExpenseResponseDTO> DTOs);

    @Mapping(source = "money.currency", target = "money.currencyCode", qualifiedByName = "currencyToString")
    @Mapping(source = "category.id", target = "categoryId")
    ExpenseRequestDTO toRequestDTO(Expense expense);

    @Mapping(source = "money.currency", target = "money.currencyCode", qualifiedByName = "currencyToString")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "user.username", target = "username")
    ExpenseResponseDTO toResponseDTO(Expense expense);

    @Mapping(source = "money.currency", target = "money.currencyCode", qualifiedByName = "currencyToString")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "user.username", target = "username")
    List<ExpenseResponseDTO> toResponseDTOs(List<Expense> expenses);

    @Named("stringToCurrency")
    static Currency stringToCurrency(String currencyCode){
        return Currency.getInstance(currencyCode);
    }

    @Named("currencyToString")
    static String currencyToString(Currency currency){
        return currency.getCurrencyCode();
    }

}
