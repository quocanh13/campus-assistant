package com.example.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateStudentAdminConversationRequest {
    @Size(max = 200, message = "{validation.conversation.title.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.conversation.title.required}")
    private String title;

    @NotBlank(message = "{validation.admin.id.required}")
    @JsonProperty("admin_id")
    private String adminId;

    private java.util.UUID id;
}
