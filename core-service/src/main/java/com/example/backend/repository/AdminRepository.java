package com.example.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    boolean existsByAccountId(UUID accountId);
}
