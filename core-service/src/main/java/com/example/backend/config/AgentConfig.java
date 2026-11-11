package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.example.backend.properties.AgentProperties;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class AgentConfig {
    private final AgentProperties agentProperties;

    @Bean
    public RestClient agentRestClient() {
        return RestClient.builder()
                .baseUrl(agentProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
