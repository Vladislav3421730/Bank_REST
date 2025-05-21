package com.example.bankcards.dto;

import java.math.BigDecimal;

public record TransactionFilterDto(
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String operation,
        String operationResult) {
}
