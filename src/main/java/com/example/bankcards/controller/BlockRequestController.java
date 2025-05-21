package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockRequestDto;
import com.example.bankcards.dto.error.AppErrorDto;
import com.example.bankcards.dto.request.BlockCardRequestDto;
import com.example.bankcards.service.BlockRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/block")
@SecurityRequirement(name = "BearerAuthentication")
@Tag(name = "Block Requests Management", description = "Operations for managing block card requests")
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
)
public class BlockRequestController {

    private final BlockRequestService blockRequestService;

    @PatchMapping("/{cardId}")
    @Operation(summary = "Create a block request for a card",
            description = "Creates a block request for the specified card ID if not blocked yet and if 7 days passed since last request")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Block request was created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))),
    })
    public ResponseEntity<Void> createBlockRequest(Principal principal, @PathVariable UUID cardId) {
        blockRequestService.createRequest(principal.getName(), cardId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update block request status",
            description = "Updates the status of an existing block request (e.g., COMPLETED). If COMPLETED, card status will be set to BLOCKED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status was changed successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status value",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<BlockRequestDto> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid BlockCardRequestDto blockCardRequestDto) {
        BlockRequestDto blockRequestDto = blockRequestService.updateStatus(id, blockCardRequestDto);
        return ResponseEntity.ok(blockRequestDto);
    }

    @GetMapping
    @Operation(summary = "Get all block requests with pagination",
            description = "Returns a paginated list of all block requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests were found successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<Page<BlockRequestDto>> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page<BlockRequestDto> blockRequests = blockRequestService.findAll(PageRequest.of(page, pageSize));
        return ResponseEntity.ok(blockRequests);
    }

    @GetMapping("/{cardId}/cards")
    @Operation(summary = "Get block requests by card ID with pagination",
            description = "Returns a paginated list of block requests filtered by the specified card ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests were found successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<Page<BlockRequestDto>> findAllByCardId(
            @PathVariable UUID cardId,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page<BlockRequestDto> blockRequests = blockRequestService
                .findAllByCardId(cardId, PageRequest.of(page, pageSize));
        return ResponseEntity.ok(blockRequests);
    }

    @GetMapping("/{userId}/users")
    @Operation(summary = "Get block requests by user ID with pagination",
            description = "Returns a paginated list of block requests filtered by the specified user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests were found successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<Page<BlockRequestDto>> findAllByUserId(
            @PathVariable UUID userId,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page<BlockRequestDto> blockRequests = blockRequestService
                .findAllByUserId(userId, PageRequest.of(page, pageSize));
        return ResponseEntity.ok(blockRequests);
    }

}
