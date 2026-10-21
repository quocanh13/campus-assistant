package com.example.backend.mapper;

import com.example.backend.dto.response.document.GetDocumentResponse;
import com.example.backend.entity.Document;

public class DocumentMapper {
    public static GetDocumentResponse toAdminGetDocument(Document document){
        GetDocumentResponse getDocumentResponse = GetDocumentResponse.builder()
            .id(document.getId()).name(document.getName()).fileSize(document.getFileSize())
            .mimeType(document.getMimeType()).userId(document.getUserId())
            .uploadedAt(document.getUploadedAt()).build();
        
        return getDocumentResponse;
    }

    public static GetDocumentResponse toStudentGetDocument(Document document) {
        GetDocumentResponse getDocumentResponse = GetDocumentResponse.builder()
            .id(document.getId()).name(document.getName()).fileSize(document.getFileSize())
            .mimeType(document.getMimeType()).build();
        
        return getDocumentResponse;
    }
}
