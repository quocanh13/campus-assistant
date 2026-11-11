package com.example.backend.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminResponse {
    private Integer id;
    private UUID accountId;
    private String name;
    private LocalDateTime createdAt;
}
