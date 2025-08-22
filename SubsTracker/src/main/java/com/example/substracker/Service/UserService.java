package com.example.substracker.Service;

import com.example.substracker.API.ApiException;
import com.example.substracker.DTO.AiAnalysisDTOOut;
import com.example.substracker.DTO.SpendingAnalysisDTOOut;
import com.example.substracker.DTO.SubscriptionDTOOut;
import com.example.substracker.DTO.UserDTOOut;
import com.example.substracker.Model.AiAnalysis;
import com.example.substracker.Model.SpendingAnalysis;
import com.example.substracker.Model.Subscription;
import com.example.substracker.Model.User;
import com.example.substracker.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<UserDTOOut> getAllUsersDTOOut(){
        ArrayList<UserDTOOut> userDTOOuts = new ArrayList<>();

        for(User user : getAllUsers()) {
            // Get user's subscriptions as DTOs
            List<SubscriptionDTOOut> subscriptionDTOOuts = new ArrayList<>();
            Set<Subscription> userSubscriptions = user.getSubscriptions();
            if(userSubscriptions != null) {
                for(Subscription subscription : userSubscriptions) {
                    SubscriptionDTOOut subscriptionDTOOut = new SubscriptionDTOOut(
                            subscription.getSubscriptionName(),
                            subscription.getCategory(),
                            subscription.getPrice(),
                            subscription.getBillingPeriod(),
                            subscription.getNextBillingDate(),
                            subscription.getStatus(),
                            subscription.getUrl(),
                            subscription.getDescription()
                    );
                    subscriptionDTOOuts.add(subscriptionDTOOut);
                }
            }

            // Get user's spending analysis as DTO
            SpendingAnalysisDTOOut spendingAnalysisDTOOut = null;
            SpendingAnalysis spendingAnalysis = user.getSpendingAnalysis();
            if(spendingAnalysis != null) {
                // Convert AiAnalysis to DTO
                AiAnalysisDTOOut aiAnalysisDTOOut = null;
                AiAnalysis aiAnalysis = spendingAnalysis.getAiAnalysis();
                if(aiAnalysis != null) {
                    aiAnalysisDTOOut = new AiAnalysisDTOOut(
                            aiAnalysis.getGeneralRecommendations()
                    );
                }

                spendingAnalysisDTOOut = new SpendingAnalysisDTOOut(
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

            UserDTOOut userDTOOut = new UserDTOOut(
                    user.getName(),
                    user.getEmail(),
                    user.getMonthlySalary(),
                    user.getEmailNotificationsEnabled(),
                    subscriptionDTOOuts,
                    spendingAnalysisDTOOut
            );
            userDTOOuts.add(userDTOOut);
        }
        return userDTOOuts;
    }

    // Method to get a specific user's DTO
    public UserDTOOut getUserDTOOut(Integer userId) {
        User user = userRepository.findUserById(userId);
        if(user == null) {
            throw new ApiException("User not found");
        }

        // Get user's subscriptions as DTOs
        List<SubscriptionDTOOut> subscriptionDTOOuts = new ArrayList<>();
        Set<Subscription> userSubscriptions = user.getSubscriptions();
        if(userSubscriptions != null) {
            for(Subscription subscription : userSubscriptions) {
                SubscriptionDTOOut subscriptionDTOOut = new SubscriptionDTOOut(
                        subscription.getSubscriptionName(),
                        subscription.getCategory(),
                        subscription.getPrice(),
                        subscription.getBillingPeriod(),
                        subscription.getNextBillingDate(),
                        subscription.getStatus(),
                        subscription.getUrl(),
                        subscription.getDescription()
                );
                subscriptionDTOOuts.add(subscriptionDTOOut);
            }
        }

        // Get user's spending analysis as DTO
        SpendingAnalysisDTOOut spendingAnalysisDTOOut = null;
        SpendingAnalysis spendingAnalysis = user.getSpendingAnalysis();
        if(spendingAnalysis != null) {
            // Convert AiAnalysis to DTO
            AiAnalysisDTOOut aiAnalysisDTOOut = null;
            AiAnalysis aiAnalysis = spendingAnalysis.getAiAnalysis();
            if(aiAnalysis != null) {
                aiAnalysisDTOOut = new AiAnalysisDTOOut(
                        aiAnalysis.getGeneralRecommendations()
                );
            }

            spendingAnalysisDTOOut = new SpendingAnalysisDTOOut(
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

        return new UserDTOOut(
                user.getName(),
                user.getEmail(),
                user.getMonthlySalary(),
                user.getEmailNotificationsEnabled(),
                subscriptionDTOOuts,
                spendingAnalysisDTOOut
        );
    }

    public void addUser(User user){
        userRepository.save(user);
    }

    public void deleteUser(Integer userId){
        User userDeleted = userRepository.findUserById(userId);
        if(userDeleted == null){
            throw new ApiException("user Not found");
        }
        userRepository.delete(userDeleted);
    }

    public void updateUser(Integer userId, User user){
        User oldUser = userRepository.findUserById(userId);
        if(oldUser == null){
            throw new ApiException("user not found");
        }
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setEmailNotificationsEnabled(user.getEmailNotificationsEnabled());
        oldUser.setMonthlySalary(user.getMonthlySalary());
        userRepository.save(oldUser);
    }

    //Mshari
    public void triggerNotifications(Integer userId){
        User user = userRepository.findUserById(userId);
        if (user == null){
            throw new ApiException("User not found");
        }
        boolean newState = !Boolean.TRUE.equals(user.getEmailNotificationsEnabled());
        user.setEmailNotificationsEnabled(newState);
        userRepository.save(user);

    }

    public List<User> getAllSubsUsers(){
        return userRepository.findUsersByIsSubscribed(Boolean.TRUE);
    }

    public List<User> getAllUnsubsUsers(){
        return userRepository.findUsersByIsSubscribed(Boolean.FALSE);
    }

    //Mshari
    public Boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}