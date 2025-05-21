package com.example.bankcards.controller;

import com.example.bankcards.dto.error.AppErrorDto;
import com.example.bankcards.dto.request.RechargeRequestDto;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.request.WithdrawalRequestDto;
import com.example.bankcards.dto.response.RechargeResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.dto.response.WithdrawalResponseDto;
import com.example.bankcards.service.TransferService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfer")
@Tag(name = "Transfer Management", description = "Endpoints for executing transfers")
@SecurityRequirement(name = "BearerAuthentication")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfer was executed successfully"),
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Card not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        ),
})
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Transfer funds", description = "Transfer money from one card to another")
    public ResponseEntity<TransferResponseDto> transfer(
            Principal principal,
            @RequestBody @Valid TransferRequestDto transferRequestDto) {
        TransferResponseDto transferResponseDto = transferService.transfer(principal.getName(), transferRequestDto);
        return ResponseEntity.ok(transferResponseDto);
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "Withdraw money", description = "Initiate a withdrawal transaction for the current user")
    public ResponseEntity<WithdrawalResponseDto> withdrawal(
            Principal principal,
            @RequestBody @Valid WithdrawalRequestDto withdrawalRequestDto) {
        WithdrawalResponseDto withdrawalResponseDto = transferService.withdrawal(principal.getName(), withdrawalRequestDto);
        return ResponseEntity.ok(withdrawalResponseDto);
    }

    @PostMapping("/recharge")
    @Operation(summary = "Recharge card", description = "Recharge a card with a specified amount for the current user")
    public ResponseEntity<RechargeResponseDto> recharge(
            Principal principal,
            @RequestBody @Valid RechargeRequestDto rechargeRequestDto) {
        RechargeResponseDto rechargeResponseDto = transferService.recharge(principal.getName(), rechargeRequestDto);
        return ResponseEntity.ok(rechargeResponseDto);
    }
}
