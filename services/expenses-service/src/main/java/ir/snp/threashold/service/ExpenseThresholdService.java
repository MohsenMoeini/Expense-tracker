package ir.snp.threashold.service;

import ir.snp.expense.entity.Money;
import ir.snp.expense.repository.ExpenseRepository;
import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.entity.ExpenseThreshold;
import ir.snp.threashold.mappers.ThresholdMapper;
import ir.snp.threashold.repository.ExpenseThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseThresholdService {
    private final ExpenseRepository expenseRepository;
    private final ThresholdMapper thresholdMapper;
    private final ExpenseThresholdRepository expenseThresholdRepository;

    public ExpenseThresholdService(ExpenseRepository expenseRepository, ThresholdMapper thresholdMapper, ExpenseThresholdRepository expenseThresholdRepository) {
        this.expenseRepository = expenseRepository;
        this.thresholdMapper = thresholdMapper;
        this.expenseThresholdRepository = expenseThresholdRepository;
    }

    public List<ThresholdResponseDTO> getThresholdsAndAmounts(String username) {
        List<ExpenseThreshold> thresholds =  expenseThresholdRepository.findByUser_Username(username).orElse(Collections.emptyList());
        return thresholds.stream()
                .map(threshold -> {
                    LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
                    LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
                    BigDecimal totalExpenses = expenseRepository
                            .findByUser_UsernameAndCategoryAndDateBetween(username, threshold.getCategory(), startOfMonth, endOfMonth)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(expense -> expense.getMoney().getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    ThresholdResponseDTO dto = new ThresholdResponseDTO();
                    dto.setCategoryName(threshold.getCategory().getName());
                    dto.setMonthlyThreshold(thresholdMapper.toMoneyDTO(threshold.getMonthlyCategoryThreshold()));
                    dto.setTotalMonthlyExpenses(thresholdMapper.toMoneyDTO(new Money(totalExpenses, threshold.getTotalMonthlyExpensesOnCategory().getCurrency())));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 1 * * ?") // reset thresholds on starting day of each month
    @Transactional
    public void resetMonthlyExpenses() {
        List<ExpenseThreshold> thresholds = expenseThresholdRepository.findAll();
        for (ExpenseThreshold threshold : thresholds) {
            Money totalMonthlyExpenses = threshold.getTotalMonthlyExpensesOnCategory();
            totalMonthlyExpenses.setAmount(BigDecimal.valueOf(0));
            threshold.setTotalMonthlyExpensesOnCategory(totalMonthlyExpenses);
            expenseThresholdRepository.save(threshold);
        }
    }
}
