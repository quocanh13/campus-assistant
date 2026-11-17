package com.example.backend.dto.response.message;

import com.example.backend.entity.StudentAdminMessageRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Dùng cho API GET messages?onlyContent=true: chỉ trả về role + content.
 */
@Getter
@AllArgsConstructor
public class StudentAdminMessageContentResponse {
    private StudentAdminMessageRole role;
    private String content;
}
