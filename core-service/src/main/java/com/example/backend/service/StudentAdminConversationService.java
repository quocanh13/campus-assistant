package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateStudentAdminConversationRequest;
import com.example.backend.dto.request.PatchStudentAdminConversationRequest;
import com.example.backend.dto.response.conversation.StudentAdminConversationResponse;
import com.example.backend.entity.Admin;
import com.example.backend.entity.ConversationState;
import com.example.backend.entity.Student;
import com.example.backend.entity.StudentAdminConversation;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.StudentAdminConversationMapper;
import com.example.backend.repository.AdminRepository;
import com.example.backend.repository.StudentAdminConversationRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentAdminConversationService {
    private static final String DEFAULT_TITLE = "New question";

    private final StudentAdminConversationRepository conversationRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public StudentAdminConversationResponse createConversation(
            UUID accountId,
            com.example.backend.security.JwtData jwtData,
            CreateStudentAdminConversationRequest request) {
        if (jwtData.getRole() != com.example.backend.entity.AccountRole.STUDENT) {
            throw new BusinessException(BusinessError.ACCESS_DENIED);
        }

        Student student = getStudentByAccountId(accountId);

        String rawAdminId = request.getAdminId();
        Admin admin;
        try {
            java.util.UUID uuid = java.util.UUID.fromString(rawAdminId.trim());
            admin = adminRepository.findByAccountId(uuid)
                    .orElseThrow(() -> new BusinessException(BusinessError.ADMIN_NOT_FOUND, new Object[]{uuid}));
        } catch (IllegalArgumentException ex) {
            try {
                int intId = Integer.parseInt(rawAdminId.trim());
                admin = adminRepository.findById(intId)
                        .orElseThrow(() -> new BusinessException(BusinessError.ADMIN_NOT_FOUND, new Object[]{intId}));
            } catch (NumberFormatException nfe) {
                throw new BusinessException(BusinessError.ADMIN_NOT_FOUND, new Object[]{rawAdminId});
            }
        }

        UUID id = request.getId() == null ? UUID.randomUUID() : request.getId();

        if (request.getId() != null && conversationRepository.existsById(id)) {
            throw new BusinessException(BusinessError.CONVERSATION_EXISTS, ResourceUtil.createArgs(id));
        }

        String title = request.getTitle() == null ? DEFAULT_TITLE : request.getTitle().trim();

        StudentAdminConversation conversation = StudentAdminConversation.builder()
                .id(id)
                .title(title)
                .studentId(student.getId())
                .adminId(admin.getId())
                .state(ConversationState.PENDING)
                .build();

        try {
            conversation = conversationRepository.saveAndFlush(conversation);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.CREATE_CONVERSATION_FAILED);
        }

        return StudentAdminConversationMapper.toResponse(conversation);
    }

    @Transactional(readOnly = true)
    public StudentAdminConversationResponse getConversation(UUID id, UUID accountId) {
        StudentAdminConversation conversation = getParticipantConversation(id, accountId);
        return StudentAdminConversationMapper.toResponse(conversation);
    }

    @Transactional(readOnly = true)
    public List<StudentAdminConversationResponse> getMyConversations(UUID accountId) {
        java.util.Optional<Student> studentOpt = studentRepository.findByAccountId(accountId);
        if (studentOpt.isPresent()) {
            return conversationRepository
                    .findAllByStudentIdOrderByUpdatedAtDesc(studentOpt.get().getId())
                    .stream()
                    .map(StudentAdminConversationMapper::toResponse)
                    .toList();
        }
        java.util.Optional<Admin> adminOpt = adminRepository.findByAccountId(accountId);
        if (adminOpt.isPresent()) {
            return conversationRepository
                    .findAllByAdminIdOrderByUpdatedAtDesc(adminOpt.get().getId())
                    .stream()
                    .map(StudentAdminConversationMapper::toResponse)
                    .toList();
        }
        throw new BusinessException(BusinessError.ACCOUNT_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    public List<StudentAdminConversationResponse> getAllConversations(UUID accountId) {
        boolean isStudent = studentRepository.findByAccountId(accountId).isPresent();
        boolean isAdmin = adminRepository.findByAccountId(accountId).isPresent();

        if (!isStudent && !isAdmin) {
            throw new BusinessException(BusinessError.ACCESS_DENIED);
        }

        return conversationRepository
                .findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(StudentAdminConversationMapper::toResponse)
                .toList();
    }

    @Transactional
    public StudentAdminConversationResponse patchConversation(
            UUID id,
            UUID accountId,
            PatchStudentAdminConversationRequest request) {
        if (studentRepository.findByAccountId(accountId).isPresent()) {
            throw new BusinessException(BusinessError.ACCESS_DENIED);
        }
        Admin admin = getAdminByAccountId(accountId);

        StudentAdminConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.CONVERSATION_NOT_FOUND, ResourceUtil.createArgs(id)));

        if (!conversation.getAdminId().equals(admin.getId())) {
            throw new BusinessException(BusinessError.CONVERSATION_ACCESS_DENIED, ResourceUtil.createArgs(id));
        }

        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle().trim());
        }
        if (request.getState() != null) {
            conversation.setState(request.getState());
        }

        try {
            conversation = conversationRepository.saveAndFlush(conversation);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.UPDATE_CONVERSATION_FAILED);
        }

        return StudentAdminConversationMapper.toResponse(conversation);
    }

    @Transactional
    public void deleteConversation(UUID id, UUID accountId) {
        if (studentRepository.findByAccountId(accountId).isPresent()) {
            throw new BusinessException(BusinessError.ACCESS_DENIED);
        }
        Admin admin = getAdminByAccountId(accountId);

        StudentAdminConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.CONVERSATION_NOT_FOUND, ResourceUtil.createArgs(id)));

        if (!conversation.getAdminId().equals(admin.getId())) {
            throw new BusinessException(BusinessError.CONVERSATION_ACCESS_DENIED, ResourceUtil.createArgs(id));
        }

        try {
            conversationRepository.delete(conversation);
            conversationRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.DELETE_CONVERSATION_FAILED);
        }
    }

    public StudentAdminConversation getParticipantConversation(UUID id, UUID accountId) {
        StudentAdminConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.CONVERSATION_NOT_FOUND, ResourceUtil.createArgs(id)));

        java.util.Optional<Student> studentOpt = studentRepository.findByAccountId(accountId);
        if (studentOpt.isPresent() && conversation.getStudentId().equals(studentOpt.get().getId())) {
            return conversation;
        }
        java.util.Optional<Admin> adminOpt = adminRepository.findByAccountId(accountId);
        if (adminOpt.isPresent() && conversation.getAdminId().equals(adminOpt.get().getId())) {
            return conversation;
        }
        throw new BusinessException(BusinessError.CONVERSATION_ACCESS_DENIED, ResourceUtil.createArgs(id));
    }

    public StudentAdminConversation getOwnedConversationByStudent(UUID id, UUID accountId) {
        Student student = getStudentByAccountId(accountId);
        StudentAdminConversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.CONVERSATION_NOT_FOUND, ResourceUtil.createArgs(id)));
        if (!conversation.getStudentId().equals(student.getId())) {
            throw new BusinessException(BusinessError.CONVERSATION_ACCESS_DENIED, ResourceUtil.createArgs(id));
        }
        return conversation;
    }

    private Student getStudentByAccountId(UUID accountId) {
        return studentRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(BusinessError.STUDENT_PROFILE_NOT_FOUND, ResourceUtil.createArgs(accountId)));
    }

    private Admin getAdminByAccountId(UUID accountId) {
        return adminRepository.findByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(BusinessError.ADMIN_PROFILE_NOT_FOUND, ResourceUtil.createArgs(accountId)));
    }
}
