package ir.snp.threashold.service;

import ir.snp.expense.entity.Money;
import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.entity.ExpenseThreshold;
import ir.snp.threashold.mappers.ThresholdMapper;
import ir.snp.threashold.repository.ExpenseThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseThresholdService {
    private final ThresholdMapper thresholdMapper;
    private final ExpenseThresholdRepository expenseThresholdRepository;

    public ExpenseThresholdService(ThresholdMapper thresholdMapper, ExpenseThresholdRepository expenseThresholdRepository) {
        this.thresholdMapper = thresholdMapper;
        this.expenseThresholdRepository = expenseThresholdRepository;
    }

    public List<ThresholdResponseDTO> getThresholdsAndAmounts(String username) {
        List<ExpenseThreshold> thresholds =  expenseThresholdRepository.findByUser_Username(username).orElse(Collections.emptyList());
        return thresholds.stream()
                .map(
                        threshold -> {
                            ThresholdResponseDTO dto = new ThresholdResponseDTO();
                            dto.setId(threshold.getId());
                            dto.setCategoryId(threshold.getCategory().getId());
                            dto.setCategoryName(threshold.getCategory().getName());
                            dto.setMonthlyThreshold(thresholdMapper.toMoneyDTO(threshold.getMonthlyCategoryThreshold()));
                            dto.setTotalMonthlyExpenses(thresholdMapper.toMoneyDTO(threshold.getTotalMonthlyExpensesOnCategory()));
                            return dto;
                        }
                ).collect(Collectors.toList());
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
