package com.example.bankcards.service;

import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.request.LoginUserRequestDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;

public interface AuthService {

    JwtResponseDto createAuthToken(LoginUserRequestDto user);

    void registerUser(RegisterUserRequestDto user);
}
