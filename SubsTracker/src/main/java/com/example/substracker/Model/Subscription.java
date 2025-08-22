package com.example.substracker.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscriptions")
// One Check For All Patterns in the class.
@Check(constraints = "category IN ('Digital', 'Service') AND status IN ('Active', 'Expired') AND billing_period IN ('monthly', '3month', '6month', 'yearly')")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Subscription name cannot be empty")
    @Size(max = 100, message = "Subscription name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String subscriptionName;

    @NotEmpty(message = "Category cannot be empty")
    @Pattern(regexp = "Digital|Service", message = "Category must be either 'digital' or 'service'")
    @Column(nullable = false, length = 20)
    private String category;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or greater")
    @Column(nullable = false)
    private Double price;

    @NotEmpty(message = "Billing period cannot be empty")
    @Pattern(regexp = "monthly|3month|6month|yearly",
            message = "Billing period must be one of: monthly, 3month, 6month, yearly")
    @Column(nullable = false, length = 20)
    private String billingPeriod;

    @FutureOrPresent(message = "Next billing date cannot be in the past")
    @Column(nullable = false)
    private LocalDate nextBillingDate;

    @Pattern(regexp = "Active|Expired", message = "Status must be either Active or Expired")
    @Column(nullable = false, length = 20)
    private String status;

    @Size(max = 255, message = "URL must not exceed 255 characters")
    @Column( columnDefinition = "TEXT")
    private String url;

    @NotEmpty(message = "description cannot be empty")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(nullable = false ,columnDefinition = "TEXT")
    private String description;



    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //Done Relation: Many to one with User
    @ManyToOne
    @JsonIgnore
    private User user;

    //Done Relation: One To Many with Expiration Alert
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "subscription")
    private Set<ExpirationAlert> expirationAlerts;

}
