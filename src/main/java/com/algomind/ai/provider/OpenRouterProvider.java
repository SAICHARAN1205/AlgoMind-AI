package com.algomind.ai.provider;

import com.algomind.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenRouterProvider extends AbstractOpenAICompatibleProvider {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    public OpenRouterProvider(PromptBuilder promptBuilder, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        super(promptBuilder, webClientBuilder, objectMapper);
    }

    @Override
    public String getProviderName() {
        return "openrouter";
    }

    @Override
    protected String getApiKey() {
        return apiKey;
    }

    @Override
    protected String getApiUrl() {
        return "https://openrouter.ai/api/v1/chat/completions";
    }

    @Override
    protected String getModelName() {
        return "anthropic/claude-3-haiku"; // Defaulting to a fast, cheap model
    }
}
