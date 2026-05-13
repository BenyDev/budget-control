package com.benedykt.budget_control.openAiApi.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.SequencedCollection;

@Getter
@Setter
@NoArgsConstructor
public class OpenAiChatResponse {
    private List<Choice> choices;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Choice {
        private Message message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Message {
        private String content;
    }
}
