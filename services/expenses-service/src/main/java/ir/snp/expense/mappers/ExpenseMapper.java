package ir.snp.expense.mappers;

import ir.snp.expense.dto.CategoryDTO;
import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.entity.Category;
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
    @Mapping(source = "category.name", target = "category.name")
    @Mapping(source = "category.id", target = "category.id")
    Expense toEntity(ExpenseResponseDTO dto);

    List<Expense> toEntities(List<ExpenseResponseDTO> DTOs);

    @Mapping(source = "money.currency", target = "money.currencyCode", qualifiedByName = "currencyToString")
    @Mapping(source = "category.id", target = "categoryId")
    ExpenseRequestDTO toRequestDTO(Expense expense);

    @Mapping(source = "money.currency", target = "money.currencyCode", qualifiedByName = "currencyToString")
    @Mapping(source = "category", target = "category", qualifiedByName = "categoryToCategoryDTO")
    @Mapping(source = "user.username", target = "username")
    ExpenseResponseDTO toResponseDTO(Expense expense);

    @Mapping(source = "money.currency", target = "money.currencyCode", qualifiedByName = "currencyToString")
    @Mapping(source = "category", target = "category", qualifiedByName = "categoryToCategoryDTO")
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
    
    @Named("categoryToCategoryDTO")
    static CategoryDTO categoryToCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(category.getId(), category.getName());
    }
}
