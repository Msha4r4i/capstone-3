package com.example.substracker.DTO;

import com.example.substracker.Model.AiAnalysis;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SpendingAnalysisDTOOut {
    private Double digitalSubscriptionsTotalPrice;
    private Double serviceSubscriptionsTotalPrice;
    private Double totalSpendingPrice;
    private Double averageSubscriptionCost;
    private Double spendingToIncomeRatio;
    private Integer totalSubscriptionsCount;
    private Integer digitalSubscriptionsCount;
    private Integer serviceSubscriptionsCount;
    private AiAnalysisDTOOut aiAnalysis;

}
