package com.example.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);
}
