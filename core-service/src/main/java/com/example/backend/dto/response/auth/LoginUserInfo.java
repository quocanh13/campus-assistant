package com.example.backend.dto.response.auth;

import java.util.UUID;

import com.example.backend.entity.AccountRole;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginUserInfo {
    private UUID id;
    private String email;
    private AccountRole role;
}
