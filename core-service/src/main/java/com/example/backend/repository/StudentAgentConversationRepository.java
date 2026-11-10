package com.example.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.StudentAgentConversation;

public interface StudentAgentConversationRepository
        extends JpaRepository<StudentAgentConversation, UUID> {
    List<StudentAgentConversation> findAllByStudentIdOrderByUpdatedAtDesc(Integer studentId);
}
