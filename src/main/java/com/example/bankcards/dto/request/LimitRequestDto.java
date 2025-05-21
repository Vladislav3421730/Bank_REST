package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for setting a transaction limit on a specific card")
public class LimitRequestDto {

    @NotNull(message = "cardId must be not null")
    @Schema(description = "UUID of the card to apply the limit to", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID cardId;

    @NotNull(message = "limit must be not null")
    @DecimalMin(value = "0.00", message = "min amount for limit is 0")
    @Schema(description = "Limit value to be applied to the card", example = "5000.00", minimum = "0.00")
    private BigDecimal limit;
}
