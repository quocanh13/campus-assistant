package com.example.backend.client.request;

import java.util.List;
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
public class IngestConversationRequest {
    @JsonProperty("conversation_id")
    private UUID conversationId;

    private String title;

    private List<Message> messages;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;

        private String content;
    }
}
