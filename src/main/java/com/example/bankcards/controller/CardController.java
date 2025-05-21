package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.FilterCardDto;
import com.example.bankcards.dto.error.AppErrorDto;
import com.example.bankcards.dto.request.AddCardRequestDto;
import com.example.bankcards.dto.request.StatusCardRequestDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Endpoints for managing cards (create, delete, get, update status)")
@SecurityRequirement(name = "BearerAuthentication")
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
)
public class CardController {

    private final CardService cardService;

    @GetMapping
    @Operation(summary = "Find all cards")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of cards returned successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<Page<CardDto>> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "minBalance", required = false) BigDecimal minBalance,
            @RequestParam(value = "maxBalance", required = false) BigDecimal maxBalance,
            @RequestParam(value = "expiredBefore", required = false) LocalDate expiredBefore,
            @RequestParam(value = "expiredAfter", required = false) LocalDate expiredAfter) {
        FilterCardDto filterCardDto = new FilterCardDto(minBalance, maxBalance, expiredBefore, expiredAfter);
        Page<CardDto> cards = cardService.findAll(PageRequest.of(page, pageSize), filterCardDto);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/me")
    @Operation(summary = "Find all cards for authorized user")
    @ApiResponse(responseCode = "200", description = "List of cards returned successfully")
    public ResponseEntity<Page<CardDto>> findAllUserCards(
            Principal principal,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "minBalance", required = false) BigDecimal minBalance,
            @RequestParam(value = "maxBalance", required = false) BigDecimal maxBalance,
            @RequestParam(value = "expiredBefore", required = false) LocalDate expiredBefore,
            @RequestParam(value = "expiredAfter", required = false) LocalDate expiredAfter) {
        FilterCardDto filterCardDto = new FilterCardDto(minBalance, maxBalance, expiredBefore, expiredAfter);
        Page<CardDto> cards = cardService.findAllByUsername(principal.getName(), PageRequest.of(page, pageSize), filterCardDto);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID", description = "Retrieve card information by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "card found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<CardDto> findById(@PathVariable UUID id) {
        CardDto cardDto = cardService.findById(id);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping
    @Operation(summary = "Add new card", description = "Creates and saves a new card")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid card data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<CardDto> saveCard(@RequestBody @Valid AddCardRequestDto addCardRequestDto) {
        CardDto cardDto = cardService.save(addCardRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card", description = "Deletes a card by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card successfully deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update card status", description = "Changes the status of a card (e.g., ACTIVE, BLOCKED)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card status successfully updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status value",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @RequestBody @Valid StatusCardRequestDto statusCardRequestDto) {
        cardService.updateStatus(id, statusCardRequestDto);
        return ResponseEntity.ok().build();
    }

}
