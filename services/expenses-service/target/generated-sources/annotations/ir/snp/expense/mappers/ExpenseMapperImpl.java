package ir.snp.expense.mappers;

import ir.snp.expense.dto.CategoryDTO;
import ir.snp.expense.dto.ExpenseRequestDTO;
import ir.snp.expense.dto.ExpenseResponseDTO;
import ir.snp.expense.dto.MoneyDTO;
import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Expense;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-04T14:10:41+0330",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Override
    public Expense toEntity(ExpenseRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Expense expense = new Expense();

        expense.setMoney( moneyDTOToMoney( dto.getMoney() ) );
        expense.setDescription( dto.getDescription() );
        expense.setDate( dto.getDate() );
        expense.setUser( expenseRequestDTOToUser( dto ) );
        expense.setCategory( expenseRequestDTOToCategory( dto ) );

        return expense;
    }

    @Override
    public Expense toEntity(ExpenseResponseDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Expense expense = new Expense();

        expense.setMoney( moneyDTOToMoney1( dto.getMoney() ) );
        expense.setUser( expenseResponseDTOToUser( dto ) );
        expense.setCategory( categoryDTOToCategory( dto.getCategory() ) );
        expense.setId( dto.getId() );
        expense.setDescription( dto.getDescription() );
        expense.setDate( dto.getDate() );
        expense.setVersion( dto.getVersion() );

        return expense;
    }

    @Override
    public List<Expense> toEntities(List<ExpenseResponseDTO> DTOs) {
        if ( DTOs == null ) {
            return null;
        }

        List<Expense> list = new ArrayList<Expense>( DTOs.size() );
        for ( ExpenseResponseDTO expenseResponseDTO : DTOs ) {
            list.add( toEntity( expenseResponseDTO ) );
        }

        return list;
    }

    @Override
    public ExpenseRequestDTO toRequestDTO(Expense expense) {
        if ( expense == null ) {
            return null;
        }

        ExpenseRequestDTO expenseRequestDTO = new ExpenseRequestDTO();

        expenseRequestDTO.setMoney( moneyToMoneyDTO( expense.getMoney() ) );
        expenseRequestDTO.setCategoryId( expenseCategoryId( expense ) );
        expenseRequestDTO.setDescription( expense.getDescription() );
        expenseRequestDTO.setDate( expense.getDate() );

        return expenseRequestDTO;
    }

    @Override
    public ExpenseResponseDTO toResponseDTO(Expense expense) {
        if ( expense == null ) {
            return null;
        }

        ExpenseResponseDTO expenseResponseDTO = new ExpenseResponseDTO();

        expenseResponseDTO.setMoney( moneyToMoneyDTO1( expense.getMoney() ) );
        expenseResponseDTO.setCategory( ExpenseMapper.categoryToCategoryDTO( expense.getCategory() ) );
        expenseResponseDTO.setUsername( expenseUserUsername( expense ) );
        expenseResponseDTO.setId( expense.getId() );
        expenseResponseDTO.setDescription( expense.getDescription() );
        expenseResponseDTO.setDate( expense.getDate() );
        expenseResponseDTO.setVersion( expense.getVersion() );

        return expenseResponseDTO;
    }

    @Override
    public List<ExpenseResponseDTO> toResponseDTOs(List<Expense> expenses) {
        if ( expenses == null ) {
            return null;
        }

        List<ExpenseResponseDTO> list = new ArrayList<ExpenseResponseDTO>( expenses.size() );
        for ( Expense expense : expenses ) {
            list.add( toResponseDTO( expense ) );
        }

        return list;
    }

    protected Money moneyDTOToMoney(MoneyDTO moneyDTO) {
        if ( moneyDTO == null ) {
            return null;
        }

        Money money = new Money();

        money.setCurrency( ExpenseMapper.stringToCurrency( moneyDTO.getCurrencyCode() ) );
        money.setAmount( moneyDTO.getAmount() );

        return money;
    }

    protected User expenseRequestDTOToUser(ExpenseRequestDTO expenseRequestDTO) {
        if ( expenseRequestDTO == null ) {
            return null;
        }

        String username = null;

        User user = new User( username );

        return user;
    }

    protected Category expenseRequestDTOToCategory(ExpenseRequestDTO expenseRequestDTO) {
        if ( expenseRequestDTO == null ) {
            return null;
        }

        Category category = new Category();

        return category;
    }

    protected Money moneyDTOToMoney1(MoneyDTO moneyDTO) {
        if ( moneyDTO == null ) {
            return null;
        }

        Money money = new Money();

        money.setCurrency( ExpenseMapper.stringToCurrency( moneyDTO.getCurrencyCode() ) );
        money.setAmount( moneyDTO.getAmount() );

        return money;
    }

    protected User expenseResponseDTOToUser(ExpenseResponseDTO expenseResponseDTO) {
        if ( expenseResponseDTO == null ) {
            return null;
        }

        String username = null;

        username = expenseResponseDTO.getUsername();

        User user = new User( username );

        return user;
    }

    protected Category categoryDTOToCategory(CategoryDTO categoryDTO) {
        if ( categoryDTO == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( categoryDTO.getName() );
        category.setId( categoryDTO.getId() );

        return category;
    }

    protected MoneyDTO moneyToMoneyDTO(Money money) {
        if ( money == null ) {
            return null;
        }

        MoneyDTO moneyDTO = new MoneyDTO();

        moneyDTO.setCurrencyCode( ExpenseMapper.currencyToString( money.getCurrency() ) );
        moneyDTO.setAmount( money.getAmount() );

        return moneyDTO;
    }

    private Long expenseCategoryId(Expense expense) {
        if ( expense == null ) {
            return null;
        }
        Category category = expense.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected MoneyDTO moneyToMoneyDTO1(Money money) {
        if ( money == null ) {
            return null;
        }

        MoneyDTO moneyDTO = new MoneyDTO();

        moneyDTO.setCurrencyCode( ExpenseMapper.currencyToString( money.getCurrency() ) );
        moneyDTO.setAmount( money.getAmount() );

        return moneyDTO;
    }

    private String expenseUserUsername(Expense expense) {
        if ( expense == null ) {
            return null;
        }
        User user = expense.getUser();
        if ( user == null ) {
            return null;
        }
        String username = user.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }
}
