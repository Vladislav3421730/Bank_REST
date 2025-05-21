package com.example.bankcards.controller;

import com.example.bankcards.dto.request.RechargeRequestDto;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.request.WithdrawalRequestDto;
import com.example.bankcards.dto.response.RechargeResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.dto.response.WithdrawalResponseDto;
import com.example.bankcards.exception.*;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTests {

    private MockMvc mockMvc;

    @Mock
    private TransferService transferService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TransferController transferController;

    private static final String USER_EMAIL = "vlad@gmail.com";
    private static RechargeRequestDto rechargeRequestDto;
    private static RechargeRequestDto rechargeInvalidRequestDto;
    private static RechargeResponseDto rechargeResponseDto;
    private static WithdrawalRequestDto withdrawalRequestDto;
    private static WithdrawalRequestDto withdrawalInvalidRequestDto;
    private static WithdrawalResponseDto withdrawalResponseDto;
    private static TransferRequestDto transferRequestDto;
    private static TransferRequestDto transferInvalidRequestDto;
    private static TransferResponseDto transferResponseDto;
    private static CardStatusException cardStatusException;
    private static CardNotFoundException cardNotFoundException;
    private static CardLimitException cardLimitException;
    private static CardBalanceException cardBalanceException;
    private static Principal principal;

    @BeforeAll
    static void init() {
        rechargeRequestDto = new RechargeRequestDto();
        rechargeRequestDto.setAmount(BigDecimal.TEN);
        rechargeRequestDto.setNumber("3456 6784 5689 3458");

        rechargeInvalidRequestDto = new RechargeRequestDto();
        rechargeInvalidRequestDto.setAmount(BigDecimal.ONE);
        rechargeInvalidRequestDto.setNumber("Invalid number");

        withdrawalRequestDto = new WithdrawalRequestDto();
        withdrawalRequestDto.setAmount(BigDecimal.TEN);
        withdrawalRequestDto.setNumber("3456 6784 5689 3458");

        withdrawalInvalidRequestDto = new WithdrawalRequestDto();
        withdrawalInvalidRequestDto.setAmount(BigDecimal.ONE);
        withdrawalInvalidRequestDto.setNumber("Invalid number");

        transferRequestDto = new TransferRequestDto();
        transferRequestDto.setNumber("3456 6784 5689 3458");
        transferRequestDto.setTargetNumber("3456 6756 5689 3409");
        transferRequestDto.setAmount(BigDecimal.TEN);

        transferResponseDto = new TransferResponseDto();
        transferResponseDto.setNumber(transferRequestDto.getNumber());
        transferResponseDto.setTargetNumber(transferRequestDto.getTargetNumber());
        transferResponseDto.setTransferAmount(rechargeRequestDto.getAmount());

        transferInvalidRequestDto = new TransferRequestDto();
        transferInvalidRequestDto.setNumber("34564 5689 3458");
        transferInvalidRequestDto.setTargetNumber("56 5689 3409");
        transferInvalidRequestDto.setAmount(BigDecimal.ONE);

        rechargeResponseDto = RechargeResponseDto.builder()
                .rechargeAmount(rechargeRequestDto.getAmount())
                .number(rechargeRequestDto.getNumber())
                .build();

        withdrawalResponseDto = WithdrawalResponseDto.builder()
                .number(withdrawalRequestDto.getNumber())
                .withdrawalAmount(withdrawalRequestDto.getAmount())
                .build();

        principal = () -> USER_EMAIL;

        cardStatusException = new CardStatusException("wrong card status");
        cardNotFoundException = new CardNotFoundException("card not found");
        cardLimitException = new CardLimitException("card limit exception");
        cardBalanceException = new CardBalanceException("card balance exception");

    }

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(transferController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testRechargeMoney() throws Exception {

        when(transferService.recharge(USER_EMAIL, rechargeRequestDto))
                .thenReturn(rechargeResponseDto);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/recharge")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeRequestDto))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.number").value(rechargeResponseDto.getNumber()),
                        jsonPath("$.rechargeAmount").value(rechargeResponseDto.getRechargeAmount())
                );
    }

    @Test
    void testRechargeMoneyWithInvalidData() throws Exception {

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/recharge")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeInvalidRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue()),
                        jsonPath("$.errors.amount").value(notNullValue()),
                        jsonPath("$.errors.number").value(notNullValue())
                );
    }

    @Test
    void testRechargeMoneyWithWrongStatus() throws Exception {

        when(transferService.recharge(USER_EMAIL, rechargeRequestDto)).thenThrow(cardStatusException);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/recharge")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue()),
                        jsonPath("$.message").value(cardStatusException.getMessage())
                );
    }

    @Test
    void testWithdrawalMoney() throws Exception {

        when(transferService.withdrawal(USER_EMAIL, withdrawalRequestDto)).thenReturn(withdrawalResponseDto);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/withdrawal")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeRequestDto))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.number").value(withdrawalResponseDto.getNumber()),
                        jsonPath("$.withdrawalAmount").value(withdrawalResponseDto.getWithdrawalAmount())
                );
    }

    @Test
    void testWithdrawalMoneyWithInvalidData() throws Exception {

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/withdrawal")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalInvalidRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue()),
                        jsonPath("$.errors.amount").value(notNullValue()),
                        jsonPath("$.errors.number").value(notNullValue())
                );
    }

    @Test
    void testWithdrawalMoneyWhenCardNotFound() throws Exception {

        when(transferService.withdrawal(USER_EMAIL, withdrawalRequestDto)).thenThrow(cardNotFoundException);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/withdrawal")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequestDto))))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.code").value(404),
                        jsonPath("$.timestamp").value(notNullValue()),
                        jsonPath("$.message").value(cardNotFoundException.getMessage())
                );
    }

    @Test
    void testWithdrawalMoneyWithSpentBalance() throws Exception {

        when(transferService.withdrawal(USER_EMAIL, withdrawalRequestDto)).thenThrow(cardLimitException);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/withdrawal")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue()),
                        jsonPath("$.message").value(cardLimitException.getMessage())
                );
    }

    @Test
    void testWithdrawalMoneyWithWrongBalance() throws Exception {

        when(transferService.withdrawal(USER_EMAIL, withdrawalRequestDto)).thenThrow(cardBalanceException);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer/withdrawal")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue()),
                        jsonPath("$.message").value(cardBalanceException.getMessage())
                );
    }

    @Test
    void testTransferMoneyWithInvalidData() throws Exception {

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/transfer")
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferInvalidRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue()),
                        jsonPath("$.errors.amount").value(notNullValue()),
                        jsonPath("$.errors.number").value(notNullValue()),
                        jsonPath("$.errors.targetNumber").value(notNullValue())
                );
    }
}
