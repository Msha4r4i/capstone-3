package com.example.substracker.Controller;

import com.example.substracker.API.ApiResponse;
import com.example.substracker.Model.Subscription;
import com.example.substracker.Service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // Get all subscriptions
    @GetMapping("/get")
    public ResponseEntity<?> getAllSubscriptions() {
        return ResponseEntity.status(200).body(subscriptionService.getAllSubscription());
    }
    // Get all subscriptions with DTO out
    @GetMapping("/get/dto")
    public ResponseEntity<?> getAllSubscriptionsDTOOut() {
        return ResponseEntity.status(200).body(subscriptionService.getAllSubscriptionDTOOut());
    }

    @GetMapping("/get/{userId}/dto")
    public ResponseEntity<?> getSubscriptionDtoByUserId(@PathVariable Integer userId){
        return ResponseEntity.status(200).body(subscriptionService.getAllSubscriptionDTOOutByUserId(userId));
    }

    // Get all subscriptions for a specific user
    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getSubscriptionsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(subscriptionService.getAllSubscriptionByUserId(userId));
    }

    // Add subscription for a user
    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addSubscription(@PathVariable Integer userId, @Valid @RequestBody Subscription subscription) {
        subscriptionService.addSubscription(userId, subscription);
        return ResponseEntity.status(200).body(new ApiResponse("Subscription added successfully"));
    }

    // Update subscription
    @PutMapping("/update/{userId}/{subscriptionId}")
    public ResponseEntity<?> updateSubscription(@PathVariable Integer userId,
                                                @PathVariable Integer subscriptionId,
                                                @Valid @RequestBody Subscription subscription) {
        subscriptionService.updateSubscription(userId, subscriptionId, subscription);
        return ResponseEntity.status(200).body(new ApiResponse("Subscription updated successfully"));
    }

    // Delete subscription
    @DeleteMapping("/delete/{userId}/{subscriptionDeletedId}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Integer userId,@PathVariable Integer subscriptionDeletedId) {
        subscriptionService.deleteSubscription(userId , subscriptionDeletedId);
        return ResponseEntity.status(200).body(new ApiResponse("Subscription deleted successfully"));
    }

    // Renew subscription
    //made by Mohammed
    @PutMapping("/renew/{userId}/{subscriptionId}/{billingPeriod}")
    public ResponseEntity<?> renewSubscription(@PathVariable Integer userId, @PathVariable Integer subscriptionId, @PathVariable String billingPeriod) {
        subscriptionService.renewSubscription(userId, subscriptionId, billingPeriod);
        return ResponseEntity.status(200).body(new ApiResponse("Subscription renewed successfully"));
    }
    //Mshari

    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<?> getUpcomingByUser(@PathVariable Integer userId){
        return ResponseEntity.status(200).body(subscriptionService.getUpcomingForUserDTOOut(userId));
    }
    //Mshari
    @GetMapping("/user/{userId}/day/{days}")
    public ResponseEntity<?> getDueWithinDays(@PathVariable Integer userId,@PathVariable int days){
        return ResponseEntity.status(200).body(subscriptionService.getDueWithinDaysDTOOut(userId, days));
    }
    //Mshari
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveSubscriptionsFromDate(@PathVariable Integer userId){
        return ResponseEntity.status(200).body(subscriptionService.getActiveSubscriptionsDTOOut(userId));
    }
    //Mshari
    @GetMapping("/user/{userId}/expired")
    public ResponseEntity<?> getExpiredByUser(@PathVariable Integer userId){
        return ResponseEntity.status(200).body(subscriptionService.getExpiredByUserDTOOut(userId));
    }

    //made by Mohammed
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<?> getSubscriptionsForUserByCategory(@PathVariable Integer userId, @PathVariable String category){
        return ResponseEntity.status(200).body(subscriptionService.getSubscriptionsForUserByCategory(userId, category));
    }


    //made by Mohammed
    @GetMapping("/user/{userId}/billing/{billingPeriod}")
    public ResponseEntity<?> getSubscriptionForUserByNextBillingDate(@PathVariable Integer userId, @PathVariable String billingPeriod){
        return ResponseEntity.status(200).body(subscriptionService.getSubscriptionsForUserByBillingPeriod(userId, billingPeriod));
    }

    //made by Mohammed
    @GetMapping("/user/{userId}/price-range/{minPrice}/{maxPrice}")
    public ResponseEntity<?> getSubscriptionsForUserBetweenMinMaxPrice(@PathVariable Integer userId, @PathVariable Double minPrice, @PathVariable Double maxPrice){
        return ResponseEntity.status(200).body(subscriptionService.getSubscriptionsForUserBetweenMinMaxPriceOrEqual(userId, minPrice, maxPrice));
    }

    //made by Mohammed
    @GetMapping("/user/{userId}/most-expensive")
    public ResponseEntity<?> getMostExpensiveSubscription(@PathVariable Integer userId) {
        return ResponseEntity
                .status(200)
                .body(subscriptionService.getMostExpensiveSubscription(userId));
    }

    //made by Mohammed
    @GetMapping("/user/{userId}/cheapest")
    public ResponseEntity<?> getMostCheapestSubscription(@PathVariable Integer userId) {
        return ResponseEntity
                .status(200)
                .body(subscriptionService.getMostCheapestSubscription(userId));
    }

}
