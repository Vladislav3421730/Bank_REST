package com.example.bankcards.dto.request;

import com.example.bankcards.util.validation.CardNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO for transferring funds between two cards")
public class TransferRequestDto {

    @CardNumber
    @Schema(description = "Card number from which funds will be transferred", example = "1234567890123456")
    private String number;

    @CardNumber
    @Schema(description = "Card number to which funds will be transferred", example = "6543210987654321")
    private String targetNumber;

    @DecimalMin(value = "5.00", message = "Minimum amount for transfer is 5.00")
    @Schema(description = "Amount to be transferred", example = "100.00")
    private BigDecimal amount;
}
