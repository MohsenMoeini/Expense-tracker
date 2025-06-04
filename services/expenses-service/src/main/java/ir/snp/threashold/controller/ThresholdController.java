package ir.snp.threashold.controller;

import ir.snp.threashold.dto.ThresholdRequestDTO;
import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.service.ExpenseThresholdService;
import ir.snp.threashold.service.ThresholdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thresholds")
@Validated
public class ThresholdController {
    private final ExpenseThresholdService expenseThresholdService;
    private final ThresholdService thresholdService;

    @Autowired
    public ThresholdController(ExpenseThresholdService expenseThresholdService, ThresholdService thresholdService) {
        this.expenseThresholdService = expenseThresholdService;
        this.thresholdService = thresholdService;
    }

    @GetMapping("/get-thresholds")
    public ResponseEntity<List<ThresholdResponseDTO>> getThresholds(@AuthenticationPrincipal Jwt jwtToken){
        String username = jwtToken.getClaimAsString("preferred_username");
        List<ThresholdResponseDTO> thresholds = expenseThresholdService.getThresholdsAndAmounts(username);
        return ResponseEntity.ok(thresholds);
    }
    
    @GetMapping("/{thresholdId}")
    public ResponseEntity<ThresholdResponseDTO> getThresholdById(
            @PathVariable Long thresholdId,
            @AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        ThresholdResponseDTO threshold = thresholdService.getThresholdById(thresholdId, username);
        return ResponseEntity.ok(threshold);
    }
    
    @PostMapping
    public ResponseEntity<ThresholdResponseDTO> createThreshold(
            @Valid @RequestBody ThresholdRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        ThresholdResponseDTO createdThreshold = thresholdService.createThreshold(requestDTO, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdThreshold);
    }
    
    @PutMapping("/{thresholdId}")
    public ResponseEntity<ThresholdResponseDTO> updateThreshold(
            @PathVariable Long thresholdId,
            @Valid @RequestBody ThresholdRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        ThresholdResponseDTO updatedThreshold = thresholdService.updateThreshold(thresholdId, requestDTO, username);
        return ResponseEntity.ok(updatedThreshold);
    }
    
    @DeleteMapping("/{thresholdId}")
    public ResponseEntity<Void> deleteThreshold(
            @PathVariable Long thresholdId,
            @AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        thresholdService.deleteThreshold(thresholdId, username);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/reset-monthly-expenses")
    public ResponseEntity<Void> resetMonthlyExpenses(@AuthenticationPrincipal Jwt jwtToken) {
        String username = jwtToken.getClaimAsString("preferred_username");
        thresholdService.resetMonthlyExpenses(username);
        return ResponseEntity.ok().build();
    }
}
