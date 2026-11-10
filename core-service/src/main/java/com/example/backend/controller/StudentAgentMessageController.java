package com.example.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateStudentAgentMessageRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.message.StudentAgentMessageResponse;
import com.example.backend.security.JwtData;
import com.example.backend.service.StudentAgentMessageService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping({
        "/student-agent-conversation/{conversationId}/messages",
        "/student-agent-conversations/{conversationId}/messages"
})
@AllArgsConstructor
public class StudentAgentMessageController {
    private final StudentAgentMessageService messageService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentAgentMessageResponse>> createMessage(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID conversationId,
            @Valid @RequestBody CreateStudentAgentMessageRequest request) {
        StudentAgentMessageResponse message = messageService.createMessage(
                conversationId, jwtData.getAccountId(), request);
        BaseResponse<StudentAgentMessageResponse> response = BaseResponse.success(
                "Create message successfully", message);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<StudentAgentMessageResponse>>> getMessages(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID conversationId) {
        List<StudentAgentMessageResponse> messages = messageService.getMessages(
                conversationId, jwtData.getAccountId());
        BaseResponse<List<StudentAgentMessageResponse>> response = BaseResponse.success(
                "Get messages successfully", messages);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<BaseResponse<StudentAgentMessageResponse>> getMessage(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable UUID conversationId,
            @PathVariable UUID messageId) {
        StudentAgentMessageResponse message = messageService.getMessage(
                conversationId, messageId, jwtData.getAccountId());
        BaseResponse<StudentAgentMessageResponse> response = BaseResponse.success(
                "Get message successfully", message);
        return ResponseEntity.ok().body(response);
    }
}
