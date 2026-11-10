package com.example.backend.dto.response.conversation;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudentAgentConversationResponse {
    private UUID id;
    private String title;
    private Integer studentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
