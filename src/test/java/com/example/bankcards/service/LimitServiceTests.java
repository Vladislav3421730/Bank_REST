package com.example.bankcards.service;

import com.example.bankcards.dto.LimitDto;
import com.example.bankcards.dto.request.LimitRequestDto;
import com.example.bankcards.exception.LimitNotFoundException;
import com.example.bankcards.util.mapper.LimitMapper;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.Limit;
import com.example.bankcards.repository.LimitRepository;
import com.example.bankcards.service.impl.LimitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LimitServiceTests {

    @Mock
    private LimitRepository limitRepository;

    @Mock
    private LimitMapper limitMapper;

    @InjectMocks
    private LimitServiceImpl limitService;

    private LimitRequestDto limitRequestDto;
    private Limit limit;
    private LimitDto limitDto;
    private UUID cardId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cardId = UUID.randomUUID();
        Card card = new Card();
        card.setId(cardId);

        limitRequestDto = LimitRequestDto.builder()
                .cardId(cardId)
                .limit(BigDecimal.valueOf(1000L))
                .build();

        limit = Limit.builder()
                .card(card)
                .dailyLimit(BigDecimal.valueOf(500L))
                .monthlyLimit((BigDecimal.valueOf(5000L)))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        limitDto = LimitDto.builder()
                .cardId(cardId)
                .dailyLimit(limit.getDailyLimit())
                .monthlyLimit(limit.getMonthlyLimit())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void updateLimit_ShouldUpdateDailyLimit_WhenIsDailyTrue() {
        when(limitRepository.findByCardId(cardId)).thenReturn(Optional.of(limit));
        when(limitMapper.toDto(any(Limit.class))).thenAnswer(invocation -> {
            Limit updatedLimit = invocation.getArgument(0);
            return LimitDto.builder()
                    .cardId(updatedLimit.getCard().getId())
                    .dailyLimit(updatedLimit.getDailyLimit())
                    .monthlyLimit(updatedLimit.getMonthlyLimit())
                    .updatedAt(updatedLimit.getUpdatedAt())
                    .build();
        });

        LocalDateTime beforeUpdate = limit.getUpdatedAt();

        LimitDto result = limitService.updateLimit(limitRequestDto, true);

        assertEquals(limitRequestDto.getLimit(), limit.getDailyLimit());
        assertEquals(BigDecimal.valueOf(5000L), limit.getMonthlyLimit());
        assertNotNull(limit.getUpdatedAt());
        assertTrue(limit.getUpdatedAt().isAfter(beforeUpdate));

        verify(limitRepository).save(limit);

        assertEquals(limit.getCard().getId(), result.getCardId());
        assertEquals(limit.getDailyLimit(), result.getDailyLimit());
        assertEquals(limit.getMonthlyLimit(), result.getMonthlyLimit());
    }

    @Test
    void updateLimit_ShouldUpdateMonthlyLimit_WhenIsDailyFalse() {
        when(limitRepository.findByCardId(cardId)).thenReturn(Optional.of(limit));
        when(limitMapper.toDto(any(Limit.class))).thenReturn(limitDto);

        LocalDateTime beforeUpdate = limit.getUpdatedAt();

        LimitDto result = limitService.updateLimit(limitRequestDto, false);

        assertEquals(limitRequestDto.getLimit(), limit.getMonthlyLimit());
        assertEquals(BigDecimal.valueOf(500L), limit.getDailyLimit());
        assertNotNull(limit.getUpdatedAt());
        assertTrue(limit.getUpdatedAt().isAfter(beforeUpdate));

        verify(limitRepository).save(limit);

        assertEquals(limit.getCard().getId(), result.getCardId());
    }

    @Test
    void updateLimit_ShouldThrowException_WhenLimitNotFound() {
        when(limitRepository.findByCardId(cardId)).thenReturn(Optional.empty());

        LimitNotFoundException exception = assertThrows(LimitNotFoundException.class, () ->
                limitService.updateLimit(limitRequestDto, true));

        assertTrue(exception.getMessage().contains(cardId.toString()));

        verify(limitRepository, never()).save(any());
        verify(limitMapper, never()).toDto(any());
    }
}