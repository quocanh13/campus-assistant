package com.example.backend.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.CreateAdminRequest;
import com.example.backend.dto.request.PatchAdminRequest;
import com.example.backend.dto.response.admin.AdminResponse;
import com.example.backend.entity.Admin;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.AdminMapper;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.AdminRepository;
import com.example.backend.utils.PageUtil;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AdminResponse createAdmin(CreateAdminRequest request) {
        if (adminRepository.existsById(request.getId())) {
            throw new BusinessException(
                    BusinessError.ADMIN_EXISTS,
                    ResourceUtil.createArgs(String.valueOf(request.getId())));
        }

        if (adminRepository.existsByAccountId(request.getAccountId())) {
            throw new BusinessException(
                    BusinessError.ADMIN_ACCOUNT_EXISTS,
                    ResourceUtil.createArgs(request.getAccountId()));
        }

        if (!accountRepository.existsById(request.getAccountId())) {
            throw new BusinessException(
                    BusinessError.ACCOUNT_NOT_FOUND,
                    ResourceUtil.createArgs(request.getAccountId()));
        }

        Admin admin = Admin.builder()
                .id(request.getId())
                .accountId(request.getAccountId())
                .name(request.getName().trim())
                .build();

        try {
            admin = adminRepository.saveAndFlush(admin);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.CREATE_ADMIN_FAILED);
        }

        return AdminMapper.toResponse(admin);
    }

    @Transactional(readOnly = true)
    public AdminResponse getAdmin(Integer id) {
        return AdminMapper.toResponse(getAdminEntity(id));
    }

    @Transactional(readOnly = true)
    public List<AdminResponse> getAdmins(int index, int limit) {
        return adminRepository.findAll(PageUtil.pageable(index, limit))
                .stream()
                .map(AdminMapper::toResponse)
                .toList();
    }

    @Transactional
    public AdminResponse patchAdmin(Integer id, PatchAdminRequest request) {
        Admin admin = getAdminEntity(id);

        if (request.getName() != null) {
            admin.setName(request.getName().trim());
        }

        try {
            admin = adminRepository.saveAndFlush(admin);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.UPDATE_ADMIN_FAILED);
        }

        return AdminMapper.toResponse(admin);
    }

    @Transactional
    public void deleteAdmin(Integer id) {
        Admin admin = getAdminEntity(id);

        try {
            adminRepository.delete(admin);
            adminRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(BusinessError.DELETE_ADMIN_FAILED);
        }
    }

    private Admin getAdminEntity(Integer id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BusinessError.ADMIN_NOT_FOUND,
                        ResourceUtil.createArgs(String.valueOf(id))));
    }
}
