package com.example.substracker.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class SpendingAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Digital total is required")
    @PositiveOrZero(message = "Digital total must be >= 0")
    @Column(columnDefinition = "double not null")
    private Double digitalSubscriptionsTotalPrice = 0.0;

    @NotNull(message = "Service total is required")
    @PositiveOrZero(message = "Service total must be >= 0")
    @Column(columnDefinition = "double not null")
    private Double serviceSubscriptionsTotalPrice = 0.0;

    @NotNull(message = "Total spending is required")
    @PositiveOrZero(message = "Total spending must be >= 0")
    @Column(columnDefinition = "double not null")
    private Double totalSpendingPrice = 0.0;

    @NotNull(message = "Average subscription cost is required")
    @PositiveOrZero(message = "Average subscription cost must be >= 0")
    @Column(columnDefinition = "double not null")
    private Double averageSubscriptionCost = 0.0;

    @NotNull(message = "Spending to income ratio is required")
    @PositiveOrZero(message = "Spending to income ratio must be >= 0")
    @Column(columnDefinition = "double not null")
    private Double spendingToIncomeRatio = 0.0;

    @NotNull(message = "Total subscriptions count is required")
    @Min(value = 0, message = "Total subscriptions count must be >= 0")
    @Column(columnDefinition = "int not null")
    private Integer totalSubscriptionsCount = 0;

    @NotNull(message = "Digital subscriptions count is required")
    @Min(value = 0, message = "Digital subscriptions count must be >= 0")
    @Column(columnDefinition = "int not null")
    private Integer digitalSubscriptionsCount = 0;

    @NotNull(message = "Service subscriptions count is required")
    @Min(value = 0, message = "Service subscriptions count must be >= 0")
    @Column(columnDefinition = "int not null")
    private Integer serviceSubscriptionsCount = 0;

    @UpdateTimestamp
    @Column(columnDefinition = "timestamp not null")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(columnDefinition = "timestamp not null")
    private LocalDateTime createdAt;

    // Done: Relation: One-to-One with User
    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;
    // TODO: Relation: One-to-One with AiAnalysis
    @OneToOne(cascade = CascadeType.ALL,mappedBy = "spendingAnalysis")
    @PrimaryKeyJoinColumn
    private AiAnalysis aiAnalysis;

}