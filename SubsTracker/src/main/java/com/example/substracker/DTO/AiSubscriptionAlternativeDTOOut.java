package com.example.substracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AiSubscriptionAlternativeDTOOut {
    private String alternativeServiceName;
    private Double alternativePrice;
    private String alternativeBillingPeriod;
    private String recommendationReason;
    private Double potentialMonthlySavings;
}