package com.example.backend.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateStudentAdminMessageRequest {
    private UUID id;

    @NotBlank(message = "{validation.message.content.required}")
    @Size(max = 65535, message = "{validation.message.content.length}")
    private String content;
}
