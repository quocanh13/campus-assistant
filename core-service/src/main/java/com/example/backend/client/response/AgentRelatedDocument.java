package com.example.backend.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentRelatedDocument {
    private String documentId;
    private List<Integer> chunkIndexes;
}
