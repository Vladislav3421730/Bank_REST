package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.request.BannedRequestDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface UserService {

    void save(RegisterUserRequestDto registerUserRequestDto);

    void delete(UUID id, String username);

    void banUser(UUID id, String username, BannedRequestDto bannedRequestDto);

    Page<UserDto> findAll(PageRequest pageRequest);

    UserDto findById(UUID id);
}
