package com.example.bankcards.dto;

import com.example.bankcards.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "DTO representing a block request for a card, containing status, timestamps, and related IDs.")
public class BlockRequestDto {

    @Schema(description = "Unique identifier of the block request", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "Date and time when the block request was created", example = "2025-05-21 15:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.TIMESTAMP_PATTERN)
    @Schema(description = "Date and time when the block request was last updated", example = "2025-05-21 16:00:00")
    private LocalDateTime updatedAt;

    @Schema(
            description = "Status of the block request",
            example = "COMPLETED",
            allowableValues = {"COMPLETED", "REJECTED"}
    )
    private String status;

    @Schema(description = "Unique identifier of the card where the transaction was initiated", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID cardId;

    @Schema(description = "User ID associated with the card", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID userId;
}
