package com.example.substracker.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AiAnalysis {

    @Id
    private Integer id;

    @NotEmpty(message = "general recommendations cannot be empty")
    @Column(columnDefinition = "TEXT not null")
    private String generalRecommendations;//from AI

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "datetime not null")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "datetime not null")
    private LocalDateTime updatedAt;

    //Done relation:One to One spendingAnalysis and spendingAnalysis is the boss.
    @OneToOne
    @MapsId
    @JsonIgnore
    private SpendingAnalysis spendingAnalysis;
}
