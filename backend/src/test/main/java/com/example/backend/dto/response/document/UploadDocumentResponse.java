package com.example.backend.dto.response.document;

import com.example.backend.client.response.IngestionResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadDocumentResponse {
    private GetDocumentResponse document;
    private IngestionResponse metadata;
}
