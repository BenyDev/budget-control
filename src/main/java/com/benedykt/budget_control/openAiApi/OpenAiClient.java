package com.benedykt.budget_control.openAiApi;

import com.benedykt.budget_control.config.OpenAiProperties;
import com.benedykt.budget_control.openAiApi.dtos.OpenAiChatRequest;
import com.benedykt.budget_control.openAiApi.dtos.OpenAiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiClient {

    private final RestClient restClient;
    private final OpenAiProperties properties;

    public OpenAiChatResponse createChatCompletion(OpenAiChatRequest request) {
        try{
            return restClient.post()
                    .uri(properties.getUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .body(OpenAiChatResponse.class);
        }catch (Exception e){
            log.error("OpenAI request failed", e);
            throw new RuntimeException("OpenAI request failed", e);
        }
    }
}
