package com.example.substracker.Service;

import com.example.substracker.API.ApiException;
import com.example.substracker.DTO.AiAnalysisDTOOut;
import com.example.substracker.DTO.SpendingAnalysisDTOOut;
import com.example.substracker.Model.AiAnalysis;
import com.example.substracker.Model.SpendingAnalysis;
import com.example.substracker.Model.Subscription;
import com.example.substracker.Model.User;
import com.example.substracker.Repository.SpendingAnalysisRepository;
import com.example.substracker.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SpendingAnalysisService {
    private final SpendingAnalysisRepository spendingAnalysisRepository;
    private final UserRepository userRepository;
    private final AiAnalysisService aiAnalysisService;

    //there is No Delete in spending Analysis

    public SpendingAnalysisDTOOut getSpendingAnalysisByUserId(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user == null) throw new ApiException("User not found");
        SpendingAnalysis sa = user.getSpendingAnalysis();
        if(sa == null) throw new ApiException("User spending analysis not found (no subscriptions yet)");

        AiAnalysis ai = sa.getAiAnalysis();
        AiAnalysisDTOOut aiDto = (ai != null) ? new AiAnalysisDTOOut(ai.getGeneralRecommendations()) : null;

        return new SpendingAnalysisDTOOut(
                sa.getDigitalSubscriptionsTotalPrice(),
                sa.getServiceSubscriptionsTotalPrice(),
                sa.getTotalSpendingPrice(),
                sa.getAverageSubscriptionCost(),
                sa.getSpendingToIncomeRatio(),
                sa.getTotalSubscriptionsCount(),
                sa.getDigitalSubscriptionsCount(),
                sa.getServiceSubscriptionsCount(),
                aiDto
        );
    }

    public SpendingAnalysisDTOOut getSpendingAnalysisDTOOutByUserId(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }
        if(user.getSpendingAnalysis() == null){
            throw new ApiException("User spending analysis not found because he have not subscriptions yet");
        }

        SpendingAnalysis spendingAnalysis = user.getSpendingAnalysis();

        // Convert AiAnalysis to DTO
        AiAnalysisDTOOut aiAnalysisDTOOut = null;
        AiAnalysis aiAnalysis = spendingAnalysis.getAiAnalysis();
        if(aiAnalysis != null) {
            aiAnalysisDTOOut = new AiAnalysisDTOOut(
                    aiAnalysis.getGeneralRecommendations()
            );
        }

        return new SpendingAnalysisDTOOut(
                spendingAnalysis.getDigitalSubscriptionsTotalPrice(),
                spendingAnalysis.getServiceSubscriptionsTotalPrice(),
                spendingAnalysis.getTotalSpendingPrice(),
                spendingAnalysis.getAverageSubscriptionCost(),
                spendingAnalysis.getSpendingToIncomeRatio(),
                spendingAnalysis.getTotalSubscriptionsCount(),
                spendingAnalysis.getDigitalSubscriptionsCount(),
                spendingAnalysis.getServiceSubscriptionsCount(),
                aiAnalysisDTOOut
        );
    }

    public List<SpendingAnalysisDTOOut> getAllSpendingAnalysisDTOOut(){
        ArrayList<SpendingAnalysisDTOOut> spendingAnalysisDTOOuts = new ArrayList<>();

        for(SpendingAnalysis spendingAnalysis : spendingAnalysisRepository.findAll()) {
            // Convert AiAnalysis to DTO
            AiAnalysisDTOOut aiAnalysisDTOOut = null;
            AiAnalysis aiAnalysis = spendingAnalysis.getAiAnalysis();
            if(aiAnalysis != null) {
                aiAnalysisDTOOut = new AiAnalysisDTOOut(
                        aiAnalysis.getGeneralRecommendations()
                );
            }

            SpendingAnalysisDTOOut spendingAnalysisDTOOut = new SpendingAnalysisDTOOut(
                    spendingAnalysis.getDigitalSubscriptionsTotalPrice(),
                    spendingAnalysis.getServiceSubscriptionsTotalPrice(),
                    spendingAnalysis.getTotalSpendingPrice(),
                    spendingAnalysis.getAverageSubscriptionCost(),
                    spendingAnalysis.getSpendingToIncomeRatio(),
                    spendingAnalysis.getTotalSubscriptionsCount(),
                    spendingAnalysis.getDigitalSubscriptionsCount(),
                    spendingAnalysis.getServiceSubscriptionsCount(),
                    aiAnalysisDTOOut
            );
            spendingAnalysisDTOOuts.add(spendingAnalysisDTOOut);
        }
        return spendingAnalysisDTOOuts;
    }

    public void createOrUpdateSpendingAnalysis(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("user not found");
        }

        Set<Subscription> subscriptions = user.getSubscriptions();
        if(subscriptions == null || subscriptions.isEmpty()){
            throw new  ApiException("User does not have subscriptions yet");
        }

        Set<Subscription> activeSubscriptions = new HashSet<>();
        for(Subscription subscription : subscriptions){
            if(subscription.getStatus().equals("Active"))
                activeSubscriptions.add(subscription);
        }

        if(activeSubscriptions.isEmpty()){
            throw new ApiException("you dont have Active subscriptions to analyse it");
        }

        SpendingAnalysis spendingAnalysis = user.getSpendingAnalysis();
        if(spendingAnalysis == null){
            throw new ApiException("spendingAnalysis not found");
        }

        // Reset values before recalculating (important for existing records)
        spendingAnalysis.setDigitalSubscriptionsTotalPrice(0.0);
        spendingAnalysis.setServiceSubscriptionsTotalPrice(0.0);
        spendingAnalysis.setTotalSpendingPrice(0.0);
        spendingAnalysis.setDigitalSubscriptionsCount(0);
        spendingAnalysis.setServiceSubscriptionsCount(0);

        for(Subscription subscription : activeSubscriptions){
            if(subscription.getCategory().equals("Digital")){
                spendingAnalysis.setDigitalSubscriptionsTotalPrice(spendingAnalysis.getDigitalSubscriptionsTotalPrice() + subscription.getPrice());
                spendingAnalysis.setDigitalSubscriptionsCount(spendingAnalysis.getDigitalSubscriptionsCount() + 1);
            }else if(subscription.getCategory().equals("Service")){
                spendingAnalysis.setServiceSubscriptionsTotalPrice(spendingAnalysis.getServiceSubscriptionsTotalPrice() + subscription.getPrice());
                spendingAnalysis.setServiceSubscriptionsCount(spendingAnalysis.getServiceSubscriptionsCount() + 1);
            }
            spendingAnalysis.setTotalSpendingPrice(spendingAnalysis.getTotalSpendingPrice() + subscription.getPrice());
        }

        spendingAnalysis.setAverageSubscriptionCost(spendingAnalysis.getTotalSpendingPrice() / activeSubscriptions.size());
        spendingAnalysis.setTotalSubscriptionsCount(activeSubscriptions.size());
        spendingAnalysis.setSpendingToIncomeRatio((spendingAnalysis.getTotalSpendingPrice() / user.getMonthlySalary()) * 100);

        //AI analysis:
        if(spendingAnalysis.getAiAnalysis() == null){
            AiAnalysis aiAnalysis = new AiAnalysis();
            spendingAnalysis.setAiAnalysis(aiAnalysis);
            aiAnalysis.setSpendingAnalysis(spendingAnalysis);
        }
        aiAnalysisService.addOrUpdateRecommendation(userId , spendingAnalysis.getId());
        spendingAnalysis.setUser(user);
        spendingAnalysisRepository.save(spendingAnalysis);
    }

    /**
     * Check if spending ratio is healthy (< 30% is considered healthy)
     */
    public Map<String, Object> getSpendingRatioStatus(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new ApiException("User not found");
        }

        SpendingAnalysis sa = user.getSpendingAnalysis();
        if(sa == null) {
            throw new ApiException("User spending analysis not found (no subscriptions yet)");
        }

        Map<String, Object> ratioStatus = new HashMap<>();
        double ratio = sa.getSpendingToIncomeRatio();

        ratioStatus.put("spendingToIncomeRatio", ratio);
        ratioStatus.put("monthlySalary", user.getMonthlySalary());
        ratioStatus.put("totalSpending", sa.getTotalSpendingPrice());

        // Determine status based on common financial advice
        String status;
        String message;
        if(ratio < 20) {
            status = "Excellent";
            message = "Your subscription spending is very manageable relative to your income.";
        } else if(ratio < 30) {
            status = "Good";
            message = "Your subscription spending is within a healthy range.";
        } else if(ratio < 40) {
            status = "Caution";
            message = "Consider reviewing your subscriptions to optimize spending.";
        } else {
            status = "High Risk";
            message = "Your subscription spending is quite high relative to your income. Consider canceling some subscriptions.";
        }

        ratioStatus.put("status", status);
        ratioStatus.put("message", message);
        ratioStatus.put("isHealthy", ratio < 30);

        return ratioStatus;
    }

    /**
     * Get average costs breakdown for a user
     */
    public Map<String, Object> getAveragesBreakdown(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new ApiException("User not found");
        }

        SpendingAnalysis sa = user.getSpendingAnalysis();
        if(sa == null) {
            throw new ApiException("User spending analysis not found (no subscriptions yet)");
        }

        Map<String, Object> averages = new HashMap<>();

        // Overall averages
        averages.put("averageSubscriptionCost", sa.getAverageSubscriptionCost());

        // Category-specific averages
        double avgDigitalCost = sa.getDigitalSubscriptionsCount() > 0 ?
                sa.getDigitalSubscriptionsTotalPrice() / sa.getDigitalSubscriptionsCount() : 0;
        double avgServiceCost = sa.getServiceSubscriptionsCount() > 0 ?
                sa.getServiceSubscriptionsTotalPrice() / sa.getServiceSubscriptionsCount() : 0;

        averages.put("averageDigitalSubscriptionCost", Math.round(avgDigitalCost * 100.0) / 100.0);
        averages.put("averageServiceSubscriptionCost", Math.round(avgServiceCost * 100.0) / 100.0);

        // Additional insights
        averages.put("totalSubscriptionsCount", sa.getTotalSubscriptionsCount());
        averages.put("digitalSubscriptionsCount", sa.getDigitalSubscriptionsCount());
        averages.put("serviceSubscriptionsCount", sa.getServiceSubscriptionsCount());

        // Calculate daily and weekly averages
        double dailySpending = sa.getTotalSpendingPrice() / 30; // Approximate monthly to daily
        double weeklySpending = sa.getTotalSpendingPrice() / 4.3; // Approximate monthly to weekly

        averages.put("averageDailySpending", Math.round(dailySpending * 100.0) / 100.0);
        averages.put("averageWeeklySpending", Math.round(weeklySpending * 100.0) / 100.0);

        return averages;
    }

    /**
     * Get spending comparison with different salary scenarios
     */
    public Map<String, Object> getSpendingProjections(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new ApiException("User not found");
        }

        SpendingAnalysis sa = user.getSpendingAnalysis();
        if(sa == null) {
            throw new ApiException("User spending analysis not found (no subscriptions yet)");
        }

        Map<String, Object> projections = new HashMap<>();
        double currentSpending = sa.getTotalSpendingPrice();
        double currentSalary = user.getMonthlySalary();

        // Current situation
        projections.put("currentSpending", currentSpending);
        projections.put("currentSalary", currentSalary);
        projections.put("currentRatio", sa.getSpendingToIncomeRatio());

        // Projections for different salary increases
        double salary10Percent = currentSalary * 1.1;
        double salary25Percent = currentSalary * 1.25;
        double salary50Percent = currentSalary * 1.5;

        projections.put("salaryIncrease10Percent", salary10Percent);
        projections.put("ratioWith10PercentIncrease", Math.round((currentSpending / salary10Percent) * 100 * 100.0) / 100.0);

        projections.put("salaryIncrease25Percent", salary25Percent);
        projections.put("ratioWith25PercentIncrease", Math.round((currentSpending / salary25Percent) * 100 * 100.0) / 100.0);

        projections.put("salaryIncrease50Percent", salary50Percent);
        projections.put("ratioWith50PercentIncrease", Math.round((currentSpending / salary50Percent) * 100 * 100.0) / 100.0);

        // What if spending doubled
        double doubledSpending = currentSpending * 2;
        projections.put("doubledSpending", doubledSpending);
        projections.put("ratioWithDoubledSpending", Math.round((doubledSpending / currentSalary) * 100 * 100.0) / 100.0);

        return projections;
    }

    /**
     * Get spending insights and recommendations based on patterns
     */
    public Map<String, Object> getSpendingInsights(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new ApiException("User not found");
        }

        SpendingAnalysis sa = user.getSpendingAnalysis();
        if(sa == null) {
            throw new ApiException("User spending analysis not found (no subscriptions yet)");
        }

        Map<String, Object> insights = new HashMap<>();

        // Basic data
        insights.put("totalSpending", sa.getTotalSpendingPrice());
        insights.put("totalSubscriptions", sa.getTotalSubscriptionsCount());
        insights.put("averageCost", sa.getAverageSubscriptionCost());

        // Category dominance
        boolean digitalDominant = sa.getDigitalSubscriptionsTotalPrice() > sa.getServiceSubscriptionsTotalPrice();
        insights.put("dominantCategory", digitalDominant ? "Digital" : "Service");
        insights.put("dominantCategorySpending", digitalDominant ?
                sa.getDigitalSubscriptionsTotalPrice() : sa.getServiceSubscriptionsTotalPrice());

        // Insights and recommendations
        List<String> recommendations = new ArrayList<>();

        if(sa.getSpendingToIncomeRatio() > 30) {
            recommendations.add("Consider reducing subscriptions - your spending ratio is above 30%");
        }

        if(sa.getAverageSubscriptionCost() > 100) {
            recommendations.add("Your average subscription cost is high - look for cheaper alternatives");
        }

        if(sa.getDigitalSubscriptionsCount() > 5) {
            recommendations.add("You have many digital subscriptions - check for overlapping services");
        }

        if(sa.getServiceSubscriptionsCount() > 3) {
            recommendations.add("Review your service subscriptions for necessity");
        }

        if(recommendations.isEmpty()) {
            recommendations.add("Your subscription spending looks well-balanced!");
        }

        insights.put("recommendations", recommendations);
        insights.put("spendingLevel", sa.getSpendingToIncomeRatio() < 15 ? "Conservative" :
                sa.getSpendingToIncomeRatio() < 30 ? "Moderate" : "High");

        return insights;
    }

    /**
     * Get subscription efficiency metrics
     */
    public Map<String, Object> getEfficiencyMetrics(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new ApiException("User not found");
        }

        SpendingAnalysis sa = user.getSpendingAnalysis();
        if(sa == null) {
            throw new ApiException("User spending analysis not found (no subscriptions yet)");
        }

        Map<String, Object> metrics = new HashMap<>();

        // Cost per subscription ratios
        metrics.put("averageCostPerSubscription", sa.getAverageSubscriptionCost());
        metrics.put("costPerIncomePercent", sa.getSpendingToIncomeRatio());

        // Efficiency scores (0-100 scale)
        double diversificationScore = 0;
        if(sa.getTotalSubscriptionsCount() > 0) {
            // More balanced between digital and service = higher score
            double digitalRatio = (double) sa.getDigitalSubscriptionsCount() / sa.getTotalSubscriptionsCount();
            diversificationScore = Math.max(0, 100 - Math.abs(50 - (digitalRatio * 100)) * 2);
        }

        double budgetEfficiencyScore = Math.max(0, 100 - sa.getSpendingToIncomeRatio() * 2);
        double costEfficiencyScore = sa.getAverageSubscriptionCost() < 50 ? 100 :
                sa.getAverageSubscriptionCost() < 100 ? 75 :
                        sa.getAverageSubscriptionCost() < 200 ? 50 : 25;

        metrics.put("diversificationScore", Math.round(diversificationScore * 100.0) / 100.0);
        metrics.put("budgetEfficiencyScore", Math.round(budgetEfficiencyScore * 100.0) / 100.0);
        metrics.put("costEfficiencyScore", costEfficiencyScore);

        double overallEfficiencyScore = (diversificationScore + budgetEfficiencyScore + costEfficiencyScore) / 3;
        metrics.put("overallEfficiencyScore", Math.round(overallEfficiencyScore * 100.0) / 100.0);

        // Efficiency rating
        String rating = overallEfficiencyScore >= 80 ? "Excellent" :
                overallEfficiencyScore >= 60 ? "Good" :
                        overallEfficiencyScore >= 40 ? "Fair" : "Poor";
        metrics.put("efficiencyRating", rating);

        return metrics;
    }

    /**
     * Get spending budget analysis and suggestions
     */
    public Map<String, Object> getBudgetAnalysis(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new ApiException("User not found");
        }

        SpendingAnalysis sa = user.getSpendingAnalysis();
        if(sa == null) {
            throw new ApiException("User spending analysis not found (no subscriptions yet)");
        }

        Map<String, Object> budget = new HashMap<>();

        double currentSpending = sa.getTotalSpendingPrice();
        double salary = user.getMonthlySalary();

        // Budget thresholds
        double recommendedBudget = salary * 0.20; // 20% of salary
        double maxBudget = salary * 0.30; // 30% max
        double conservativeBudget = salary * 0.15; // 15% conservative

        budget.put("currentSpending", currentSpending);
        budget.put("monthlySalary", salary);
        budget.put("recommendedBudget", Math.round(recommendedBudget * 100.0) / 100.0);
        budget.put("maxBudget", Math.round(maxBudget * 100.0) / 100.0);
        budget.put("conservativeBudget", Math.round(conservativeBudget * 100.0) / 100.0);

        // Available budget space
        double remainingBudget = recommendedBudget - currentSpending;
        budget.put("remainingRecommendedBudget", Math.round(remainingBudget * 100.0) / 100.0);

        double remainingMaxBudget = maxBudget - currentSpending;
        budget.put("remainingMaxBudget", Math.round(remainingMaxBudget * 100.0) / 100.0);

        // Budget status
        String budgetStatus;
        if(currentSpending <= conservativeBudget) {
            budgetStatus = "Conservative - You have room for more subscriptions";
        } else if(currentSpending <= recommendedBudget) {
            budgetStatus = "Optimal - Your spending is in the recommended range";
        } else if(currentSpending <= maxBudget) {
            budgetStatus = "High - Consider optimizing your subscriptions";
        } else {
            budgetStatus = "Over Budget - Immediate action needed to reduce spending";
        }

        budget.put("budgetStatus", budgetStatus);
        budget.put("isOverBudget", currentSpending > maxBudget);
        budget.put("isOverRecommended", currentSpending > recommendedBudget);

        // Savings potential
        double potentialSavings = Math.max(0, currentSpending - recommendedBudget);
        budget.put("potentialSavings", Math.round(potentialSavings * 100.0) / 100.0);
        budget.put("yearlyPotentialSavings", Math.round(potentialSavings * 12 * 100.0) / 100.0);

        return budget;
    }
}