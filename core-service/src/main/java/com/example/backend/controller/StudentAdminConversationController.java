package com.example.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateStudentAdminConversationRequest;
import com.example.backend.dto.request.PatchStudentAdminConversationRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.conversation.StudentAdminConversationResponse;
import com.example.backend.security.JwtData;
import com.example.backend.service.StudentAdminConversationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class StudentAdminConversationController {
    private final StudentAdminConversationService conversationService;

    @PostMapping("/student-admin-conversations")
    public ResponseEntity<BaseResponse<StudentAdminConversationResponse>> createConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @Valid @RequestBody CreateStudentAdminConversationRequest request) {
        StudentAdminConversationResponse conversation =
                conversationService.createConversation(jwtData.getAccountId(), jwtData, request);
        BaseResponse<StudentAdminConversationResponse> response = BaseResponse.success(
                "Create conversation successfully", conversation);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Lấy toàn bộ conversation (cả student và admin đều có thể gọi).
     */
    @GetMapping("/student-admin-conversations")
    public ResponseEntity<BaseResponse<List<StudentAdminConversationResponse>>> getAllConversations(
            @AuthenticationPrincipal JwtData jwtData) {
        List<StudentAdminConversationResponse> conversations =
                conversationService.getAllConversations(jwtData.getAccountId());
        BaseResponse<List<StudentAdminConversationResponse>> response = BaseResponse.success(
                "Get all conversations successfully", conversations);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Lấy conversation của chính người gọi: jwt -> account_id -> student/admin id.
     */
    @GetMapping("/me/student-admin-conversations")
    public ResponseEntity<BaseResponse<List<StudentAdminConversationResponse>>> getMyConversations(
            @AuthenticationPrincipal JwtData jwtData) {
        List<StudentAdminConversationResponse> conversations =
                conversationService.getMyConversations(jwtData.getAccountId());
        BaseResponse<List<StudentAdminConversationResponse>> response = BaseResponse.success(
                "Get my conversations successfully", conversations);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/student-admin-conversations/{id}")
    public ResponseEntity<BaseResponse<StudentAdminConversationResponse>> getConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID id) {
        StudentAdminConversationResponse conversation =
                conversationService.getConversation(id, jwtData.getAccountId());
        BaseResponse<StudentAdminConversationResponse> response = BaseResponse.success(
                "Get conversation successfully", conversation);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/student-admin-conversations/{id}")
    public ResponseEntity<BaseResponse<StudentAdminConversationResponse>> patchConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID id,
            @Valid @RequestBody PatchStudentAdminConversationRequest request) {
        StudentAdminConversationResponse conversation =
                conversationService.patchConversation(id, jwtData.getAccountId(), request);
        BaseResponse<StudentAdminConversationResponse> response = BaseResponse.success(
                "Update conversation successfully", conversation);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/student-admin-conversations/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID id) {
        conversationService.deleteConversation(id, jwtData.getAccountId());
        BaseResponse<Void> response = BaseResponse.success(
                "Delete conversation successfully", null);
        return ResponseEntity.ok().body(response);
    }
}
