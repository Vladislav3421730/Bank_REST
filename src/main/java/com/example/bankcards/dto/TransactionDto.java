package com.example.bankcards.dto;

import com.example.bankcards.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "DTO representing a transaction performed on a card.")
public class TransactionDto {

    @Schema(description = "Unique identifier for the transaction", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @Schema(description = "The amount involved in the transaction", example = "250.00")
    private BigDecimal amount;

    @Schema(description = "The type of the operation (e.g., 'TRANSFER', 'WITHDRAWAL')", example = "TRANSFER")
    private String operationType;

    @Schema(description = "The result of the operation (e.g., 'SUCCESS', 'FAILURE')", example = "SUCCESSFULLY")
    private String operationResult;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "Timestamp when the transaction occurred", example = "2025-01-01 12:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Unique identifier of the card where the transaction was initiated", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID cardId;

    @Schema(description = "Unique identifier of the target card involved in the transaction", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID targetCardId;
}
