package com.example.backend.dto.response.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetDocumentContentResponse{
    private final String fileName;
    private final String fileType;
    private final byte[] content;
}
