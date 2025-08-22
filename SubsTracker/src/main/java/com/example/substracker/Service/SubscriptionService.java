package com.example.substracker.Service;
import com.example.substracker.API.ApiException;
import com.example.substracker.DTO.SubscriptionDTOOut;
import com.example.substracker.Model.SpendingAnalysis;
import com.example.substracker.Model.Subscription;
import com.example.substracker.Model.User;
import com.example.substracker.Repository.SpendingAnalysisRepository;
import com.example.substracker.Repository.SubscriptionRepository;
import com.example.substracker.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SpendingAnalysisService spendingAnalysisService;
    private final SpendingAnalysisRepository spendingAnalysisRepository;

    //all subscriptions for all users
    public List<Subscription> getAllSubscription(){
        return subscriptionRepository.findAll();
    }

    //get DTO out for all subscriptions
    public List<SubscriptionDTOOut> getAllSubscriptionDTOOut(){
        ArrayList<SubscriptionDTOOut> subscriptionDTOOuts = new ArrayList<>();
        for(Subscription subscription : getAllSubscription()) {
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
        return subscriptionDTOOuts;
    }

    //all subscriptions for specific user
    public Set<Subscription> getAllSubscriptionByUserId(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }
        return user.getSubscriptions();
    }

    //get all subscriptions for specific user as DTO
    public List<SubscriptionDTOOut> getAllSubscriptionDTOOutByUserId(Integer userId){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }

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
        return subscriptionDTOOuts;
    }

    // Utility method to convert subscription to DTO
    private SubscriptionDTOOut convertToDTO(Subscription subscription) {
        return new SubscriptionDTOOut(
                subscription.getSubscriptionName(),
                subscription.getCategory(),
                subscription.getPrice(),
                subscription.getBillingPeriod(),
                subscription.getNextBillingDate(),
                subscription.getStatus(),
                subscription.getUrl(),
                subscription.getDescription()
        );
    }

    // Utility method to convert list of subscriptions to DTOs
    private List<SubscriptionDTOOut> convertListToDTO(List<Subscription> subscriptions) {
        List<SubscriptionDTOOut> dtoList = new ArrayList<>();
        for(Subscription subscription : subscriptions) {
            dtoList.add(convertToDTO(subscription));
        }
        return dtoList;
    }

    //create new Subscription
    public void addSubscription(Integer userId, Subscription subscription){

        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }

        if(user.getSubscriptions() == null){
            user.setSubscriptions(new HashSet<>());//create subscription List to the user
        }
        subscription.setUser(user);
        user.getSubscriptions().add(subscription);
        if(Objects.equals(subscription.getBillingPeriod(), "monthly")){
            subscription.setNextBillingDate(java.time.LocalDate.now().plusMonths(1));
        } else if(Objects.equals(subscription.getBillingPeriod(), "3month")){
            subscription.setNextBillingDate(java.time.LocalDate.now().plusMonths(3));
        } else if(Objects.equals(subscription.getBillingPeriod(), "6month")){
            subscription.setNextBillingDate(java.time.LocalDate.now().plusMonths(6));
        } else if(Objects.equals(subscription.getBillingPeriod(), "yearly")){
            subscription.setNextBillingDate(java.time.LocalDate.now().plusYears(1));
        }
        subscription.setStatus("Active");
        subscriptionRepository.save(subscription);
        //creating Spending Analysis:
        //First time >> Spending analysis creation
        if(user.getSpendingAnalysis() == null){
            SpendingAnalysis spendingAnalysis = new SpendingAnalysis();
            spendingAnalysis.setUser(user);
            user.setSpendingAnalysis(spendingAnalysis);
            spendingAnalysisRepository.save(spendingAnalysis);
        }
        spendingAnalysisService.createOrUpdateSpendingAnalysis(userId);
    }

    public void updateSubscription(Integer userId,Integer SubscriptionId,Subscription subscription){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }
        Set<Subscription> subscriptions = user.getSubscriptions();

        //Chack if user have this subscription ID between his subscriptions
        Subscription oldSubscription = null;
        for (Subscription sub : subscriptions){
            if(sub.getId().equals(SubscriptionId)){
                oldSubscription = sub;
            }
        }

        if(oldSubscription == null){
            throw new ApiException("Subscription not found");
        }

        oldSubscription.setSubscriptionName(subscription.getSubscriptionName());
        oldSubscription.setCategory(subscription.getCategory());
        oldSubscription.setPrice(subscription.getPrice());
        oldSubscription.setDescription(subscription.getDescription());
        oldSubscription.setUpdatedAt(subscription.getUpdatedAt());
        oldSubscription.setCreatedAt(subscription.getCreatedAt());
        oldSubscription.setBillingPeriod(subscription.getBillingPeriod());
        oldSubscription.setNextBillingDate(subscription.getNextBillingDate());
        oldSubscription.setStatus(subscription.getStatus());

        subscriptionRepository.save(oldSubscription);
        spendingAnalysisService.createOrUpdateSpendingAnalysis(userId);
    }

    public void deleteSubscription (Integer userId,Integer subscriptionDeletedId){
        User user = userRepository.findUserById(userId);
        if(user == null){
            throw new ApiException("User not found");
        }
        Set<Subscription> subscriptions = user.getSubscriptions();
        Subscription deletedSubscription = null;
        for (Subscription sub : subscriptions){
            if(sub.getId().equals(subscriptionDeletedId)){
                deletedSubscription = sub;
            }
        }
        if(deletedSubscription == null){
            throw new ApiException("Subscription not found");
        }
        user.getSubscriptions().remove(deletedSubscription);
        subscriptionRepository.delete(deletedSubscription);
        spendingAnalysisService.createOrUpdateSpendingAnalysis(userId);
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkStatusSubscriptionExpired() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        for (Subscription subscription : subscriptions) {
            if (subscription.getNextBillingDate() == null) {
                continue; // Skip if next billing date is not set
            }
            if (!subscription.getNextBillingDate().isAfter(java.time.LocalDate.now())) {
                subscription.setStatus("Expired");
                subscriptionRepository.save(subscription);
            }
        }
    }

    public void renewSubscription(Integer userId, Integer subscriptionId,String billingPeriod) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        Set<Subscription> subscriptions = user.getSubscriptions();
        Subscription subscriptionToRenew = null;
        for (Subscription sub : subscriptions) {
            if (sub.getId().equals(subscriptionId) && sub.getStatus().equals("Expired")) {
                subscriptionToRenew = sub;
            }
        }
        if (subscriptionToRenew == null) {
            throw new ApiException("Subscription not found");
        }

        subscriptionToRenew.setBillingPeriod(billingPeriod);
        if (Objects.equals(billingPeriod, "monthly")) {
            subscriptionToRenew.setNextBillingDate(java.time.LocalDate.now().plusMonths(1));
        } else if (Objects.equals(billingPeriod, "3month")) {
            subscriptionToRenew.setNextBillingDate(java.time.LocalDate.now().plusMonths(3));
        } else if (Objects.equals(billingPeriod, "6month")) {
            subscriptionToRenew.setNextBillingDate(java.time.LocalDate.now().plusMonths(6));
        } else if (Objects.equals(billingPeriod, "yearly")) {
            subscriptionToRenew.setNextBillingDate(java.time.LocalDate.now().plusYears(1));
        }
        subscriptionToRenew.setStatus("Active");
        subscriptionRepository.save(subscriptionToRenew);
    }

    //Mshari - Entity methods
    public List<Subscription> getUpcomingForUser(Integer userId){
        List<Subscription> subscriptions = subscriptionRepository.findByUser_IdAndStatusAndNextBillingDateGreaterThanEqualOrderByNextBillingDateAsc
                (userId,"Active",LocalDate.now());
        return subscriptions;
    }

    public List<Subscription> getDueWithinDays(Integer userId,int days){
        if (days < 1 ){
            throw new ApiException("days must be more 1day");
        }
        LocalDate today = LocalDate.now();
        LocalDate to = today.plusDays(days);
        return subscriptionRepository.findByUser_IdAndStatusAndNextBillingDateBetweenOrderByNextBillingDateAsc
                (userId,"Active",today,to);
    }

    public List<Subscription> getActiveSubscriptions(Integer userId){
        return subscriptionRepository.findSubscriptionByUserIdAndStatus(userId,"Active");
    }

    public List<Subscription> getExpiredByUser(Integer userId){
        return subscriptionRepository.findSubscriptionByUserIdAndStatus(userId,"Expired");
    }

    //Mshari - DTO methods
    public List<SubscriptionDTOOut> getUpcomingForUserDTOOut(Integer userId){
        List<Subscription> subscriptions = getUpcomingForUser(userId);
        return convertListToDTO(subscriptions);
    }

    public List<SubscriptionDTOOut> getDueWithinDaysDTOOut(Integer userId, int days){
        List<Subscription> subscriptions = getDueWithinDays(userId, days);
        return convertListToDTO(subscriptions);
    }

    public List<SubscriptionDTOOut> getActiveSubscriptionsDTOOut(Integer userId){
        List<Subscription> subscriptions = getActiveSubscriptions(userId);
        return convertListToDTO(subscriptions);
    }

    public List<SubscriptionDTOOut> getExpiredByUserDTOOut(Integer userId){
        List<Subscription> subscriptions = getExpiredByUser(userId);
        return convertListToDTO(subscriptions);
    }

    public List<SubscriptionDTOOut> getSubscriptionsForUserByCategory(Integer userId, String category){
        User user = userRepository.findUserById(userId);
        List<Subscription> subscriptionList = new ArrayList<>();
        if(user != null){
            Set<Subscription> subscriptions = user.getSubscriptions();
            if(subscriptions != null){
                for(Subscription subscription : subscriptions){
                    if(subscription.getCategory().equals(category)){
                        subscriptionList.add(subscription);
                    }
                }
            }else{
                throw new ApiException("User didn't have any subscriptions");
            }
        }else{
            throw new ApiException("User not found");
        }
        return convertListToDTO(subscriptionList);
    }

    public List<SubscriptionDTOOut> getSubscriptionsForUserByBillingPeriod(Integer userId, String billingPeriod){
        User user = userRepository.findUserById(userId);
        List<Subscription> subscriptionList = new ArrayList<>();
        if(user != null){
            Set<Subscription> subscriptions = user.getSubscriptions();
            if(subscriptions != null){
                for(Subscription subscription : subscriptions){
                    if(billingPeriod.equals(subscription.getBillingPeriod())){
                        subscriptionList.add(subscription);
                    }
                }
            }else{
                throw new ApiException("User didn't have any subscriptions");
            }
        }else{
            throw new ApiException("User not found");
        }
        return convertListToDTO(subscriptionList);
    }

    public List<SubscriptionDTOOut> getSubscriptionsForUserBetweenMinMaxPriceOrEqual(Integer userId, Double minPrice, Double maxPrice){
        User user = userRepository.findUserById(userId);
        List<Subscription> subscriptionList = new ArrayList<>();
        if(user != null){
            Set<Subscription> subscriptions = user.getSubscriptions();
            if(subscriptions != null){
                for(Subscription subscription : subscriptions){
                    if(subscription.getPrice()>=minPrice && subscription.getPrice()<=maxPrice){
                        subscriptionList.add(subscription);
                    }
                }
            }else{
                throw new ApiException("User didn't have any subscriptions");
            }
        }else{
            throw new ApiException("User not found");
        }
        return convertListToDTO(subscriptionList);
    }

    public SubscriptionDTOOut getMostExpensiveSubscription(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null || user.getSubscriptions() == null || user.getSubscriptions().isEmpty()) {
            throw new ApiException("User doesn't have any subscriptions");
        }

        Subscription mostExpensive = user.getSubscriptions().stream()
                .max(Comparator.comparingDouble(Subscription::getPrice))
                .orElseThrow(() -> new ApiException("User doesn't have any subscriptions"));

        return convertToDTO(mostExpensive);
    }

    public SubscriptionDTOOut getMostCheapestSubscription(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null || user.getSubscriptions() == null || user.getSubscriptions().isEmpty()) {
            throw new ApiException("User doesn't have any subscriptions");
        }

        Subscription cheapest = user.getSubscriptions().stream()
                .min(Comparator.comparingDouble(Subscription::getPrice))
                .orElseThrow(() -> new ApiException("User doesn't have any subscriptions"));

        return convertToDTO(cheapest);
    }



}