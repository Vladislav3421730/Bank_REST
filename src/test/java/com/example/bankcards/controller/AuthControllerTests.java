package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginUserRequestDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.exception.LoginFailedException;
import com.example.bankcards.exception.PasswordsNotTheSameException;
import com.example.bankcards.exception.RegistrationFailedException;
import com.example.bankcards.service.AuthService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuthController authController;

    private static LoginUserRequestDto validLoginRequest;
    private static LoginUserRequestDto invalidLoginRequest;
    private static LoginUserRequestDto badCredentialsLoginRequest;
    private static JwtResponseDto validJwtResponse;
    private static RegisterUserRequestDto validRegisterRequest;
    private static RegisterUserRequestDto invalidRegisterRequest;
    private static RegisterUserRequestDto differentPasswordsRequest;
    private static RegisterUserRequestDto existingEmailRequest;
    private static PasswordsNotTheSameException passwordsNotTheSameException;
    private static RegistrationFailedException registrationFailedException;
    private static LoginFailedException loginFailedException;

    @BeforeAll
    static void init() {
        validLoginRequest = new LoginUserRequestDto("user123", "q1w2e3");
        invalidLoginRequest = new LoginUserRequestDto("", "q1w2e3");
        badCredentialsLoginRequest = new LoginUserRequestDto("user", "q1w2e3");
        validJwtResponse = new JwtResponseDto("token");

        validRegisterRequest = new RegisterUserRequestDto(
                "vlad", "panasik", "vlad123", "q1w2e3", "q1w2e3"
        );
        invalidRegisterRequest = new RegisterUserRequestDto(
                "vlad", "", "vla", "q1w2e3", "q1w2e3"
        );
        differentPasswordsRequest = new RegisterUserRequestDto(
                "vlad", "panasik", "vlad@gmail.com", "q1w2e3", "q1w2"
        );
        existingEmailRequest = new RegisterUserRequestDto(
                "vlad", "panasik", "vlad@gmail.com", "q1w2e3", "q1w2"
        );

        passwordsNotTheSameException = new PasswordsNotTheSameException("Not the same");
        registrationFailedException = new RegistrationFailedException("Registration failed");
        loginFailedException = new LoginFailedException("bad credentials");
    }

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnJwtToken_whenLoginWithValidData() throws Exception {

        when(authService.createAuthToken(validLoginRequest)).thenReturn(validJwtResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(validJwtResponse.getAccessToken()));
    }

    @Test
    void shouldReturnBadRequest_whenLoginWithInvalidUsernameFormat() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.errors.username").value(notNullValue()),
                        jsonPath("$.code").value("400"),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldReturnBadRequest_whenLoginWithBadCredentials() throws Exception {

        when(authService.createAuthToken(badCredentialsLoginRequest)).thenThrow(loginFailedException);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badCredentialsLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.message").value(loginFailedException.getMessage()),
                        jsonPath("$.code").value("400"),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldRegisterUserSuccessfully_whenValidDataProvided() throws Exception {

        doNothing().when(authService).registerUser(validRegisterRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithInvalidData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.errors.lastName").value(notNullValue()),
                        jsonPath("$.errors.username").value(notNullValue()),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithDifferentPasswords() throws Exception {

        doThrow(passwordsNotTheSameException).when(authService).registerUser(differentPasswordsRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(differentPasswordsRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.message").value(passwordsNotTheSameException.getMessage()),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithExistingEmail() throws Exception {

        doThrow(registrationFailedException).when(authService).registerUser(existingEmailRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingEmailRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").value(400),
                        jsonPath("$.message").value(registrationFailedException.getMessage()),
                        jsonPath("$.timestamp").value(notNullValue())
                );
    }
}
