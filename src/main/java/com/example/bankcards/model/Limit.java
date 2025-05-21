package com.example.bankcards.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "limits")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Limit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "daily_limit", nullable = false)
    private BigDecimal dailyLimit;

    @Column(name = "monthly_limit", nullable = false)
    private BigDecimal monthlyLimit;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Limit(Card card) {
        this.card = card;
    }

    @PrePersist
    void init() {
        dailyLimit = BigDecimal.valueOf(300.00);
        monthlyLimit = BigDecimal.valueOf(3400.00);
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

}
