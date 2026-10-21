package com.example.backend.dto.response.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter 
@Builder 
@AllArgsConstructor 
public class CreateUserResponse {
    private UUID id;
    private String username;
    private String name;
    private String role;
}
