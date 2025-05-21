package com.example.bankcards.dto.response;

import com.example.bankcards.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Response DTO for withdrawal transaction details")
public class WithdrawalResponseDto {

    @Schema(description = "Card ID associated with the withdrawal", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID cardId;

    @Schema(description = "Card number associated with the withdrawal", example = "1234567890123456")
    private String number;

    @Schema(description = "Amount withdrawn from the card", example = "50.00")
    private BigDecimal withdrawalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "Timestamp of the withdrawal transaction", example = "2025-04-14 14:30:00")
    private LocalDateTime transferTime;

    @Schema(description = "Remaining balance after the withdrawal", example = "150.00")
    private BigDecimal remainingBalance;

    @Schema(description = "User ID associated with the card", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID userId;
}
