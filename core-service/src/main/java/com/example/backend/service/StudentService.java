package com.example.backend.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateStudentRequest;
import com.example.backend.dto.request.PatchStudentRequest;
import com.example.backend.dto.response.student.StudentResponse;
import com.example.backend.entity.Account;
import com.example.backend.entity.AccountRole;
import com.example.backend.entity.Student;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.StudentMapper;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.MajorRepository;
import com.example.backend.repository.StudentRepository;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final AccountRepository accountRepository;
    private final MajorRepository majorRepository;

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        if (studentRepository.existsById(request.getId())) {
            throw new BusinessException(
                    BusinessError.STUDENT_EXISTS,
                    ResourceUtil.createArgs(String.valueOf(request.getId())));
        }

        if (studentRepository.existsByAccountId(request.getAccountId())) {
            throw new BusinessException(
                    BusinessError.STUDENT_ACCOUNT_EXISTS,
                    ResourceUtil.createArgs(request.getAccountId()));
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new BusinessException(
                        BusinessError.ACCOUNT_NOT_FOUND,
                        ResourceUtil.createArgs(request.getAccountId())));

        if (account.getRole() != AccountRole.STUDENT) {
            throw new BusinessException(
                    BusinessError.INVALID_STUDENT_ACCOUNT_ROLE,
                    ResourceUtil.createArgs(request.getAccountId()));
        }

        String majorCode = request.getMajor().trim();

        if (!majorRepository.existsById(majorCode)) {
            throw new BusinessException(
                    BusinessError.MAJOR_NOT_FOUND,
                    ResourceUtil.createArgs(majorCode));
        }

        Student student = Student.builder()
                .id(request.getId())
                .accountId(request.getAccountId())
                .name(request.getName().trim())
                .cohort(request.getCohort())
                .major(majorCode)
                .build();

        try {
            student = studentRepository.saveAndFlush(student);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.CREATE_STUDENT_FAILED);
        }

        return StudentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudent(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.STUDENT_NOT_FOUND,
                        ResourceUtil.createArgs(String.valueOf(id))));

        return StudentMapper.toResponse(student);
    }

    @Transactional
    public StudentResponse patchStudent(Integer id, PatchStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.STUDENT_NOT_FOUND,
                        ResourceUtil.createArgs(String.valueOf(id))));

        if (request.getName() != null) {
            student.setName(request.getName().trim());
        }

        if (request.getCohort() != null) {
            student.setCohort(request.getCohort());
        }

        if (request.getMajor() != null) {
            String majorCode = request.getMajor().trim();

            if (!majorRepository.existsById(majorCode)) {
                throw new BusinessException(
                        BusinessError.MAJOR_NOT_FOUND,
                        ResourceUtil.createArgs(majorCode));
            }

            student.setMajor(majorCode);
        }

        try {
            student = studentRepository.saveAndFlush(student);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.UPDATE_STUDENT_FAILED);
        }

        return StudentMapper.toResponse(student);
    }

    @Transactional
    public void deleteStudent(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.STUDENT_NOT_FOUND,
                        ResourceUtil.createArgs(String.valueOf(id))));

        try {
            studentRepository.delete(student);
            studentRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.DELETE_STUDENT_FAILED);
        }
    }
}
