package com.example.backend.dto.response.message;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatWithAgentResponse {
    private StudentAgentMessageResponse studentMessage;
    private StudentAgentMessageResponse assistantMessage;
    private List<com.example.backend.client.response.AgentRelatedDocument> relatedDocument;
    private List<Map<String, Object>> toolData;
}
