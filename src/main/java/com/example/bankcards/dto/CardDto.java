package com.example.bankcards.dto;

import com.example.bankcards.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "DTO representing a card with details such as number, balance, and expiration date.")
public class CardDto {

    @Schema(description = "Unique identifier for the card", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @Schema(description = "Card number", example = "**** **** **** 3456")
    private String number;

    @Schema(description = "Status of the card", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "BLOCKED"})
    private String status;

    @Schema(description = "Current balance on the card", example = "1000.50")
    private BigDecimal balance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
    @Schema(description = "Expiration date of the card", example = "2025-12-31")
    private LocalDate expirationDate;

    @Schema(description = "User ID associated with the card", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID userId;
}
