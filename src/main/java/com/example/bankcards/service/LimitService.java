package com.example.bankcards.service;

import com.example.bankcards.dto.LimitDto;
import com.example.bankcards.dto.request.LimitRequestDto;

public interface LimitService {

    LimitDto updateLimit(LimitRequestDto limitRequestDto, boolean isDaily);

}
