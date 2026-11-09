package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateStudentRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.student.StudentResponse;
import com.example.backend.service.StudentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/students")
@AllArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {
        StudentResponse student = studentService.createStudent(request);
        BaseResponse<StudentResponse> response = BaseResponse.success(
                "Create student successfully", student);
        return ResponseEntity.ok().body(response);
    }
}
