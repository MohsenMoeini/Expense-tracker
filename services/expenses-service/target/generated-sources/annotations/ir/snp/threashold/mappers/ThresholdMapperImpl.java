package ir.snp.threashold.mappers;

import ir.snp.expense.dto.MoneyDTO;
import ir.snp.expense.entity.Money;
import java.util.Currency;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-04T14:10:41+0330",
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
}
