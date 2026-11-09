package com.example.backend.security;

import java.util.UUID;

import com.example.backend.entity.AccountRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtData {
    private UUID accountId;
    private AccountRole role;
}
