package com.example.bankcards.dto.request;

import com.example.bankcards.util.validation.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "DTO for updating the card status")
@AllArgsConstructor
@NoArgsConstructor
public class StatusCardRequestDto {

    @NotNull(message = "Status must not be null")
    @Status
    @Schema(description = "New card status. The allowed values are defined in the custom @Status annotation", example = "ACTIVE")
    private String status;
}

