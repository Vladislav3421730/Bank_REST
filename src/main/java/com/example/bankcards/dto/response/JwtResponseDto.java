package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "DTO for JWT response containing access and refresh tokens")
@AllArgsConstructor
public class JwtResponseDto {

    @Schema(description = "Access token used for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjI4MjM2NzQ1fQ.k3rUfj1DikFbd1k6B3hJHfD9_xD6wM8uP9YzJ8gPrFs")
    private String accessToken;
}
