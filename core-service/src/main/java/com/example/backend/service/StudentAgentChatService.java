package com.example.backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend.client.AgentClient;
import com.example.backend.client.response.AgentChatData;
import com.example.backend.dto.request.ChatWithAgentRequest;
import com.example.backend.dto.request.CreateStudentAgentMessageRequest;
import com.example.backend.dto.response.message.ChatWithAgentResponse;
import com.example.backend.dto.response.message.StudentAgentMessageResponse;
import com.example.backend.entity.Student;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.repository.StudentRepository;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentAgentChatService {
    private final StudentRepository studentRepository;
    private final StudentAgentMessageService messageService;
    private final AgentClient agentClient;

    public ChatWithAgentResponse chatWithAgent(
            UUID conversationId,
            UUID accountId,
            ChatWithAgentRequest request) {
        
        Student student = studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.STUDENT_PROFILE_NOT_FOUND,
                        ResourceUtil.createArgs(accountId)));

        CreateStudentAgentMessageRequest studentMsgReq = new CreateStudentAgentMessageRequest();
        studentMsgReq.setContent(request.getContent());
        StudentAgentMessageResponse studentMessageResponse = messageService.createMessage(
                conversationId, accountId, studentMsgReq);

        AgentChatData agentData = agentClient.chat(
                conversationId,
                request.getContent(),
                student.getId());

        StudentAgentMessageResponse assistantMessageResponse = messageService.createAssistantMessage(
                conversationId,
                agentData.getAnswer());

        return ChatWithAgentResponse.builder()
                .studentMessage(studentMessageResponse)
                .assistantMessage(assistantMessageResponse)
                .relatedDocument(agentData.getRelatedDocument())
                .toolData(agentData.getToolData())
                .build();
    }
}
