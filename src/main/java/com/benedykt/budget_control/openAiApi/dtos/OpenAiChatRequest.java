package com.benedykt.budget_control.openAiApi.dtos;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenAiChatRequest {

    private String model;
    private List<Message> messages;
    private Map<String, Object> response_format;


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
