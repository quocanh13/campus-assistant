package com.example.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QueryRequest {
    private final String query;
}
