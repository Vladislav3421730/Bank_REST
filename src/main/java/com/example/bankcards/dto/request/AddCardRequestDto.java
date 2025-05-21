package com.example.bankcards.dto.request;

import com.example.bankcards.util.validation.CardNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request to add a new card")
public class AddCardRequestDto {

    @NotNull(message = "userId must be not null")
    @Schema(description = "ID of the user to whom the card will be linked", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @CardNumber
    @NotBlank(message = "Card number must not be blank")
    @Schema(description = "Card number in format XXXX XXXX XXXX XXXX", example = "1234 5678 9012 3456")
    private String number;
}
