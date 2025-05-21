package com.example.bankcards.model;

import com.example.bankcards.util.CardNumberCryptoConverter;
import com.example.bankcards.model.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "card")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "number", nullable = false)
    @NotBlank(message = "Card number must not be blank")
    @Convert(converter = CardNumberCryptoConverter.class)
    private String number;

    @Column(name = "balance", nullable = false)
    @NotNull(message = "Balance must not be null")
    private BigDecimal balance;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status must not be null")
    private CardStatus status;

    @Column(name = "expiration_date", nullable = false)
    @NotNull(message = "Expiration date must not be null")
    private LocalDate expirationDate;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    void init() {
        balance = BigDecimal.ZERO;
        status = CardStatus.ACTIVE;
        expirationDate = LocalDate.now().plusYears(4);
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "card")
    private List<Transaction> transactions = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "card")
    private Limit limit;
}

