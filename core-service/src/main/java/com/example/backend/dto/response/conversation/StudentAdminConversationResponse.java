package com.example.backend.dto.response.conversation;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.backend.entity.ConversationState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudentAdminConversationResponse {
    private UUID id;
    private String title;
    private Integer studentId;
    private Integer adminId;
    private ConversationState state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
