package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.request.AddCardRequestDto;
import com.example.bankcards.dto.request.StatusCardRequestDto;
import com.example.bankcards.exception.CardAlreadyExistException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.service.CardService;
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
public class CardControllerTests {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CardController cardController;

    private static UUID id;
    private static CardDto validCardDto;
    private static CardNotFoundException cardNotFoundException;
    private static AddCardRequestDto createCardValidRequestDto;
    private static CardDto cardDtoForCreating;
    private static AddCardRequestDto createCardInvalidRequestDto;
    private static CardAlreadyExistException cardAlreadyExistException;
    private static UserNotFoundException userNotFoundException;
    private static StatusCardRequestDto statusCardValidRequestDto;
    private static StatusCardRequestDto statusCardInvalidRequestDto;

    @BeforeAll
    static void init() {
        id = UUID.randomUUID();

        validCardDto = new CardDto();
        validCardDto.setId(id);
        cardNotFoundException = new CardNotFoundException("card not found");

        createCardValidRequestDto = new AddCardRequestDto();
        createCardValidRequestDto.setUserId(id);
        createCardValidRequestDto.setNumber("1234 5678 3456 7980");

        cardDtoForCreating = new CardDto();
        cardDtoForCreating.setNumber(createCardValidRequestDto.getNumber());
        cardDtoForCreating.setUserId(id);

        createCardInvalidRequestDto = new AddCardRequestDto();
        createCardInvalidRequestDto.setUserId(id);
        createCardInvalidRequestDto.setNumber("Invalid card number");

        cardAlreadyExistException = new CardAlreadyExistException("already exist");
        userNotFoundException = new UserNotFoundException("user not found");

        statusCardValidRequestDto = new StatusCardRequestDto("ACTIVE");
        statusCardInvalidRequestDto = new StatusCardRequestDto("Wrong status");

    }

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnCard_whenIdIsValid() throws Exception {

        when(cardService.findById(id)).thenReturn(validCardDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

    }

    @Test
    void shouldReturn404_whenCardNotFoundById() throws Exception {

        when(cardService.findById(id)).thenThrow(cardNotFoundException);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.message").value(cardNotFoundException.getMessage()),
                        jsonPath("$.code").value(404),
                        jsonPath("$.timestamp").value(notNullValue())
                );

    }

    @Test
    void shouldCreateCard_whenRequestIsValid() throws Exception {

        when(cardService.save(createCardValidRequestDto)).thenReturn(cardDtoForCreating);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/cards")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardValidRequestDto))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.number").value(cardDtoForCreating.getNumber()),
                        jsonPath("$.userId").value(cardDtoForCreating.getUserId().toString())
                );
    }

    @Test
    void shouldReturn400_whenCreateCardRequestIsInvalid() throws Exception {

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/cards")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardInvalidRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errors.number").value(notNullValue()),
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldReturn400_whenCardNumberAlreadyExists() throws Exception {

        when(cardService.save(createCardValidRequestDto)).thenThrow(cardAlreadyExistException);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/cards")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardValidRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value(cardAlreadyExistException.getMessage()),
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldReturn404_whenUserIdNotFoundDuringCardCreation() throws Exception {

        when(cardService.save(createCardValidRequestDto)).thenThrow(userNotFoundException);

        mockMvc.perform((MockMvcRequestBuilders.post("/api/v1/cards")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardValidRequestDto))))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.message").value(userNotFoundException.getMessage()),
                        jsonPath("$.code").value(404),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldDeleteCard_whenIdIsValid() throws Exception {

        doNothing().when(cardService).delete(id);

        mockMvc.perform((MockMvcRequestBuilders.delete("/api/v1/cards/{id}", id)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404_whenDeletingNonExistingCard() throws Exception {

        doThrow(cardNotFoundException).when(cardService).delete(id);

        mockMvc.perform((MockMvcRequestBuilders.delete("/api/v1/cards/{id}", id)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.message").value(cardNotFoundException.getMessage()),
                        jsonPath("$.code").value(404),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldUpdateCardStatus_whenRequestIsValid() throws Exception {

        doNothing().when(cardService).updateStatus(id, statusCardValidRequestDto);

        mockMvc.perform((MockMvcRequestBuilders.patch("/api/v1/cards/{id}/status",id)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusCardValidRequestDto))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_whenCardStatusIsInvalid() throws Exception {

        mockMvc.perform((MockMvcRequestBuilders.patch("/api/v1/cards/{id}/status", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusCardInvalidRequestDto))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errors.status").value(notNullValue()),
                        jsonPath("$.code").value(400),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldReturn404_whenUpdatingStatusOfNonExistingCard() throws Exception {

        doThrow(cardNotFoundException).when(cardService).updateStatus(id, statusCardValidRequestDto);

        mockMvc.perform((MockMvcRequestBuilders.patch("/api/v1/cards/{id}/status", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusCardValidRequestDto))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
