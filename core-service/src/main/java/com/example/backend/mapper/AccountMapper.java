package com.example.backend.mapper;

import com.example.backend.dto.response.account.AccountResponse;
import com.example.backend.entity.Account;

public class AccountMapper {
    public static AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
