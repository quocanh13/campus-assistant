package com.example.backend.mapper;

import com.example.backend.dto.response.conversation.StudentAgentConversationResponse;
import com.example.backend.entity.StudentAgentConversation;

public class StudentAgentConversationMapper {
    public static StudentAgentConversationResponse toResponse(
            StudentAgentConversation conversation) {
        return StudentAgentConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .studentId(conversation.getStudentId())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}
