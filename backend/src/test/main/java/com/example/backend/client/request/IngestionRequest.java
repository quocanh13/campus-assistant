package com.example.backend.client.request;

import java.util.UUID;

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
    @JsonProperty("file_id")
    private UUID id;

    @JsonProperty("file_type")
    private String type;

    @JsonProperty("file_name")
    private String name;
}
