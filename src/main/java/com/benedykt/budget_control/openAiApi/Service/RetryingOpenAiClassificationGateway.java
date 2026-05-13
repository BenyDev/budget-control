package com.benedykt.budget_control.openAiApi.Service;

import com.benedykt.budget_control.openAiApi.OpenAiClient;
import com.benedykt.budget_control.openAiApi.dtos.OpenAiChatRequest;
import com.benedykt.budget_control.openAiApi.dtos.OpenAiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryingOpenAiClassificationGateway implements OpenAiClassificationGateway{

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY_MS = 1000L;

    private final OpenAiClient openAiClient;


    @Override
    public OpenAiChatResponse execute(OpenAiChatRequest request) {
        long delay = INITIAL_DELAY_MS;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try{

            log.info("Calling OpenAi. Attempt {}/{}", attempt, MAX_RETRIES);

            OpenAiChatResponse response = openAiClient.createChatCompletion(request);

            if(response == null || response.getChoices() == null || response.getChoices().isEmpty()){
                throw new RuntimeException("OpenAI response is null or empty");
            }

            log.info("OpenAi call succeeded an attempt {}/{}", attempt, MAX_RETRIES);

            return response;

            }catch(Exception ex){
                log.warn("OpenAi call failed on attempt {}/{}. Error={}", attempt, MAX_RETRIES,ex.getMessage());
                if(attempt == MAX_RETRIES){
                    log.error("OpenAI call failed after {} attempts", MAX_RETRIES,ex);
                    throw new RuntimeException("OpenAI call failed after retries",ex);
                }

                sleep(delay);
                delay = delay * 2;

            }
        }
        throw  new RuntimeException("Unexpected retry flow error");
    }
    private void sleep(long delayMs){
        try{
            Thread.sleep(delayMs);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted during retry backoff",e);
        }
    }
}

