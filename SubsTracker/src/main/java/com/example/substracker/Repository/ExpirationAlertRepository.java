package com.example.substracker.Repository;

import com.example.substracker.Model.ExpirationAlert;
import com.example.substracker.Model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpirationAlertRepository extends JpaRepository<ExpirationAlert,Integer> {
    ExpirationAlert findExpirationAlertById(Integer id);


    boolean existsBySubscriptionAndAlertTypeAndIsSent(Subscription subscription, String alertType, boolean b);
}
