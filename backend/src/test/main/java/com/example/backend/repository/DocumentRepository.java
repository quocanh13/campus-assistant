package com.example.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findAllByOrderByUploadedAtAsc(Pageable pageable);
    List<Document> findByName(String name);
}
