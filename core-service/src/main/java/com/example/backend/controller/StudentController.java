package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateStudentRequest;
import com.example.backend.dto.request.PatchStudentRequest;
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

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentResponse>> getStudent(
            @PathVariable Integer id) {
        StudentResponse student = studentService.getStudent(id);
        BaseResponse<StudentResponse> response = BaseResponse.success(
                "Get student successfully", student);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentResponse>> patchStudent(
            @PathVariable Integer id,
            @Valid @RequestBody PatchStudentRequest request) {
        StudentResponse student = studentService.patchStudent(id, request);
        BaseResponse<StudentResponse> response = BaseResponse.success(
                "Update student successfully", student);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteStudent(
            @PathVariable Integer id) {
        studentService.deleteStudent(id);
        BaseResponse<Void> response = BaseResponse.success(
                "Delete student successfully", null);
        return ResponseEntity.ok().body(response);
    }
}
