package com.example.backend.dto.response.message;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.backend.entity.MessageRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudentAgentMessageResponse {
    private UUID id;
    private UUID conversationId;
    private MessageRole role;
    private String content;
    private LocalDateTime createdAt;
}
