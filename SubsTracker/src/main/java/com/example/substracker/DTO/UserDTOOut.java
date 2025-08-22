package com.example.substracker.DTO;

import com.example.substracker.Model.SpendingAnalysis;
import com.example.substracker.Model.Subscription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
public class UserDTOOut {

    private String name;
    private String email;
    private Double monthlySalary;
    private Boolean emailNotificationsEnabled;
    private List<SubscriptionDTOOut> subscriptionsDTOOuts;
    private SpendingAnalysisDTOOut spendingAnalysisDTOOut;
}
