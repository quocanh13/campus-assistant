package com.example.backend.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateStudentAgentConversationRequest {
    @Size(max = 200, message = "{validation.conversation.title.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.conversation.title.required}")
    private String title;

    // Optional: client may supply the conversation id, otherwise a UUID is generated.
    private UUID id;
}
