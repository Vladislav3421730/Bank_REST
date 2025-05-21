package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.RechargeRequestDto;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.request.WithdrawalRequestDto;
import com.example.bankcards.dto.response.RechargeResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.dto.response.WithdrawalResponseDto;
import com.example.bankcards.exception.*;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.util.factory.TransactionFactory;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.Transaction;
import com.example.bankcards.model.enums.OperationResult;
import com.example.bankcards.model.enums.OperationType;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.util.CardValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final CardValidationUtils cardValidationUtils;
    private final TransactionRepository transactionRepository;
    private final CardMapper cardMapper;

    @Override
    @Transactional(noRollbackFor = {CardLimitException.class, CardBalanceException.class, CardStatusException.class})
    public WithdrawalResponseDto withdrawal(String email, WithdrawalRequestDto withdrawalRequestDto) {

        String cardNumber = withdrawalRequestDto.getNumber();
        BigDecimal withdrawalAmount = withdrawalRequestDto.getAmount();

        Card card = findCardByUsernameAndNumber(email, cardNumber);

        cardValidationUtils.validateStatus(card, withdrawalAmount, OperationType.WITHDRAWAL);
        cardValidationUtils.validateBalance(card, withdrawalAmount, OperationType.WITHDRAWAL);
        cardValidationUtils.validateLimit(card, withdrawalAmount, OperationType.WITHDRAWAL);

        card.setBalance(card.getBalance().subtract(withdrawalAmount));
        cardRepository.save(card);

        Transaction transaction = TransactionFactory
                .create(card, withdrawalAmount, OperationType.WITHDRAWAL, OperationResult.SUCCESSFULLY);
        transactionRepository.save(transaction);

        return WithdrawalResponseDto.builder()
                .cardId(card.getId())
                .number(cardMapper.mapNumberFromCardToCardDto(card.getNumber()))
                .withdrawalAmount(withdrawalAmount)
                .remainingBalance(card.getBalance())
                .transferTime(LocalDateTime.now())
                .userId(card.getUser().getId())
                .build();
    }

    @Override
    @Transactional(noRollbackFor = {CardStatusException.class})
    public RechargeResponseDto recharge(String email, RechargeRequestDto rechargeRequestDto) {

        String cardNumber = rechargeRequestDto.getNumber();
        BigDecimal rechargeAmount = rechargeRequestDto.getAmount();

        Card card = findCardByUsernameAndNumber(email, cardNumber);

        cardValidationUtils.validateStatus(card, rechargeAmount, OperationType.RECHARGE);

        card.setBalance(card.getBalance().add(rechargeAmount));
        cardRepository.save(card);

        Transaction transaction = TransactionFactory
                .create(card, rechargeAmount, OperationType.RECHARGE, OperationResult.SUCCESSFULLY);
        transactionRepository.save(transaction);

        return RechargeResponseDto.builder()
                .cardId(card.getId())
                .number(cardMapper.mapNumberFromCardToCardDto(card.getNumber()))
                .rechargeAmount(rechargeAmount)
                .balance(card.getBalance())
                .transferTime(LocalDateTime.now())
                .userId(card.getUser().getId())
                .build();

    }

    @Override
    @Transactional(noRollbackFor = {CardBalanceException.class, CardStatusException.class, CardsAreTheSameException.class})
    public TransferResponseDto transfer(String email, TransferRequestDto transferRequestDto) {

        String cardNumber = transferRequestDto.getNumber();
        String targetCardNumber = transferRequestDto.getTargetNumber();
        BigDecimal transferAmount = transferRequestDto.getAmount();

        Card card = findCardByUsernameAndNumber(email, cardNumber);
        cardValidationUtils.validateStatus(card, transferAmount, OperationType.TRANSFER);

        Card targetCard = findCardByUsernameAndNumber(email, targetCardNumber);
        cardValidationUtils.validateStatus(targetCard, transferAmount, OperationType.TRANSFER);
        cardValidationUtils.validateNumbers(card, targetCard.getId(), transferAmount, OperationType.TRANSFER);
        cardValidationUtils.validateBalanceForTransfer(card, targetCard, transferAmount, OperationType.TRANSFER);

        card.setBalance(card.getBalance().subtract(transferAmount));
        targetCard.setBalance(targetCard.getBalance().add(transferAmount));

        cardRepository.save(card);
        cardRepository.save(targetCard);

        Transaction transaction = TransactionFactory
                .create(card, targetCard, transferAmount, OperationType.TRANSFER, OperationResult.SUCCESSFULLY);
        transactionRepository.save(transaction);

        return TransferResponseDto.builder()
                .cardId(card.getId())
                .targetCardId(targetCard.getId())
                .number(cardMapper.mapNumberFromCardToCardDto(card.getNumber()))
                .targetNumber(cardMapper.mapNumberFromCardToCardDto(targetCard.getNumber()))
                .transferTime(LocalDateTime.now())
                .balance(card.getBalance())
                .transferAmount(transferAmount)
                .userId(card.getUser().getId())
                .build();
    }

    public Card findCardByUsernameAndNumber(String username, String number) {
        return cardRepository.findCardByUserUsernameAndNumber(username, number).orElseThrow(() -> {
            log.error("Card with number {} wasn't founded", number);
            throw new CardNotFoundException(String.format("Card with number %s wasn't found", number));
        });
    }
}
