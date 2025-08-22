package com.example.substracker.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String number;
    private String cvc;
    private String month;
    private String year;
    private double amount;
    private String currency;
    private String description;
    private String callbackUrl;

    //this attribute where user cant edit it .
    @JsonIgnore
    private String paymentUserId;
    @JsonIgnore
    private String redirectToCompletePayment;

    //relation where every user can have only one card.
    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;

}