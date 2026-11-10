package com.example.backend.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchStudentAgentConversationRequest {
    @Size(max = 200, message = "{validation.conversation.title.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.conversation.title.required}")
    private String title;
}
