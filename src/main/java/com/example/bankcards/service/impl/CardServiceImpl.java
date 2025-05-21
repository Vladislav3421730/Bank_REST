package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.FilterCardDto;
import com.example.bankcards.dto.request.AddCardRequestDto;
import com.example.bankcards.dto.request.StatusCardRequestDto;
import com.example.bankcards.exception.CardAlreadyExistException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.util.Constants;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.Limit;
import com.example.bankcards.model.User;
import com.example.bankcards.model.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.PredicateFactory;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Override
    @Transactional
    public CardDto save(AddCardRequestDto addCardRequestDto) {
        User user = userRepository.findById(addCardRequestDto.getUserId()).orElseThrow(() -> {
            log.error("User with id {} wasn't found", addCardRequestDto.getUserId());
            throw new UserNotFoundException(String.format("User with id %s wasn't found", addCardRequestDto.getUserId()));
        });

        if (cardRepository.existsCardByNumber(addCardRequestDto.getNumber())) {
            log.error("Card already exists");
            throw new CardAlreadyExistException("Card already exists");
        }

        Card card = Card.builder()
                .user(user)
                .number(addCardRequestDto.getNumber())
                .build();

        Limit limit = new Limit();
        limit.setCard(card);
        card.setLimit(limit);

        cardRepository.save(card);
        log.info("Card with number {} was successfully saved", addCardRequestDto.getNumber());
        return cardMapper.toDto(card);
    }


    @Override
    @Transactional
    public void delete(UUID id) {
        if (!cardRepository.existsById(id)) {
            log.error("Card with id {} wasn't founded", id);
            throw new CardNotFoundException(String.format("Card with if %s wasn't found", id));
        }
        cardRepository.deleteById(id);
        log.info("Card with id {} was successfully deleted", id);
    }

    @Override
    @Transactional
    public void updateStatus(UUID id, StatusCardRequestDto status) {
        Card card = cardRepository.findById(id).orElseThrow(() -> {
            log.error("Card with id {} wasn't founded", id);
            throw new CardNotFoundException(String.format("Card with if %s wasn't found", id));
        });
        card.setStatus(CardStatus.valueOf(status.getStatus()));
        cardRepository.save(card);
        log.info("Card with id {} was successfully updated with status {}", card.getId(), status.getStatus());
    }

    @Override
    public Page<CardDto> findAll(PageRequest pageRequest, FilterCardDto filterCardDto) {

        Specification<Card> spec = (root, query, cb) -> {
            List<Predicate> predicates = PredicateFactory.formPredicates(cb, root, filterCardDto);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return cardRepository.findAll(spec, pageRequest)
                .map(cardMapper::toDto);
    }

    @Override
    public Page<CardDto> findAllByUsername(String username, PageRequest pageRequest, FilterCardDto filterCardDto) {
        log.info("Trying get all user's cards with username {}", username);

        Specification<Card> spec = (root, query, cb) -> {
            List<Predicate> predicates = PredicateFactory.formPredicates(cb, root, filterCardDto);
            predicates.add(cb.equal(root.get(Constants.USER_COLUMN).get(Constants.USERNAME_COLUMN), username));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return cardRepository.findAll(spec, pageRequest)
                .map(cardMapper::toDto);
    }

    @Override
    public CardDto findById(UUID id) {
        return cardRepository.findById(id)
                .map(cardMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Card with id {} wasn't founded", id);
                    throw new CardNotFoundException(String.format("Card with if %s wasn't found", id));
                });
    }


}
