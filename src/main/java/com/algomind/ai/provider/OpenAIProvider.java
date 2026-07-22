package com.algomind.ai.provider;

import com.algomind.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenAIProvider extends AbstractOpenAICompatibleProvider {

    @Value("${openai.api.key:}")
    private String apiKey;

    public OpenAIProvider(PromptBuilder promptBuilder, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        super(promptBuilder, webClientBuilder, objectMapper);
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

    @Override
    protected String getApiKey() {
        return apiKey;
    }

    @Override
    protected String getApiUrl() {
        return "https://api.openai.com/v1/chat/completions";
    }

    @Override
    protected String getModelName() {
        return "gpt-4o-mini";
    }
}
