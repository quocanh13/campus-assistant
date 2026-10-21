package com.example.backend.dto.response.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder 
@AllArgsConstructor
public class GetUserResponse {
    private UUID id;
    private String username;
    private String name;
    private String role;
}
