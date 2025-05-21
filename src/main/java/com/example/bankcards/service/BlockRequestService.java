package com.example.bankcards.service;

import com.example.bankcards.dto.BlockRequestDto;
import com.example.bankcards.dto.request.BlockCardRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface BlockRequestService {

    void createRequest(String username, UUID id);

    Page<BlockRequestDto> findAll(PageRequest pageRequest);

    Page<BlockRequestDto> findAllByCardId(UUID id, PageRequest pageRequest);

    Page<BlockRequestDto> findAllByUserId(UUID id, PageRequest pageRequest);

    BlockRequestDto updateStatus(UUID id, BlockCardRequestDto blockCardRequestDto);
}
