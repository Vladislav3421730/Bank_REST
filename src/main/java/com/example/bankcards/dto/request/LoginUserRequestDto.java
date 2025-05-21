package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "DTO for user login containing email and password")
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequestDto {

    @NotBlank(message = "Username must be not blank")
    @Schema(description = "User's login", example = "vlad123")
    private String username;

    @NotBlank(message = "Password must be not blank")
    @Schema(description = "User's password", example = "q1w2e3")
    private String password;
}
