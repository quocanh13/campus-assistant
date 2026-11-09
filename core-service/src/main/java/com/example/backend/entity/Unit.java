package com.example.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "units")
@Setter 
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Unit {
    @Id 
    private String code;
    private String name;
}
