package com.example.backend.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.backend.client.request.IngestionRequest;
import com.example.backend.client.request.QueryRequest;
import com.example.backend.client.response.IngestionResponse;
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

    public IngestionResponse ingestDocument(Document document){
        IngestionRequest body = new IngestionRequest(
            document.getId(),
            document.getMimeType(),
            document.getName()
        );
        try{
            RagBaseResponse<IngestionResponse> response = restClient.post()
                    .uri(ragConfig.getRagProperties().getBaseUrl() + "/ingestion")
                    .body(body).retrieve()
                    .body(new ParameterizedTypeReference<RagBaseResponse<IngestionResponse>>() {});
            logger.info("POST " + ragConfig.getRagProperties().getBaseUrl() + "/ingestion" + " -> OK");
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
}
