package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateStudentAdminMessageRequest;
import com.example.backend.dto.request.PatchStudentAdminMessageRequest;
import com.example.backend.dto.response.message.StudentAdminMessageResponse;
import com.example.backend.entity.Student;
import com.example.backend.entity.StudentAdminMessage;
import com.example.backend.entity.StudentAdminMessageRole;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.StudentAdminMessageMapper;
import com.example.backend.repository.AdminRepository;
import com.example.backend.repository.StudentAdminMessageRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentAdminMessageService {
    private final StudentAdminMessageRepository messageRepository;
    private final StudentAdminConversationService conversationService;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public StudentAdminMessageResponse createMessage(
            UUID conversationId,
            UUID accountId,
            CreateStudentAdminMessageRequest request) {

        // participant check
        conversationService.getParticipantConversation(conversationId, accountId);

        StudentAdminMessageRole role = resolveRole(accountId);

        UUID id = request.getId() == null ? UUID.randomUUID() : request.getId();

        if (request.getId() != null && messageRepository.existsById(id)) {
            throw new BusinessException(BusinessError.MESSAGE_EXISTS, ResourceUtil.createArgs(id));
        }

        StudentAdminMessage message = StudentAdminMessage.builder()
                .id(id)
                .conversationId(conversationId)
                .role(role)
                .content(request.getContent().trim())
                .build();

        try {
            message = messageRepository.saveAndFlush(message);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.CREATE_MESSAGE_FAILED);
        }

        return StudentAdminMessageMapper.toResponse(message);
    }

    @Transactional(readOnly = true)
    public StudentAdminMessageResponse getMessage(
            UUID conversationId,
            UUID messageId,
            UUID accountId) {
        conversationService.getParticipantConversation(conversationId, accountId);

        StudentAdminMessage message = messageRepository
                .findByIdAndConversationId(messageId, conversationId)
                .orElseThrow(() -> new BusinessException(BusinessError.MESSAGE_NOT_FOUND, ResourceUtil.createArgs(messageId)));

        return StudentAdminMessageMapper.toResponse(message);
    }

    @Transactional(readOnly = true)
    public List<StudentAdminMessageResponse> getMessages(
            UUID conversationId,
            UUID accountId,
            int index,
            int limit) {
        conversationService.getParticipantConversation(conversationId, accountId);

        List<StudentAdminMessage> all = messageRepository
                .findAllByConversationIdOrderByCreatedAtAsc(conversationId);

        int from = Math.max(0, index);
        int to = (limit <= 0) ? all.size() : Math.min(all.size(), from + limit);
        if (from > all.size()) from = all.size();

        return all.subList(from, to).stream()
                .map(StudentAdminMessageMapper::toResponse)
                .toList();
    }

    @Transactional
    public StudentAdminMessageResponse patchMessage(
            UUID conversationId,
            UUID messageId,
            UUID accountId,
            PatchStudentAdminMessageRequest request) {
        if (studentRepository.findByAccountId(accountId).isPresent()) {
            throw new BusinessException(BusinessError.ACCESS_DENIED);
        }
        com.example.backend.entity.Admin admin = adminRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(BusinessError.ADMIN_PROFILE_NOT_FOUND, ResourceUtil.createArgs(accountId)));

        var conversation = conversationService.getParticipantConversation(conversationId, accountId);
        if (!conversation.getAdminId().equals(admin.getId())) {
            throw new BusinessException(BusinessError.ACCESS_DENIED);
        }

        StudentAdminMessage message = messageRepository
                .findByIdAndConversationId(messageId, conversationId)
                .orElseThrow(() -> new BusinessException(BusinessError.MESSAGE_NOT_FOUND, ResourceUtil.createArgs(messageId)));

        message.setContent(request.getContent().trim());

        try {
            message = messageRepository.saveAndFlush(message);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.UPDATE_MESSAGE_FAILED);
        }

        return StudentAdminMessageMapper.toResponse(message);
    }

    @Transactional
    public void deleteMessage(
            UUID conversationId,
            UUID messageId,
            UUID accountId) {
        conversationService.getParticipantConversation(conversationId, accountId);

        StudentAdminMessage message = messageRepository
                .findByIdAndConversationId(messageId, conversationId)
                .orElseThrow(() -> new BusinessException(BusinessError.MESSAGE_NOT_FOUND, ResourceUtil.createArgs(messageId)));

        try {
            messageRepository.delete(message);
            messageRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.DELETE_MESSAGE_FAILED);
        }
    }

    private StudentAdminMessageRole resolveRole(UUID accountId) {
        java.util.Optional<Student> studentOpt = studentRepository.findByAccountId(accountId);
        if (studentOpt.isPresent()) {
            return StudentAdminMessageRole.STUDENT;
        }
        java.util.Optional<com.example.backend.entity.Admin> adminOpt = adminRepository.findByAccountId(accountId);
        if (adminOpt.isPresent()) {
            return StudentAdminMessageRole.ADMIN;
        }
        throw new BusinessException(BusinessError.ACCOUNT_NOT_FOUND);
    }
}
