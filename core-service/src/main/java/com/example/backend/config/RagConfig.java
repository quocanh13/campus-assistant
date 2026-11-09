package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.example.backend.properties.RagProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Configuration
@Getter
@AllArgsConstructor
public class RagConfig {
    private final RagProperties ragProperties;

    @Bean
    public RestClient ragRestClient(){
        return RestClient.builder().baseUrl(ragProperties.getBaseUrl()).build();
    }
}
