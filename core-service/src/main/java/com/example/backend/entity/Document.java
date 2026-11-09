package com.example.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;

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
@Table(name = "documents")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert 
@Builder
public class Document{
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "uploaded_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "valid_at")
    private LocalDateTime validAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expriedAt;
}
