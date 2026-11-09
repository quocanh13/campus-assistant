package com.example.backend.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchDocumentRequest {
    @Size(max = 255, message = "{validation.document.name.length}")
    @Pattern(regexp = ".*\\S.*", message = "{validation.document.name.required}")
    private String name;

    @Size(max = 2500, message = "{validation.document.description.length}")
    private String description;

    @JsonProperty("valid_at")
    private LocalDateTime validAt;

    @JsonProperty("expired_at")
    private LocalDateTime expiredAt;
}
