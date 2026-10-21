package com.example.backend.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Builder 
public class User {
    @Id
    private UUID id;

    private String username;

    @Column(name = "password_hash")
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;
}
