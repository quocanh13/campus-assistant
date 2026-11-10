package com.example.backend.mapper;

import com.example.backend.dto.response.message.StudentAgentMessageResponse;
import com.example.backend.entity.StudentAgentMessage;

public class StudentAgentMessageMapper {
    public static StudentAgentMessageResponse toResponse(StudentAgentMessage message) {
        return StudentAgentMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
