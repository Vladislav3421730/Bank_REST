package com.example.bankcards.dto.response;

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
@Schema(description = "Response DTO for transfer transaction details")
public class TransferResponseDto {

    @Schema(description = "Card ID associated with the transfer", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID cardId;

    @Schema(description = "Card number associated with the transfer", example = "1234567890123456")
    private String number;

    @Schema(description = "Target card ID for the transfer", example = "b34f1b88-70be-413a-bf34-f3a1fa6f3a24")
    private UUID targetCardId;

    @Schema(description = "Target card number for the transfer", example = "9876543210987654")
    private String targetNumber;

    @Schema(description = "Amount transferred from the card", example = "50.00")
    private BigDecimal transferAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "Timestamp of the transfer transaction", example = "2025-04-14 14:30:00")
    private LocalDateTime transferTime;

    @Schema(description = "New balance after the transfer", example = "150.00")
    private BigDecimal balance;

    @Schema(description = "User ID associated with the card", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID userId;
}
