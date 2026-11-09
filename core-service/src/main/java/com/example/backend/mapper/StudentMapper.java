package com.example.backend.mapper;

import com.example.backend.dto.response.student.StudentResponse;
import com.example.backend.entity.Student;

public class StudentMapper {
    public static StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .accountId(student.getAccountId())
                .name(student.getName())
                .cohort(student.getCohort())
                .major(student.getMajor())
                .createdAt(student.getCreatedAt())
                .build();
    }
}
