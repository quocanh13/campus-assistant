package com.example.backend.dto.response.document;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder 
@AllArgsConstructor 
@Getter  
public class GetDocumentResponse{
    private UUID id;

    @JsonProperty("name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonProperty("mime_type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mimeType;

    @JsonProperty("file_size")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long fileSize;

    @JsonProperty("user_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID userId;

    @JsonProperty("uploaded_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant uploadedAt;
}