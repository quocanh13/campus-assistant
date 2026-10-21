package com.example.backend.client.response;

import java.util.List;
import java.util.UUID;

import com.example.backend.entity.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter 
public class QueryResponse {
    @JsonProperty ("processed_query")
    private final String processedQuery;

    private final String answer;
    private final String context;

    @JsonProperty ("related_document_ids")
    @JsonInclude (JsonInclude.Include.NON_NULL)
    private List<UUID> relatedDocumentIds;

    @JsonProperty ("related_documents")
    @JsonInclude (JsonInclude.Include.NON_NULL)
    private List<Document> relatedDocuments;
}
