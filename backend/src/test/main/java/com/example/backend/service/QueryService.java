package com.example.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.backend.client.RagClient;
import com.example.backend.client.response.QueryResponse;
import com.example.backend.entity.Document;
import com.example.backend.repository.DocumentRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QueryService {
    private final RagClient ragClient;
    private final CurrentUserService currentUserService;
    private final DocumentRepository documentRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public QueryResponse query(String query, Boolean includeRelatedDocuments){
        logger.info(currentUserService.getUername() + " query");
        QueryResponse queryResponse = ragClient.query(query);
        if(includeRelatedDocuments){
            List<Document> relatedDocuments = documentRepository.findAllById(queryResponse.getRelatedDocumentIds());
            for(Document document : relatedDocuments){
                document.setUploadedAt(null);
                document.setUserId(null);
            }
            queryResponse.setRelatedDocumentIds(null);
            queryResponse.setRelatedDocuments(relatedDocuments);
        }
        return queryResponse;
    }
}
