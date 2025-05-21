package com.example.bankcards.controller;

import com.example.bankcards.dto.LimitDto;
import com.example.bankcards.dto.error.AppErrorDto;
import com.example.bankcards.dto.error.FieldErrorDto;
import com.example.bankcards.dto.request.LimitRequestDto;
import com.example.bankcards.service.LimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/limit")
@ApiResponses({
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request body",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FieldErrorDto.class))
        )
})
@Tag(name = "Limit Management", description = "Endpoints for managing daily and monthly spending limits")
@SecurityRequirement(name = "BearerAuthentication")
public class LimitController {

    private final LimitService limitService;

    @PutMapping("/daily")
    @Operation(summary = "Set daily limit", description = "Updates the user's daily spending limit")
    public ResponseEntity<LimitDto> setDayLimit(@RequestBody @Valid LimitRequestDto limitRequestDto) {
        LimitDto limitDto = limitService.updateLimit(limitRequestDto, true);
        return ResponseEntity.ok(limitDto);
    }

    @PutMapping("/monthly")
    @Operation(summary = "Set monthly limit", description = "Updates the user's monthly spending limit")
    public ResponseEntity<LimitDto> setMonthLimit(@RequestBody @Valid LimitRequestDto limitRequestDto) {
        LimitDto limitDto = limitService.updateLimit(limitRequestDto, false);
        return ResponseEntity.ok(limitDto);
    }

}
