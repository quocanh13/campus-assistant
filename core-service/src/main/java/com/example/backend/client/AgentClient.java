package com.example.backend.client;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.backend.client.request.AgentChatRequest;
import com.example.backend.client.response.AgentBaseResponse;
import com.example.backend.client.response.AgentChatData;
import com.example.backend.exception.agent.AgentException;

@Component
public class AgentClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestClient agentRestClient;

    public AgentClient(@Qualifier("agentRestClient") RestClient agentRestClient) {
        this.agentRestClient = agentRestClient;
    }

    public AgentChatData chat(UUID conversationId, String message, Integer studentCode) {
        try {
            AgentChatRequest request = AgentChatRequest.builder()
                    .message(message)
                    .studentCode(studentCode)
                    .build();

            AgentBaseResponse<AgentChatData> response = agentRestClient.post()
                    .uri("/conversations/{conversationId}", conversationId)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<AgentBaseResponse<AgentChatData>>() {});

            if (response == null) {
                throw new AgentException("AGENT_EMPTY_RESPONSE", "Received empty response from agent service");
            }
            if (!"SUCCESS".equals(response.getCode())) {
                throw new AgentException(response.getCode(), response.getMessage() != null ? response.getMessage() : "Agent service returned error");
            }
            return response.getData();
        } catch (RestClientResponseException e) {
            logger.error("Agent service HTTP error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AgentException("AGENT_HTTP_ERROR", "Agent service error: " + e.getStatusText());
        } catch (Exception e) {
            if (e instanceof AgentException) {
                throw (AgentException) e;
            }
            logger.error("Failed to connect to agent service", e);
            throw new AgentException("AGENT_CONNECTION_ERROR", "Failed to connect to agent service: " + e.getMessage());
        }
    }
}
