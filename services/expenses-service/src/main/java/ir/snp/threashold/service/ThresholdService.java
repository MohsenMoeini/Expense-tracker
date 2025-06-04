package ir.snp.threashold.service;

import ir.snp.expense.entity.Category;
import ir.snp.expense.entity.Money;
import ir.snp.expense.entity.User;
import ir.snp.expense.exception.CategoryNotFoundException;
import ir.snp.expense.exception.UnauthorizedActionException;
import ir.snp.expense.repository.CategoryRepository;
import ir.snp.threashold.dto.ThresholdRequestDTO;
import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.entity.ExpenseThreshold;
import ir.snp.threashold.exception.ThresholdNotFoundException;
import ir.snp.threashold.mappers.ThresholdMapper;
import ir.snp.threashold.repository.ExpenseThresholdRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
public class ThresholdService {
    private final ExpenseThresholdRepository thresholdRepository;
    private final CategoryRepository categoryRepository;
    private final ThresholdMapper thresholdMapper;
    
    // Default currency for new thresholds
    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("USD");

    @Autowired
    public ThresholdService(ExpenseThresholdRepository thresholdRepository, 
                           CategoryRepository categoryRepository,
                           ThresholdMapper thresholdMapper) {
        this.thresholdRepository = thresholdRepository;
        this.categoryRepository = categoryRepository;
        this.thresholdMapper = thresholdMapper;
    }

    public List<ThresholdResponseDTO> getAllThresholds(String username) {
        List<ExpenseThreshold> thresholds = thresholdRepository.findByUser_Username(username)
                .orElse(List.of());
        return thresholdMapper.toResponseDTOs(thresholds);
    }

    public ThresholdResponseDTO getThresholdById(Long thresholdId, String username) {
        ExpenseThreshold threshold = thresholdRepository.findById(thresholdId)
                .orElseThrow(() -> new ThresholdNotFoundException(thresholdId));

        if (!threshold.getUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You do not have permission to view this threshold");
        }

        return thresholdMapper.toResponseDTO(threshold);
    }

    @Transactional
    public ThresholdResponseDTO createThreshold(ThresholdRequestDTO requestDTO, String username) {
        Category category = categoryRepository.findByIdAndUser_Username(requestDTO.getCategoryId(), username)
                .orElseThrow(() -> new CategoryNotFoundException(requestDTO.getCategoryId()));

        // Check if threshold already exists for this category and user
        if (thresholdRepository.findByUser_UsernameAndCategory(username, category).isPresent()) {
            throw new IllegalStateException("A threshold for this category already exists");
        }

        ExpenseThreshold threshold = thresholdMapper.createThresholdEntity(requestDTO, category, username, DEFAULT_CURRENCY);
        ExpenseThreshold savedThreshold = thresholdRepository.save(threshold);
        return thresholdMapper.toResponseDTO(savedThreshold);
    }

    @Transactional
    public ThresholdResponseDTO updateThreshold(Long thresholdId, ThresholdRequestDTO requestDTO, String username) {
        ExpenseThreshold threshold = thresholdRepository.findById(thresholdId)
                .orElseThrow(() -> new ThresholdNotFoundException(thresholdId));

        if (!threshold.getUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You do not have permission to update this threshold");
        }

        Category category = categoryRepository.findByIdAndUser_Username(requestDTO.getCategoryId(), username)
                .orElseThrow(() -> new CategoryNotFoundException(requestDTO.getCategoryId()));

        // Update threshold amount but keep the existing currency
        Money thresholdMoney = threshold.getMonthlyCategoryThreshold();
        thresholdMoney.setAmount(requestDTO.getThresholdAmount());
        // Currency is preserved from the existing threshold
        
        // If category is changing, update the category
        if (!threshold.getCategory().getId().equals(category.getId())) {
            threshold.setCategory(category);
        }

        ExpenseThreshold updatedThreshold = thresholdRepository.save(threshold);
        return thresholdMapper.toResponseDTO(updatedThreshold);
    }

    @Transactional
    public void deleteThreshold(Long thresholdId, String username) {
        ExpenseThreshold threshold = thresholdRepository.findById(thresholdId)
                .orElseThrow(() -> new ThresholdNotFoundException(thresholdId));

        if (!threshold.getUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You do not have permission to delete this threshold");
        }

        thresholdRepository.delete(threshold);
    }

    @Transactional
    public void resetMonthlyExpenses(String username) {
        List<ExpenseThreshold> thresholds = thresholdRepository.findByUser_Username(username)
                .orElse(List.of());
        
        for (ExpenseThreshold threshold : thresholds) {
            Money totalExpenses = threshold.getTotalMonthlyExpensesOnCategory();
            totalExpenses.setAmount(BigDecimal.ZERO);
            thresholdRepository.save(threshold);
        }
    }
}
