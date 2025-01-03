package ir.snp.threashold.service;

import ir.snp.threashold.entity.ExpenseThreshold;
import ir.snp.threashold.repository.ExpenseThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseThresholdService {
    private final ExpenseThresholdRepository expenseThresholdRepository;

    public ExpenseThresholdService(ExpenseThresholdRepository expenseThresholdRepository) {
        this.expenseThresholdRepository = expenseThresholdRepository;
    }

    @Scheduled(cron = "0 0 1 * * ?") // reset thresholds on starting day of each month
    @Transactional
    public void resetMonthlyExpenses(){
        List<ExpenseThreshold> thresholds = expenseThresholdRepository.findAll();
        for (ExpenseThreshold threshold : thresholds) {
            threshold.setTotalMonthlyExpenses(0);
            expenseThresholdRepository.save(threshold);
        }
    }
}
