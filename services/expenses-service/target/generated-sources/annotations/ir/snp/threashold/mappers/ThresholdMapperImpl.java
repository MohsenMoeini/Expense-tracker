package ir.snp.threashold.mappers;

import ir.snp.expense.dto.MoneyDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Money;
import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.entity.ExpenseThreshold;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-04T16:13:42+0330",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class ThresholdMapperImpl implements ThresholdMapper {

    @Override
    public MoneyDTO toMoneyDTO(Money money) {
        if ( money == null ) {
            return null;
        }

        MoneyDTO moneyDTO = new MoneyDTO();

        String currencyCode = moneyCurrencyCurrencyCode( money );
        if ( currencyCode != null ) {
            moneyDTO.setCurrencyCode( ThresholdMapper.currencyToString( Currency.getInstance( currencyCode ) ) );
        }
        moneyDTO.setAmount( money.getAmount() );

        return moneyDTO;
    }

    @Override
    public ThresholdResponseDTO toResponseDTO(ExpenseThreshold threshold) {
        if ( threshold == null ) {
            return null;
        }

        ThresholdResponseDTO thresholdResponseDTO = new ThresholdResponseDTO();

        thresholdResponseDTO.setCategoryId( thresholdCategoryId( threshold ) );
        thresholdResponseDTO.setCategoryName( thresholdCategoryName( threshold ) );
        thresholdResponseDTO.setMonthlyThreshold( toMoneyDTO( threshold.getMonthlyCategoryThreshold() ) );
        thresholdResponseDTO.setTotalMonthlyExpenses( toMoneyDTO( threshold.getTotalMonthlyExpensesOnCategory() ) );
        thresholdResponseDTO.setId( threshold.getId() );

        return thresholdResponseDTO;
    }

    @Override
    public List<ThresholdResponseDTO> toResponseDTOs(List<ExpenseThreshold> thresholds) {
        if ( thresholds == null ) {
            return null;
        }

        List<ThresholdResponseDTO> list = new ArrayList<ThresholdResponseDTO>( thresholds.size() );
        for ( ExpenseThreshold expenseThreshold : thresholds ) {
            list.add( toResponseDTO( expenseThreshold ) );
        }

        return list;
    }

    private String moneyCurrencyCurrencyCode(Money money) {
        if ( money == null ) {
            return null;
        }
        Currency currency = money.getCurrency();
        if ( currency == null ) {
            return null;
        }
        String currencyCode = currency.getCurrencyCode();
        if ( currencyCode == null ) {
            return null;
        }
        return currencyCode;
    }

    private Long thresholdCategoryId(ExpenseThreshold expenseThreshold) {
        if ( expenseThreshold == null ) {
            return null;
        }
        Category category = expenseThreshold.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String thresholdCategoryName(ExpenseThreshold expenseThreshold) {
        if ( expenseThreshold == null ) {
            return null;
        }
        Category category = expenseThreshold.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
