package com.example.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_agent_conversations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentAgentConversation {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Builder.Default
    @Column(name = "title", nullable = false, length = 200)
    private String title = "New conversation";

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
