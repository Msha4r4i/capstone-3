package com.example.substracker.Repository;

import com.example.substracker.Model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {
    Subscription findSubscriptionById(Integer id);

    List<Subscription> findByStatus(String status);

    List<Subscription> findSubscriptionsByUserId(Integer userId);

    List<Subscription> findByUser_IdAndStatusAndNextBillingDateGreaterThanEqualOrderByNextBillingDateAsc(
            Integer userId, String status, LocalDate fromDate
    );

    List<Subscription> findByUser_IdAndStatusAndNextBillingDateBetweenOrderByNextBillingDateAsc(
            Integer userId, String status, LocalDate from, LocalDate to
    );

    List<Subscription> findByUser_IdAndStatusAndNextBillingDateBetween(
            Integer userId, String status, LocalDate fromDate, LocalDate toDate
    );

    List<Subscription> findSubscriptionByUserIdAndStatusAndNextBillingDate(Integer userId, String status, LocalDate nextBillingDate);


    List<Subscription> findSubscriptionByUserIdAndStatus(Integer userId, String status);
}
