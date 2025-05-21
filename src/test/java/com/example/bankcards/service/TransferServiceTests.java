package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.bankcards.exception.*;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.User;
import com.example.bankcards.model.enums.CardStatus;
import com.example.bankcards.model.enums.OperationType;
import com.example.bankcards.dto.request.*;
import com.example.bankcards.dto.response.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.impl.TransferServiceImpl;
import com.example.bankcards.util.CardValidationUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

class TransferServiceTests {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardValidationUtils cardValidationUtils;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private String username = "user";
    private Card card;
    private Card targetCard;
    private BigDecimal amount = BigDecimal.valueOf(100L);

    private WithdrawalRequestDto withdrawalRequestDto;
    private RechargeRequestDto rechargeRequestDto;
    private TransferRequestDto transferRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        card = new Card();
        card.setId(UUID.randomUUID());
        card.setNumber("1111 2222 3333 4444");
        card.setBalance(BigDecimal.valueOf(1000L));
        card.setStatus(CardStatus.ACTIVE);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("vlad");
        card.setUser(user);

        targetCard = new Card();
        targetCard.setId(UUID.randomUUID());
        targetCard.setNumber("5555 6666 7777 8888");
        targetCard.setBalance(BigDecimal.valueOf(500L));
        targetCard.setStatus(CardStatus.ACTIVE);

        withdrawalRequestDto = new WithdrawalRequestDto();
        withdrawalRequestDto.setNumber(card.getNumber());
        withdrawalRequestDto.setAmount(amount);

        rechargeRequestDto = new RechargeRequestDto();
        rechargeRequestDto.setNumber(card.getNumber());
        rechargeRequestDto.setAmount(amount);

        transferRequestDto = new TransferRequestDto();
        transferRequestDto.setNumber(card.getNumber());
        transferRequestDto.setTargetNumber(targetCard.getNumber());
        transferRequestDto.setAmount(amount);
    }

    @Test
    void shouldWithdrawSuccessfully_whenValidRequest() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        when(cardMapper.mapNumberFromCardToCardDto(card.getNumber())).thenReturn(card.getNumber());

        WithdrawalResponseDto response = transferService.withdrawal(username, withdrawalRequestDto);

        verify(cardValidationUtils).validateStatus(card, amount, OperationType.WITHDRAWAL);
        verify(cardValidationUtils).validateBalance(card, amount, OperationType.WITHDRAWAL);
        verify(cardValidationUtils).validateLimit(card, amount, OperationType.WITHDRAWAL);

        assertEquals(card.getId(), response.getCardId());
        assertEquals(card.getNumber(), response.getNumber());
        assertEquals(amount, response.getWithdrawalAmount());
        assertEquals(card.getBalance(), response.getRemainingBalance());
    }

    @Test
    void shouldThrowCardNotFoundException_whenWithdrawalCardNotFound() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, withdrawalRequestDto.getNumber())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> transferService.withdrawal(username, withdrawalRequestDto));
    }

    @Test
    void shouldThrowCardStatusException_whenWithdrawalCardBlocked() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        doThrow(CardStatusException.class).when(cardValidationUtils).validateStatus(card, amount, OperationType.WITHDRAWAL);

        assertThrows(CardStatusException.class, () -> transferService.withdrawal(username, withdrawalRequestDto));
    }

    @Test
    void shouldRechargeSuccessfully_whenValidRequest() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        when(cardMapper.mapNumberFromCardToCardDto(card.getNumber())).thenReturn(card.getNumber());

        RechargeResponseDto response = transferService.recharge(username, rechargeRequestDto);

        verify(cardValidationUtils).validateStatus(card, amount, OperationType.RECHARGE);
        assertEquals(card.getId(), response.getCardId());
        assertEquals(card.getNumber(), response.getNumber());
        assertEquals(amount, response.getRechargeAmount());
        assertEquals(card.getBalance(), response.getBalance());
    }

    @Test
    void shouldThrowCardStatusException_whenRechargeCardBlocked() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        doThrow(CardStatusException.class).when(cardValidationUtils).validateStatus(card, amount, OperationType.RECHARGE);

        assertThrows(CardStatusException.class, () -> transferService.recharge(username, rechargeRequestDto));
    }

    @Test
    void shouldTransferSuccessfully_whenValidRequest() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        when(cardRepository.findCardByUserUsernameAndNumber(username, targetCard.getNumber())).thenReturn(Optional.of(targetCard));

        TransferResponseDto response = transferService.transfer(username, transferRequestDto);

        verify(cardValidationUtils).validateStatus(card, amount, OperationType.TRANSFER);
        verify(cardValidationUtils).validateStatus(targetCard, amount, OperationType.TRANSFER);
        verify(cardValidationUtils).validateNumbers(card, targetCard.getId(), amount, OperationType.TRANSFER);
        verify(cardValidationUtils).validateBalanceForTransfer(card, targetCard, amount, OperationType.TRANSFER);

        assertEquals(card.getId(), response.getCardId());
        assertEquals(targetCard.getId(), response.getTargetCardId());
        assertEquals(amount, response.getTransferAmount());
    }

    @Test
    void shouldThrowCardNotFoundException_whenTransferSourceCardNotFound() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, transferRequestDto.getNumber())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> transferService.transfer(username, transferRequestDto));
    }

    @Test
    void shouldThrowCardNotFoundException_whenTransferTargetCardNotFound() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        when(cardRepository.findCardByUserUsernameAndNumber(username, transferRequestDto.getTargetNumber())).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> transferService.transfer(username, transferRequestDto));
    }

    @Test
    void shouldThrowCardBalanceException_whenTransferBalanceInsufficient() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        when(cardRepository.findCardByUserUsernameAndNumber(username, targetCard.getNumber())).thenReturn(Optional.of(targetCard));

        doThrow(CardBalanceException.class)
                .when(cardValidationUtils)
                .validateBalanceForTransfer(card, targetCard, amount, OperationType.TRANSFER);

        assertThrows(CardBalanceException.class, () -> transferService.transfer(username, transferRequestDto));
    }

    @Test
    void shouldThrowCardsAreTheSameException_whenTransferToSameCard() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        when(cardRepository.findCardByUserUsernameAndNumber(username, targetCard.getNumber())).thenReturn(Optional.of(card));

        doThrow(CardsAreTheSameException.class)
                .when(cardValidationUtils)
                .validateNumbers(card, card.getId(), amount, OperationType.TRANSFER);

        assertThrows(CardsAreTheSameException.class, () -> transferService.transfer(username, transferRequestDto));
    }

    @Test
    void shouldThrowCardStatusException_whenTransferCardBlocked() {
        when(cardRepository.findCardByUserUsernameAndNumber(username, card.getNumber())).thenReturn(Optional.of(card));
        when(cardRepository.findCardByUserUsernameAndNumber(username, targetCard.getNumber())).thenReturn(Optional.of(targetCard));

        doThrow(CardStatusException.class)
                .when(cardValidationUtils)
                .validateStatus(card, amount, OperationType.TRANSFER);

        assertThrows(CardStatusException.class, () -> transferService.transfer(username, transferRequestDto));
    }
}
