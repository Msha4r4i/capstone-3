package com.example.substracker.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be empty")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$\n") //
    private String password;

    @NotEmpty(message = "Name cannot be empty")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull(message = "Monthly salary is required")
    @Positive(message = "Monthly salary must be greater than 0")
    @Column(nullable = false)
    private Double monthlySalary;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotNull
    @NotNull(message = "Email notifications flag is required")
    @Column(nullable = false)
    private Boolean emailNotificationsEnabled;

    //Done Relation: One to Many With Subscription.
    @OneToMany(cascade = CascadeType.ALL , mappedBy = "user")
    private Set<Subscription> subscriptions;


    //Done Relation: One to One with Spending Analysis.
    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    @PrimaryKeyJoinColumn
    private SpendingAnalysis spendingAnalysis;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    @PrimaryKeyJoinColumn
    private PaymentRequest paymentRequest;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)//user can't edit this filed.
    private Boolean isSubscribed = false;


}