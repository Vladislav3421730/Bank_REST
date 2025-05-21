package com.example.bankcards.dto.request;

import com.example.bankcards.util.validation.CardNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO for withdrawing funds from a card")
public class WithdrawalRequestDto {

    @CardNumber
    @Schema(description = "Card number from which funds will be withdrawn", example = "1234567890123456")
    private String number;

    @DecimalMin(value = "5.00", message = "Minimum withdrawal amount is 5.00")
    @Schema(description = "Amount to be withdrawn", example = "50.00")
    private BigDecimal amount;
}
