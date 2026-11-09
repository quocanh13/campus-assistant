package com.example.backend.service;

import java.time.LocalDateTime;
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
import com.example.backend.config.DocumentConfig;
import com.example.backend.dto.response.document.GetDocumentContentResponse;
import com.example.backend.dto.response.document.UploadDocumentResponse;
import com.example.backend.entity.Document;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.repository.DocumentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentConfig documentConfig;
    private final DocumentRepository documentRepository;
    private final S3Service s3Service;
    private final RagClient ragClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private EntityManager entityManager;

    public UploadDocumentResponse uploadDocument(
        MultipartFile file,
        int fromCohort,
        int toCohort,
        ArrayList<String> keywords,
        LocalDateTime validAt,
        LocalDateTime expiredAt
    ){
        String contentType = file.getContentType();
        if(contentType == null || !documentConfig.getAllowedType().contains(contentType))
            throw new BusinessException(BusinessError.INVALID_DOCUMENT_TYPE);

        UUID fileId = UUID.randomUUID();
        String fileName = file.getOriginalFilename();
        if(fileName == null)
            throw new IllegalStateException("file.getOriginalFilename returns null");

        Document document = Document.builder()
            .id(fileId).size(file.getSize())
            .name(fileName).mimeType(contentType)
            .userId(UUID.randomUUID()).expriedAt(expiredAt)
            .validAt(validAt).build();

        if(document == null)
            throw new IllegalStateException("Document.builder return null");
        document = documentRepository.save(document);
       
        try {
            s3Service.uploadFile(fileId.toString(), file);
        }catch(Exception exception) {
            documentRepository.delete(document);
            throw exception;
        }

        try {
            ragClient.ingestDocument(
                document, fromCohort, toCohort,
                keywords, validAt, expiredAt
            );
            return new UploadDocumentResponse( fileId, document.getName() );
        } catch(Exception exception) {
            documentRepository.delete(document);
            // s3Service.deleteFile(document.getId().toString());
            throw exception;
        }
    }

    public List<Document> getDocumentList(int index, int limit){
        Pageable pageable = PageRequest.of(index, limit);
        List<Document> documents = documentRepository.findAllByOrderByUploadedAtAsc(pageable);
        return documents;
    }

    public List<Document> getDocumentList(List<UUID> ids){
        List<Document> documents = documentRepository.findAllById(ids);
        return documents;
    }


    public Document getDocumentById(UUID id){
        Optional<Document> optionalDocument = documentRepository.findById(id);
        Document document = optionalDocument.orElseThrow(()->{
            throw new BusinessException(BusinessError.DOCUMENT_NOT_FOUND);
        });
        return document;
    }

    public long getDocumentCount(){
        long count = documentRepository.count();
        return count;
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
        Optional<Document> optionalDocument = documentRepository.findById(fileId);
        Document document = optionalDocument.orElseThrow(()->{
            throw new BusinessException(BusinessError.DOCUMENT_NOT_FOUND);
        });
        
        byte[] content = s3Service.getFileContent(fileId.toString());
        GetDocumentContentResponse getDocumentContentResponse = new GetDocumentContentResponse(
            document.getName(),
            document.getMimeType(),
            content
        );
        return getDocumentContentResponse;
    }
}
