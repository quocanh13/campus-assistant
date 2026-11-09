package com.example.backend.client.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateDocumentRequest {
    @JsonProperty("document_name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonProperty("valid_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String validAt;

    @JsonProperty("expired_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String expiredAt;
}
