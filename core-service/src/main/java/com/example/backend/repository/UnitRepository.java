package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Unit;

public interface UnitRepository extends JpaRepository<Unit, String>{
}
