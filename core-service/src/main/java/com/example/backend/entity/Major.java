package com.example.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "majors")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Major {
    @Id 
    private String code;
    private String name;
    private String instructionLanguage;
    private String unitCode;
}
