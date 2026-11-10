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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateStudentAgentConversationRequest;
import com.example.backend.dto.request.PatchStudentAgentConversationRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.conversation.StudentAgentConversationResponse;
import com.example.backend.security.JwtData;
import com.example.backend.service.StudentAgentConversationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/student-agent-conversations")
@AllArgsConstructor
public class StudentAgentConversationController {
    private final StudentAgentConversationService conversationService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentAgentConversationResponse>> createConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @Valid @RequestBody CreateStudentAgentConversationRequest request) {
        StudentAgentConversationResponse conversation =
                conversationService.createConversation(jwtData.getAccountId(), request);
        BaseResponse<StudentAgentConversationResponse> response = BaseResponse.success(
                "Create conversation successfully", conversation);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<StudentAgentConversationResponse>>> getConversations(
            @AuthenticationPrincipal JwtData jwtData) {
        List<StudentAgentConversationResponse> conversations =
                conversationService.getConversations(jwtData.getAccountId());
        BaseResponse<List<StudentAgentConversationResponse>> response = BaseResponse.success(
                "Get conversations successfully", conversations);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentAgentConversationResponse>> getConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID id) {
        StudentAgentConversationResponse conversation =
                conversationService.getConversation(id, jwtData.getAccountId());
        BaseResponse<StudentAgentConversationResponse> response = BaseResponse.success(
                "Get conversation successfully", conversation);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentAgentConversationResponse>> patchConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID id,
            @Valid @RequestBody PatchStudentAgentConversationRequest request) {
        StudentAgentConversationResponse conversation =
                conversationService.patchConversation(id, jwtData.getAccountId(), request);
        BaseResponse<StudentAgentConversationResponse> response = BaseResponse.success(
                "Update conversation successfully", conversation);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteConversation(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID id) {
        conversationService.deleteConversation(id, jwtData.getAccountId());
        BaseResponse<Void> response = BaseResponse.success(
                "Delete conversation successfully", null);
        return ResponseEntity.ok().body(response);
    }
}
