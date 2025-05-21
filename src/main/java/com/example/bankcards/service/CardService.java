package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.FilterCardDto;
import com.example.bankcards.dto.request.AddCardRequestDto;
import com.example.bankcards.dto.request.StatusCardRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface CardService {

    CardDto save(AddCardRequestDto addCardRequestDto);

    void delete(UUID id);

    void updateStatus(UUID id, StatusCardRequestDto status);

    Page<CardDto> findAll(PageRequest pageRequest, FilterCardDto filterCardDto);

    Page<CardDto> findAllByUsername(String username, PageRequest pageRequest, FilterCardDto filterCardDto);

    CardDto findById(UUID id);


}
