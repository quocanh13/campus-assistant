package com.example.backend.client.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentChatData {
    private String answer;
    private List<AgentRelatedDocument> relatedDocument;
    private List<Map<String, Object>> toolData;
}
