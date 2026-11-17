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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CreateStudentAdminMessageRequest;
import com.example.backend.dto.request.PatchStudentAdminMessageRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.message.StudentAdminMessageContentResponse;
import com.example.backend.dto.response.message.StudentAdminMessageResponse;
import com.example.backend.security.JwtData;
import com.example.backend.service.StudentAdminMessageService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping({
        "/student_admin_conversations/{conversation_id}/messages",
        "/student-admin-conversations/{conversation_id}/messages"
})
@AllArgsConstructor
public class StudentAdminMessageController {
    private final StudentAdminMessageService messageService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentAdminMessageResponse>> createMessage(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable("conversation_id") UUID conversationId,
            @Valid @RequestBody CreateStudentAdminMessageRequest request) {
        StudentAdminMessageResponse message = messageService.createMessage(
                conversationId, jwtData.getAccountId(), request);
        BaseResponse<StudentAdminMessageResponse> response = BaseResponse.success(
                "Create message successfully", message);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<?>>> getMessages(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable("conversation_id") UUID conversationId,
            @RequestParam(defaultValue = "0") int index,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean onlyContent) {
        List<StudentAdminMessageResponse> messages = messageService.getMessages(
                conversationId, jwtData.getAccountId(), index, limit);

        List<?> data = onlyContent
                ? messages.stream()
                        .map((message) -> new StudentAdminMessageContentResponse(
                                message.getRole(), message.getContent()))
                        .toList()
                : messages;

        BaseResponse<List<?>> response = BaseResponse.success(
                "Get messages successfully", data);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<BaseResponse<StudentAdminMessageResponse>> getMessage(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable("conversation_id") UUID conversationId,
            @PathVariable UUID messageId) {
        StudentAdminMessageResponse message = messageService.getMessage(
                conversationId, messageId, jwtData.getAccountId());
        BaseResponse<StudentAdminMessageResponse> response = BaseResponse.success(
                "Get message successfully", message);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<BaseResponse<StudentAdminMessageResponse>> patchMessage(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable("conversation_id") UUID conversationId,
            @PathVariable UUID messageId,
            @Valid @RequestBody PatchStudentAdminMessageRequest request) {
        StudentAdminMessageResponse message = messageService.patchMessage(
                conversationId, messageId, jwtData.getAccountId(), request);
        BaseResponse<StudentAdminMessageResponse> response = BaseResponse.success(
                "Update message successfully", message);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<BaseResponse<Void>> deleteMessage(
            @AuthenticationPrincipal JwtData jwtData,
            @PathVariable("conversation_id") UUID conversationId,
            @PathVariable UUID messageId) {
        messageService.deleteMessage(conversationId, messageId, jwtData.getAccountId());
        BaseResponse<Void> response = BaseResponse.success(
                "Delete message successfully", null);
        return ResponseEntity.ok().body(response);
    }
}
