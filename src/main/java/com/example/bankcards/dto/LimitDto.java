package com.example.bankcards.dto;

import com.example.bankcards.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing the limits of a card including daily and monthly limits.")
public class LimitDto {

    @Schema(description = "Unique identifier for the limit", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @Schema(description = "The daily limit set for the card", example = "500.00")
    private BigDecimal dailyLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "The date and time when the limit was created", example = "2025-01-01 12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "The monthly limit set for the card", example = "15000.00")
    private BigDecimal monthlyLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "The date and time when the limit was last updated", example = "2025-01-01 12:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Unique identifier for the associated card", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID cardId;

    @Schema(description = "Card number associated with the limit", example = "1234567890123456")
    private String number;
}
