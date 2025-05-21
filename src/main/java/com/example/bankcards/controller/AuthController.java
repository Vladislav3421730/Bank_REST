package com.example.bankcards.controller;

import com.example.bankcards.dto.error.AppErrorDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.request.LoginUserRequestDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization (login, registration)")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns a Access")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid login credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<JwtResponseDto> createToken(@RequestBody @Valid LoginUserRequestDto userDto) {
        JwtResponseDto jwtResponseDto = authService.createAuthToken(userDto);
        return ResponseEntity.ok(jwtResponseDto);
    }

    @PostMapping("/register")
    @Operation(summary = "Registration", description = "Registers a user and returns the registered user")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully registered user"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Registration failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterUserRequestDto registerUserRequestDto) {
        authService.registerUser(registerUserRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
