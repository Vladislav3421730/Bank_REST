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
@Schema(description = "Response DTO for recharge transaction details")
public class RechargeResponseDto {

    @Schema(description = "Card ID associated with the recharge", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID cardId;

    @Schema(description = "Card number associated with the recharge", example = "1234567890123456")
    private String number;

    @Schema(description = "Amount recharged to the card", example = "100.00")
    private BigDecimal rechargeAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "Timestamp of the recharge transaction", example = "2025-04-14 14:30:00")
    private LocalDateTime transferTime;

    @Schema(description = "New balance after the recharge", example = "200.00")
    private BigDecimal balance;

    @Schema(description = "User ID associated with the card", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID userId;
}
