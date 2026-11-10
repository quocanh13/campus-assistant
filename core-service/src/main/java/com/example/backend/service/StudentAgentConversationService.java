package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateStudentAgentConversationRequest;
import com.example.backend.dto.request.PatchStudentAgentConversationRequest;
import com.example.backend.dto.response.conversation.StudentAgentConversationResponse;
import com.example.backend.entity.Student;
import com.example.backend.entity.StudentAgentConversation;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.StudentAgentConversationMapper;
import com.example.backend.repository.StudentAgentConversationRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentAgentConversationService {
    private static final String DEFAULT_TITLE = "New conversation";

    private final StudentAgentConversationRepository conversationRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public StudentAgentConversationResponse createConversation(
            UUID accountId,
            CreateStudentAgentConversationRequest request) {
        Student student = getStudentByAccountId(accountId);

        UUID id = request.getId() == null ? UUID.randomUUID() : request.getId();

        if (request.getId() != null && conversationRepository.existsById(id)) {
            throw new BusinessException(
                    BusinessError.CONVERSATION_EXISTS,
                    ResourceUtil.createArgs(id));
        }

        String title = request.getTitle() == null
                ? DEFAULT_TITLE
                : request.getTitle().trim();

        StudentAgentConversation conversation = StudentAgentConversation.builder()
                .id(id)
                .title(title)
                .studentId(student.getId())
                .build();

        try {
            conversation = conversationRepository.saveAndFlush(conversation);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.CREATE_CONVERSATION_FAILED);
        }

        return StudentAgentConversationMapper.toResponse(conversation);
    }

    @Transactional(readOnly = true)
    public StudentAgentConversationResponse getConversation(UUID id, UUID accountId) {
        StudentAgentConversation conversation = getOwnedConversation(id, accountId);

        return StudentAgentConversationMapper.toResponse(conversation);
    }

    @Transactional(readOnly = true)
    public List<StudentAgentConversationResponse> getConversations(UUID accountId) {
        Student student = getStudentByAccountId(accountId);

        return conversationRepository
                .findAllByStudentIdOrderByUpdatedAtDesc(student.getId())
                .stream()
                .map(StudentAgentConversationMapper::toResponse)
                .toList();
    }

    @Transactional
    public StudentAgentConversationResponse patchConversation(
            UUID id,
            UUID accountId,
            PatchStudentAgentConversationRequest request) {
        StudentAgentConversation conversation = getOwnedConversation(id, accountId);

        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle().trim());
        }

        try {
            conversation = conversationRepository.saveAndFlush(conversation);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.UPDATE_CONVERSATION_FAILED);
        }

        return StudentAgentConversationMapper.toResponse(conversation);
    }

    @Transactional
    public void deleteConversation(UUID id, UUID accountId) {
        StudentAgentConversation conversation = getOwnedConversation(id, accountId);

        try {
            conversationRepository.delete(conversation);
            conversationRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.DELETE_CONVERSATION_FAILED);
        }
    }

    private Student getStudentByAccountId(UUID accountId) {
        return studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.STUDENT_PROFILE_NOT_FOUND,
                        ResourceUtil.createArgs(accountId)));
    }

    public StudentAgentConversation getOwnedConversation(UUID id, UUID accountId) {
        Student student = getStudentByAccountId(accountId);

        StudentAgentConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.CONVERSATION_NOT_FOUND,
                        ResourceUtil.createArgs(id)));

        if (!conversation.getStudentId().equals(student.getId())) {
            throw new BusinessException(
                    BusinessError.CONVERSATION_ACCESS_DENIED,
                    ResourceUtil.createArgs(id));
        }

        return conversation;
    }
}
