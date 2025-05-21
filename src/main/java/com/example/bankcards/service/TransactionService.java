package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.TransactionFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface TransactionService {
    Page<TransactionDto> findAll(PageRequest pageRequest, TransactionFilterDto transactionFilterDto);

    Page<TransactionDto> findAllByCardId(UUID id, PageRequest pageRequest);

    Page<TransactionDto> findAllByUserId(UUID id, PageRequest pageRequest);

    Page<TransactionDto> findAllByUsername(String username, PageRequest pageRequest);

    Page<TransactionDto> findAllByUsernameAndCardId(String username, UUID id, PageRequest pageRequest);
}
