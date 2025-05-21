package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FilterCardDto(
        BigDecimal minBalance,
        BigDecimal maxBalance,
        LocalDate expiredBefore,
        LocalDate expiredAfter) {
}
