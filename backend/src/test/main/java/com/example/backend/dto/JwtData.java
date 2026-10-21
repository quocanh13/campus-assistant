package com.example.backend.dto;

import java.util.UUID;

import com.example.backend.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtData {
    private UUID id;
    private String username;
    private Role role;
}
