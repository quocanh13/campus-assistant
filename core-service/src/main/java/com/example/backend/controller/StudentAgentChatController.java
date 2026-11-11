package com.example.backend.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.ChatWithAgentRequest;
import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.message.ChatWithAgentResponse;
import com.example.backend.service.StudentAgentChatService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/student-agent-conversations")
@AllArgsConstructor
public class StudentAgentChatController {
    private final StudentAgentChatService studentAgentChatService;

    @PostMapping("/{conversationId}/chat")
    public ResponseEntity<BaseResponse<ChatWithAgentResponse>> chatWithAgent(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal UUID accountId,
            @Valid @RequestBody ChatWithAgentRequest request) {
        
        ChatWithAgentResponse response = studentAgentChatService.chatWithAgent(
                conversationId, accountId, request);

        return ResponseEntity.ok(BaseResponse.success(null, response));
    }
}
