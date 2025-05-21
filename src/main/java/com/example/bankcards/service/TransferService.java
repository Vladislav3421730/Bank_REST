package com.example.bankcards.service;

import com.example.bankcards.dto.request.RechargeRequestDto;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.request.WithdrawalRequestDto;
import com.example.bankcards.dto.response.RechargeResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.dto.response.WithdrawalResponseDto;

public interface TransferService {

    WithdrawalResponseDto withdrawal(String email, WithdrawalRequestDto withdrawalRequestDto);

    RechargeResponseDto recharge(String email, RechargeRequestDto rechargeRequestDto);

    TransferResponseDto transfer(String email, TransferRequestDto transferRequestDto);
}
