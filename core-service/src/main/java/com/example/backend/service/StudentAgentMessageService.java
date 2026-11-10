package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateStudentAgentMessageRequest;
import com.example.backend.dto.response.message.StudentAgentMessageResponse;
import com.example.backend.entity.MessageRole;
import com.example.backend.entity.StudentAgentMessage;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.StudentAgentMessageMapper;
import com.example.backend.repository.StudentAgentMessageRepository;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentAgentMessageService {
    private final StudentAgentMessageRepository messageRepository;
    private final StudentAgentConversationService conversationService;

    @Transactional
    public StudentAgentMessageResponse createMessage(
            UUID conversationId,
            UUID accountId,
            CreateStudentAgentMessageRequest request) {
        conversationService.getOwnedConversation(conversationId, accountId);

        UUID id = request.getId() == null ? UUID.randomUUID() : request.getId();

        if (request.getId() != null && messageRepository.existsById(id)) {
            throw new BusinessException(
                    BusinessError.MESSAGE_EXISTS,
                    ResourceUtil.createArgs(id));
        }

        StudentAgentMessage message = StudentAgentMessage.builder()
                .id(id)
                .conversationId(conversationId)
                .role(MessageRole.STUDENT)
                .content(request.getContent().trim())
                .build();

        try {
            message = messageRepository.saveAndFlush(message);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.CREATE_MESSAGE_FAILED);
        }

        return StudentAgentMessageMapper.toResponse(message);
    }

    @Transactional(readOnly = true)
    public StudentAgentMessageResponse getMessage(
            UUID conversationId,
            UUID messageId,
            UUID accountId) {
        conversationService.getOwnedConversation(conversationId, accountId);

        StudentAgentMessage message = messageRepository
                .findByIdAndConversationId(messageId, conversationId)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.MESSAGE_NOT_FOUND,
                        ResourceUtil.createArgs(messageId)));

        return StudentAgentMessageMapper.toResponse(message);
    }

    @Transactional(readOnly = true)
    public List<StudentAgentMessageResponse> getMessages(UUID conversationId, UUID accountId) {
        conversationService.getOwnedConversation(conversationId, accountId);

        return messageRepository
                .findAllByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(StudentAgentMessageMapper::toResponse)
                .toList();
    }
}
