package com.example.substracker.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Check(constraints = "alert_type IN ('urgent', 'normal')")
public class ExpirationAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "alert date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "date not null")
    private LocalDateTime alertDate;

    @NotNull(message = "days before expiry cannot be null")
    @Positive(message = "days before expiry must be a positive number")
    @Column(columnDefinition = "int not null")
    private Integer daysBeforeExpiry;

    @NotEmpty(message = "alert type cannot be empty")

    @NotEmpty(message = "alert type cannot be empty")
    //urgent before 2 days and the normal before 7 days
    @Pattern(regexp = "^(urgent|normal)$", message = "alert type must be either 'urgent' or 'normal'")
    @Column(columnDefinition = "varchar(10) not null")
    private String alertType;

    @NotEmpty(message = "message cannot be empty")
    @Size(min = 5, max = 255, message = "message must be between 5 and 255 characters")
    @Column(columnDefinition = "varchar(255) not null")
    private String message;

    @NotNull(message = "is sent cannot be null")
    @Column(columnDefinition = "boolean not null default false")
    private Boolean isSent = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
    //Nothing
    //Done relation: Many to one Subscription.
    @ManyToOne
    @JsonIgnore
    private Subscription subscription;
}
