package com.example.substracker.Controller;

import com.example.substracker.DTO.SpendingAnalysisDTOOut;
import com.example.substracker.Service.SpendingAnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/spending_analysis")
public class SpendingAnalysisController {
    private final SpendingAnalysisService spendingAnalysisService;

    //made by Mohammed
    @GetMapping("/analyze/{userId}")
    public ResponseEntity<?> getSpendingAnalyzerByUserId(@PathVariable Integer userId) {
        return ResponseEntity.status(200)
                .body(spendingAnalysisService.getSpendingAnalysisByUserId(userId));
    }

    @GetMapping("/analyze/{userId}dto")
    public ResponseEntity<SpendingAnalysisDTOOut> getSpendingAnalysisByUserId(@PathVariable Integer userId) {
        SpendingAnalysisDTOOut spendingAnalysisDTOOut = spendingAnalysisService.getSpendingAnalysisDTOOutByUserId(userId);
        return ResponseEntity.ok(spendingAnalysisDTOOut);
    }

    @GetMapping("/get-all-spending")
    public ResponseEntity<List<SpendingAnalysisDTOOut>> getAllSpendingAnalysis() {
        List<SpendingAnalysisDTOOut> allSpendingAnalysis = spendingAnalysisService.getAllSpendingAnalysisDTOOut();
        return ResponseEntity.ok(allSpendingAnalysis);
    }

    /**
     * Check if user's spending ratio is healthy (< 30%)
     */
    //made by Mohammed
    @GetMapping("/user/{userId}/ratio-status")
    public ResponseEntity<?> getSpendingRatioStatus(@PathVariable Integer userId) {
        return ResponseEntity.status(200)
                .body(spendingAnalysisService.getSpendingRatioStatus(userId));
    }

    /**
     * Get average costs breakdown for a user
     */
    //made by Mohammed
    @GetMapping("/user/{userId}/averages")
    public ResponseEntity<?> getAveragesBreakdown(@PathVariable Integer userId) {
        return ResponseEntity.status(200)
                .body(spendingAnalysisService.getAveragesBreakdown(userId));
    }

    /**
     * Get spending projections with different salary scenarios
     */
    //made by Mohammed
    @GetMapping("/user/{userId}/projections")
    public ResponseEntity<?> getSpendingProjections(@PathVariable Integer userId) {
        return ResponseEntity.status(200)
                .body(spendingAnalysisService.getSpendingProjections(userId));
    }

    /**
     * Get spending insights and personalized recommendations
     */
    //made by Mohammed
    @GetMapping("/user/{userId}/insights")
    public ResponseEntity<?> getSpendingInsights(@PathVariable Integer userId) {
        return ResponseEntity.status(200)
                .body(spendingAnalysisService.getSpendingInsights(userId));
    }

    /**
     * Get subscription efficiency metrics and scores
     */
    //made by Mohammed
    @GetMapping("/user/{userId}/efficiency")
    public ResponseEntity<?> getEfficiencyMetrics(@PathVariable Integer userId) {
        return ResponseEntity.status(200)
                .body(spendingAnalysisService.getEfficiencyMetrics(userId));
    }

    /**
     * Get budget analysis with recommendations and thresholds
     */
    //made by Mohammed
    @GetMapping("/user/{userId}/budget-analysis")
    public ResponseEntity<?> getBudgetAnalysis(@PathVariable Integer userId) {
        return ResponseEntity.status(200)
                .body(spendingAnalysisService.getBudgetAnalysis(userId));
    }

}
