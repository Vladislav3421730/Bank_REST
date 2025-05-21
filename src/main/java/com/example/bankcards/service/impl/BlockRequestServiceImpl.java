package com.example.bankcards.service.impl;

import com.example.bankcards.dto.BlockRequestDto;
import com.example.bankcards.dto.request.BlockCardRequestDto;
import com.example.bankcards.dto.request.StatusCardRequestDto;
import com.example.bankcards.exception.BlockRequestNotFoundException;
import com.example.bankcards.exception.CardAlreadyBlockedException;
import com.example.bankcards.exception.CardException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.model.BlockRequest;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.enums.BlockStatus;
import com.example.bankcards.model.enums.CardStatus;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.mapper.BlockRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockRequestServiceImpl implements BlockRequestService {

    private final BlockRequestRepository blockRequestRepository;
    private final CardRepository cardRepository;
    private final BlockRequestMapper blockRequestMapper;
    private final CardService cardService;

    @Override
    @Transactional
    public void createRequest(String username, UUID id) {
        Card card = cardRepository.findCardByUserUsernameAndId(username, id).orElseThrow(() -> {
            log.error("Card with id {} wasn't founded", username);
            throw new CardNotFoundException(String.format("Card with id %s wasn't found", username));
        });

        if(card.getStatus() == CardStatus.BLOCKED) {
            log.info("card with id {} was already blocked", card.getId());
            throw new CardAlreadyBlockedException(String.format("card with id %s was already blocked", card.getId()));
        }

        Optional<BlockRequest> checkTimeBlock = blockRequestRepository
                .findFirstByCardIdOrderByCreatedAtDesc(card.getId());
        if(checkTimeBlock.isPresent()) {
          BlockRequest blockRequestCheck = checkTimeBlock.get();
          if(blockRequestCheck.getCreatedAt().plusDays(7).isAfter(LocalDateTime.now())) {
              log.info("Block request creation attempt rejected: previous block request was created at {}",
                      blockRequestCheck.getCreatedAt());
              throw new CardException("You can create the next block request only after 7 days from the start of the previous request");
          }
        }

        BlockRequest blockRequest = new BlockRequest(card, card.getUser());
        blockRequestRepository.save(blockRequest);
        log.info("Block request was created successfully on card {}", id);
    }

    @Override
    public Page<BlockRequestDto> findAll(PageRequest pageRequest) {
        return blockRequestRepository.findAll(pageRequest)
                .map(blockRequestMapper::toDto);
    }

    @Override
    public Page<BlockRequestDto> findAllByCardId(UUID id, PageRequest pageRequest) {
        return blockRequestRepository.findByCardId(id, pageRequest)
                .map(blockRequestMapper::toDto);
    }

    @Override
    public Page<BlockRequestDto> findAllByUserId(UUID id, PageRequest pageRequest) {
        return blockRequestRepository.findByUserId(id, pageRequest)
                .map(blockRequestMapper::toDto);
    }

    @Override
    @Transactional
    public BlockRequestDto updateStatus(UUID id, BlockCardRequestDto blockCardRequestDto) {
        BlockRequest blockRequest = blockRequestRepository.findById(id).orElseThrow(() -> {
            log.error("Block request with id {} wasn't founded", id);
            throw new BlockRequestNotFoundException(String.format("Block request with if %s wasn't found", id));
        });

        blockRequest.setStatus(BlockStatus.valueOf(blockCardRequestDto.getStatus()));
        Card card = blockRequest.getCard();

        if(blockRequest.getStatus().equals(BlockStatus.COMPLETED)) {
            cardService.updateStatus(card.getId(), new StatusCardRequestDto(CardStatus.BLOCKED.name()));
        }

        blockRequest.setUpdatedAt(LocalDateTime.now());
        blockRequestRepository.save(blockRequest);
        log.info("Block request was updated with status {}", blockRequest.getStatus().name());
        return blockRequestMapper.toDto(blockRequest);
    }
}
