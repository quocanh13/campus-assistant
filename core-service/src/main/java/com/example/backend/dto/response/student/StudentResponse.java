package com.example.backend.dto.response.student;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudentResponse {
    private Integer id;
    private UUID accountId;
    private String name;
    private Integer cohort;
    private String major;
    private LocalDateTime createdAt;
}
