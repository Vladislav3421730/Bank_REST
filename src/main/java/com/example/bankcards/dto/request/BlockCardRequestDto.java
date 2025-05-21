package com.example.bankcards.dto.request;

import com.example.bankcards.util.validation.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to block a card with a specific status (COMPLETED or REJECTED)")
public class BlockCardRequestDto {

    @NotBlank(message = "status must not be null")
    @BlockRequestStatus
    @Schema(description = "The status of the block request. Only COMPLETED or REJECTED are allowed.",
            example = "COMPLETED")
    private String status;
}
