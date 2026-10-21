package com.example.backend.dto.response.auth;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder 
@Getter 
public class LoginUserInfo {
    private UUID id;
    private String username;
    private String role;
    private String name;
}
