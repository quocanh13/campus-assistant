package com.example.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.StudentAdminConversation;

public interface StudentAdminConversationRepository
        extends JpaRepository<StudentAdminConversation, UUID> {
    List<StudentAdminConversation> findAllByOrderByUpdatedAtDesc();

    List<StudentAdminConversation> findAllByStudentIdOrderByUpdatedAtDesc(Integer studentId);

    List<StudentAdminConversation> findAllByAdminIdOrderByUpdatedAtDesc(Integer adminId);
}
