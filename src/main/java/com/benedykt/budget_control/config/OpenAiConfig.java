package com.benedykt.budget_control.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration

@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory()
                                {{
                                    setConnectTimeout(5000);
                                    setReadTimeout(15000);
                                }}
                )
                .build();
    }
}
