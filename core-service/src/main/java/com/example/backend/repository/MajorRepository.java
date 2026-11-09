package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Major;

public interface MajorRepository extends JpaRepository<Major, String>{
}
