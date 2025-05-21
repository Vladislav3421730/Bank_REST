package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for registering a new user")
public class RegisterUserRequestDto {

    @Schema(description = "LastName for the new user. Must be at least 3 characters long.", example = "john_doe")
    @NotBlank(message = "LastName cannot be blank.")
    private String firstName;

    @Schema(description = "LastName for the new user. Must be at least 3 characters long.", example = "john_doe")
    @NotBlank(message = "LastName cannot be blank.")
    private String lastName;

    @Schema(description = "Username of the new user. Must be a valid email format.", example = "john.doe@example.com")
    @NotBlank(message = "username cannot be blank.")
    @Size(min = 6, message = "Username must be at least 6 characters long.")
    private String username;

    @Schema(description = "Password for the new user. Must be at least 6 characters long.", example = "strongpassword123")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @Schema(description = "Confirm password to verify the password entered by the user.", example = "strongpassword123")
    @NotBlank(message = "Confirm cannot be blank.")
    private String confirmPassword;
}
