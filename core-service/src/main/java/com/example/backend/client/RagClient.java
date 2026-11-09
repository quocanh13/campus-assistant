package com.example.backend.client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.backend.client.request.IngestionRequest;
import com.example.backend.client.request.QueryRequest;
import com.example.backend.client.request.UpdateDocumentRequest;
import com.example.backend.client.response.QueryResponse;
import com.example.backend.client.response.RagBaseResponse;
import com.example.backend.config.RagConfig;
import com.example.backend.entity.Document;
import com.example.backend.exception.rag.RagException;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RagClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient restClient;
    private final RagConfig ragConfig;

    public Void ingestDocument(
        Document document,
        int fromCohort,
        int toCohort,
        ArrayList<String> keywords,
        LocalDateTime validAt,
        LocalDateTime expiredAt
    ){
        IngestionRequest body = new IngestionRequest(
            document.getId(), document.getMimeType(), document.getName(),
            fromCohort, toCohort, keywords,
            validAt.toString(), 
            expiredAt == null ? null : expiredAt.toString()
        );
        try{
            RagBaseResponse<Void> response = restClient.post()
                    .uri(ragConfig.getRagProperties().getBaseUrl() + "/ingest")
                    .body(body).retrieve()
                    .body(new ParameterizedTypeReference<RagBaseResponse<Void>>() {});
            logger.info("POST " + ragConfig.getRagProperties().getBaseUrl() + "/ingest" + " -> OK");
            return response.getData();
        } catch (RestClientResponseException e) {
            RagBaseResponse<Void> resonseData = e.getResponseBodyAs(new ParameterizedTypeReference<RagBaseResponse<Void>>() {});
            throw new RagException(resonseData, e.getStatusCode());
        }
    }

    public QueryResponse query(String query){
        QueryRequest body = new QueryRequest(query);
        try{
            RagBaseResponse<QueryResponse> response = restClient.post()
                    .uri(ragConfig.getRagProperties().getBaseUrl() + "/query")
                    .body(body).retrieve()
                    .body(new ParameterizedTypeReference<RagBaseResponse<QueryResponse>>() {});
            if(response == null)
                throw new IllegalStateException("Query response is null");
            logger.info("POST " + ragConfig.getRagProperties().getBaseUrl() + "/query" + " -> OK");
            return response.getData();  
        } catch (RestClientResponseException e){
            RagBaseResponse<Void> resonseData = e.getResponseBodyAs(new ParameterizedTypeReference<RagBaseResponse<Void>>() {});
            throw new RagException(resonseData, e.getStatusCode());
        }
    }

    public void deleteDocument(UUID fileId){
        try{
            restClient.delete()
                .uri(ragConfig.getRagProperties().getBaseUrl() + "/documents/" + fileId.toString())
                .retrieve()
                .body(new ParameterizedTypeReference<RagBaseResponse<Void>>() {});
        } catch (RestClientResponseException e){
            RagBaseResponse<Void> responseData = e.getResponseBodyAs(new ParameterizedTypeReference<RagBaseResponse<Void>>() {});
            throw new RagException(responseData, e.getStatusCode());
        }
    }

    public void updateDocument(
        UUID documentId,
        String name,
        LocalDateTime validAt,
        LocalDateTime expiredAt
    ){
        UpdateDocumentRequest body = UpdateDocumentRequest.builder()
            .name(name)
            .validAt(validAt == null ? null : validAt.toString())
            .expiredAt(expiredAt == null ? null : expiredAt.toString())
            .build();
        try{
            restClient.patch()
                .uri(ragConfig.getRagProperties().getBaseUrl() + "/documents/" + documentId.toString())
                .body(body).retrieve()
                .body(new ParameterizedTypeReference<RagBaseResponse<Void>>() {});
            logger.info(
                "PATCH " + ragConfig.getRagProperties().getBaseUrl()
                + "/documents/" + documentId + " -> OK"
            );
        } catch (RestClientResponseException e){
            RagBaseResponse<Void> responseData = e.getResponseBodyAs(new ParameterizedTypeReference<RagBaseResponse<Void>>() {});
            throw new RagException(responseData, e.getStatusCode());
        }
    }
}
