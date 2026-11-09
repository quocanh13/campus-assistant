package com.example.backend.client.request;

import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IngestionRequest {
    @JsonProperty("document_id")
    private UUID id;

    @JsonProperty("document_type")
    private String type;

    @JsonProperty("document_name")
    private String name;

    @JsonProperty("from_cohort")
    private int fromCohort;

    @JsonProperty("to_cohort")
    private int toCohort;

    @JsonProperty("keywords")
    private ArrayList<String> keywords;

    @JsonProperty("valid_at")
    private String validAt;

    @JsonProperty("expired_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String expiredAt;
}
