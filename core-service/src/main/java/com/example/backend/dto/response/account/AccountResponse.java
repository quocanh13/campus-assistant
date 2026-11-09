package com.example.backend.dto.response.account;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.backend.entity.AccountRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse {
    private UUID id;
    private String email;
    private AccountRole role;
    private LocalDateTime createdAt;
}
