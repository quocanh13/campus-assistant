package com.example.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.client.RagClient;
import com.example.backend.client.response.IngestionResponse;
import com.example.backend.config.DocumentConfig;
import com.example.backend.dto.response.document.GetDocumentContentResponse;
import com.example.backend.dto.response.document.GetDocumentResponse;
import com.example.backend.dto.response.document.UploadDocumentResponse;
import com.example.backend.entity.Document;
import com.example.backend.entity.Role;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.mapper.DocumentMapper;
import com.example.backend.repository.DocumentRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentConfig documentConfig;
    private final DocumentRepository documentRepository;
    private final CurrentUserService currentUserService;
    private final S3Service s3Service;
    private final RagClient ragClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public UploadDocumentResponse uploadDocument(MultipartFile file){
        if(currentUserService.getRole() != Role.ADMIN){
            throw new BusinessException(BusinessError.UPLOAD_DOCUMENT_DENIED);
        }
        UUID userId = currentUserService.getId();
        String contentType = file.getContentType();
        if(contentType == null || !documentConfig.getAllowedType().contains(contentType))
            throw new BusinessException(BusinessError.INVALID_DOCUMENT_TYPE);

        UUID fileId = UUID.randomUUID();
        String fileName = file.getOriginalFilename();
        if(fileName == null)
            throw new IllegalStateException("file.getOriginalFilename returns null");

        Document document = Document.builder()
            .id(fileId).fileSize(file.getSize())
            .name(fileName).mimeType(contentType)
            .userId(userId).build();

        if(document == null)
            throw new IllegalStateException("Document.builder return null");
        documentRepository.save(document);
        try {
            s3Service.uploadFile(fileId.toString(), file);
        }catch(Exception exception) {
            documentRepository.delete(document);
            throw exception;
        }

        try {
            IngestionResponse metadata = ragClient.ingestDocument(document);
            return new UploadDocumentResponse(DocumentMapper.toAdminGetDocument(document), metadata);
        } catch(Exception exception) {
            documentRepository.delete(document);
            s3Service.deleteFile(document.getId().toString());
            throw exception;
        }
    }

    public List<GetDocumentResponse> getDocumentList(int index, int limit){
        Pageable pageable = PageRequest.of(index, limit);
        List<Document> documents = documentRepository.findAllByOrderByUploadedAtAsc(pageable);
        return mapDocumentList(documents);
    }

    public List<GetDocumentResponse> getDocumentList(List<UUID> ids){
        List<Document> documents = documentRepository.findAllById(ids);
        return mapDocumentList(documents);
    }

    private List<GetDocumentResponse> mapDocumentList(List<Document> documents){
        Role role = currentUserService.getRole();
        ArrayList<GetDocumentResponse> getDocumentResponses = new ArrayList<>();
        if(role == Role.ADMIN)
            for(Document document : documents)
                getDocumentResponses.add(DocumentMapper.toAdminGetDocument(document));
        else
            for(Document document : documents)
                getDocumentResponses.add(DocumentMapper.toStudentGetDocument(document));
        
        return getDocumentResponses;
    }

    public GetDocumentResponse getDocumentById(UUID id){
        Optional<Document> optionalDocument = documentRepository.findById(id);
        Document document = optionalDocument.orElseThrow(()->{
            throw new BusinessException(BusinessError.DOCUMENT_NOT_FOUND);
        });
        Role role = currentUserService.getRole();
        if(role == Role.ADMIN)
            return DocumentMapper.toAdminGetDocument(document);
        else
            return DocumentMapper.toStudentGetDocument(document);
    }

    public long getDocumentCount(){
        if(currentUserService.getRole() != Role.ADMIN){
            throw new BusinessException(BusinessError.ACCESS_DENIED);
        }
        return documentRepository.count();
    }

    public Document deleteDocument(UUID id){
        Optional<Document> optionalDocument = documentRepository.findById(id);
        Document document = optionalDocument.orElseThrow(()->{
            throw new BusinessException(BusinessError.DOCUMENT_NOT_FOUND);
        });

        try{
            documentRepository.delete(document);
            s3Service.deleteFile(id.toString());
            ragClient.deleteDocument(id);
        } catch (Exception exception){
            logger.info(exception.getMessage());
            throw new BusinessException(BusinessError.DELETE_DOCUMENT_FAILED);
        }

        return document;
    }   

    public GetDocumentContentResponse getDocumentContents(@NonNull UUID fileId){
        UUID userId = currentUserService.getId();
        Optional<Document> optionalDocument = documentRepository.findById(fileId);
        Document document = optionalDocument.orElseThrow(()->{
            throw new BusinessException(BusinessError.DOCUMENT_NOT_FOUND);
        });
        if(!document.getUserId().equals(userId))
            throw new BusinessException(BusinessError.DOCUMENT_ACCESS_DENIED);
        
        byte[] content = s3Service.getFileContent(fileId.toString());
        GetDocumentContentResponse getDocumentContentResponse = new GetDocumentContentResponse(
            document.getName(),
            document.getMimeType(),
            content
        );
        return getDocumentContentResponse;
    }


}
