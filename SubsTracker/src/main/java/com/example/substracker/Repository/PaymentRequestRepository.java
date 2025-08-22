package com.example.substracker.Repository;

import com.example.substracker.Model.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest,Integer>{
    PaymentRequest findPaymentRequestById(Integer id);
}