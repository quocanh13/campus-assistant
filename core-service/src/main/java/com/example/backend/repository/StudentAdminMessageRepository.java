package com.example.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.StudentAdminMessage;

public interface StudentAdminMessageRepository extends JpaRepository<StudentAdminMessage, UUID> {
    List<StudentAdminMessage> findAllByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    Optional<StudentAdminMessage> findByIdAndConversationId(UUID id, UUID conversationId);
}
