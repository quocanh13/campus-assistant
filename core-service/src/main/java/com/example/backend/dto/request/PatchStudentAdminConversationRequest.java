package com.example.backend.dto.request;

import com.example.backend.entity.ConversationState;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchStudentAdminConversationRequest {
    @Size(max = 200, message = "{validation.conversation.title.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.conversation.title.required}")
    private String title;

    private ConversationState state;
}
