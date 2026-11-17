package com.example.backend.mapper;

import com.example.backend.dto.response.conversation.StudentAdminConversationResponse;
import com.example.backend.entity.StudentAdminConversation;

public class StudentAdminConversationMapper {
    public static StudentAdminConversationResponse toResponse(StudentAdminConversation conversation) {
        return StudentAdminConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .studentId(conversation.getStudentId())
                .adminId(conversation.getAdminId())
                .state(conversation.getState())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}
