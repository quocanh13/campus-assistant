package com.example.backend.mapper;

import com.example.backend.dto.response.message.StudentAdminMessageResponse;
import com.example.backend.entity.StudentAdminMessage;

public class StudentAdminMessageMapper {
    public static StudentAdminMessageResponse toResponse(StudentAdminMessage message) {
        return StudentAdminMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
