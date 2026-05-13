package com.benedykt.budget_control.openAiApi.Service;

import com.benedykt.budget_control.openAiApi.dtos.OpenAiChatRequest;
import com.benedykt.budget_control.openAiApi.dtos.OpenAiChatResponse;

public interface OpenAiClassificationGateway {
    OpenAiChatResponse execute(OpenAiChatRequest request);
}
