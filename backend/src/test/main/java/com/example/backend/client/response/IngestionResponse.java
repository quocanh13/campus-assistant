package com.example.backend.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IngestionResponse {
    @JsonProperty("document_type")
    private String documentType;
    
    @JsonProperty("cohort")
    private int cohort;

    @JsonProperty("keyword")
    private String keyword;
}
