package com.example.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.response.BaseResponse;
import com.example.backend.dto.response.document.GetDocumentContentResponse;
import com.example.backend.dto.response.document.GetDocumentResponse;
import com.example.backend.dto.response.document.UploadDocumentResponse;
import com.example.backend.entity.Document;
import com.example.backend.service.DocumentService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping(path = "/documents")
@AllArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<UploadDocumentResponse>> uploadDocument(
        @RequestPart("file") MultipartFile file
    ){
        UploadDocumentResponse uploadDocumentResponse = documentService.uploadDocument(file);
        BaseResponse<UploadDocumentResponse> responseData = BaseResponse.<UploadDocumentResponse>success(
            "Upload document sucessfully", 
            uploadDocumentResponse
        );
        return ResponseEntity.ok().body(responseData);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<GetDocumentResponse>> getDocumentById(@PathVariable  UUID id) {
        GetDocumentResponse document = documentService.getDocumentById(id);
        BaseResponse<GetDocumentResponse> response = BaseResponse.success("Get document successfully", document);
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/count")
    public ResponseEntity<BaseResponse<Long>> getDocumentCount(
    ){
        long count = documentService.getDocumentCount();
        BaseResponse<Long> responseData = BaseResponse.success("Get document count successfully", count);
        return ResponseEntity.ok().body(responseData);  
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<List<GetDocumentResponse>>> getDocumentList(
        @RequestParam(defaultValue = "0") int index,
        @RequestParam(defaultValue = "10") int limit
    ){
        List<GetDocumentResponse> documents = documentService.getDocumentList(index, limit);
        BaseResponse<List<GetDocumentResponse>> responseData = BaseResponse.success("Get document list sucessfully", documents);
        return ResponseEntity.ok().body(responseData);  
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Document>> deleteDocument(@PathVariable UUID id){
        Document document = documentService.deleteDocument(id);
        BaseResponse<Document> responseData = BaseResponse.success("Delete document sucessfully", document);
        return ResponseEntity.ok().body(responseData);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID fileId) {
        if(fileId == null)
            throw new IllegalStateException("fileId is null");
        GetDocumentContentResponse file = documentService.getDocumentContents(fileId);
        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .body(file.getContent());
    }

    @GetMapping("/{fileId}/view")
    public ResponseEntity<byte[]> viewFile(@PathVariable UUID fileId) {
        if(fileId == null)
            throw new IllegalStateException("fileId is null");
        GetDocumentContentResponse file = documentService.getDocumentContents(fileId);
        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + file.getFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .body(file.getContent());
    }

}
