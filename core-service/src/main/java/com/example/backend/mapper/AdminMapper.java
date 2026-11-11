package com.example.backend.mapper;

import com.example.backend.dto.response.admin.AdminResponse;
import com.example.backend.entity.Admin;

public class AdminMapper {
    public static AdminResponse toResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .accountId(admin.getAccountId())
                .name(admin.getName())
                .createdAt(admin.getCreatedAt())
                .build();
    }
}
