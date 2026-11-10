package com.example.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.StudentAgentMessage;

public interface StudentAgentMessageRepository extends JpaRepository<StudentAgentMessage, UUID> {
    List<StudentAgentMessage> findAllByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    Optional<StudentAgentMessage> findByIdAndConversationId(UUID id, UUID conversationId);
}
