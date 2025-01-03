package ir.snp.threashold.controller;

import ir.snp.threashold.dto.ThresholdResponseDTO;
import ir.snp.threashold.service.ExpenseThresholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/thresholds")
@Validated
public class ThresholdController {
    private final ExpenseThresholdService expenseThresholdService;

    @Autowired
    public ThresholdController(ExpenseThresholdService expenseThresholdService) {
        this.expenseThresholdService = expenseThresholdService;
    }

    @GetMapping("/get-thresholds")
    public ResponseEntity<List<ThresholdResponseDTO>> getThresholds(@AuthenticationPrincipal Jwt jwtToken){
        String username = jwtToken.getClaimAsString("preferred_username");
        List<ThresholdResponseDTO> thresholds = expenseThresholdService.getThresholdsAndAmounts(username);
        return ResponseEntity.ok(thresholds);
    }


}
