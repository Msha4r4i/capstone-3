package com.example.substracker.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class SubscriptionDTOOut {
    private String subscriptionName;
    private String category;
    private Double price;
    private String billingPeriod;
    private LocalDate nextBillingDate;
    private String status;
    private String url;
    private String description;
}
