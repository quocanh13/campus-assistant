package com.example.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    boolean existsByAccountId(UUID accountId);
}
