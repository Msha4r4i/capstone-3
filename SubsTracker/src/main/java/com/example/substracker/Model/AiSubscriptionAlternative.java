//AiSubscriptionAlternative
package com.example.substracker.Model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints =
                "alternative_price >= 0 " +
                "AND potential_monthly_savings >= 0 " +
                "AND alternative_billing_period IN ('monthly','3month','6month','yearly')"
)
public class AiSubscriptionAlternative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "service name cannot be empty")
    @Size(max = 100, message = "service name must not exceed 100 characters")
    @Column(columnDefinition = "varchar(100) not null")
    private String alternativeServiceName;

    @NotNull(message = "price is required")
    @PositiveOrZero(message = "price must be >= 0")
    @Column(columnDefinition = "double not null")
    private Double alternativePrice;

    @NotEmpty(message = "billing period cannot be empty")
    @Pattern(regexp = "^(monthly|3month|6month|yearly)$",
            message = "billing period must be one of: monthly, 3month, 6month, yearly")
    @Column(columnDefinition = "varchar(20) not null")
    private String alternativeBillingPeriod;

    @NotEmpty(message = "recommendation reason cannot be empty")
    @Column(columnDefinition = "TEXT not null")
    private String recommendationReason;

    @NotNull(message = "potential monthly savings is required")
    @PositiveOrZero(message = "potential monthly savings must be >= 0")
    @Column(columnDefinition = "double not null")
    private Double potentialMonthlySavings;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "datetime not null")
    private LocalDateTime createdAt;

    //TODO No relations .
}

