package com.example.bankcards.service.impl;

import com.example.bankcards.dto.LimitDto;
import com.example.bankcards.dto.request.LimitRequestDto;
import com.example.bankcards.exception.LimitNotFoundException;
import com.example.bankcards.util.mapper.LimitMapper;
import com.example.bankcards.model.Limit;
import com.example.bankcards.repository.LimitRepository;
import com.example.bankcards.service.LimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitServiceImpl implements LimitService {

    private final LimitRepository limitRepository;
    private final LimitMapper limitMapper;

    @Override
    public LimitDto updateLimit(LimitRequestDto limitRequestDto, boolean isDaily) {
        UUID cardId = limitRequestDto.getCardId();
        Limit limit = limitRepository.findByCardId(cardId).orElseThrow(() -> {
            log.error("Limit wasn't found by card id {}", cardId);
            throw new LimitNotFoundException(String.format("Limit wasn't found by card id %s", cardId));
        });
        limit.setUpdatedAt(LocalDateTime.now());
        if (isDaily) {
            limit.setDailyLimit(limitRequestDto.getLimit());
        } else {
            limit.setMonthlyLimit(limitRequestDto.getLimit());
        }
        limitRepository.save(limit);
        log.info("Limit of card was successfully updated, daily limit: {}, monthly limit: {}",
                limit.getDailyLimit(), limit.getMonthlyLimit());
        return limitMapper.toDto(limit);
    }

}
