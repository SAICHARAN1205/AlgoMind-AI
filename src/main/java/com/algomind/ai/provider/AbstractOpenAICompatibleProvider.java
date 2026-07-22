package com.algomind.ai.provider;

import com.algomind.ai.dto.AIRequest;
import com.algomind.ai.dto.AIResponse;
import com.algomind.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public abstract class AbstractOpenAICompatibleProvider implements AIProvider {

    protected final PromptBuilder promptBuilder;
    protected final WebClient webClient;
    protected final ObjectMapper objectMapper;

    public AbstractOpenAICompatibleProvider(PromptBuilder promptBuilder, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.promptBuilder = promptBuilder;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    protected abstract String getApiKey();
    protected abstract String getApiUrl();
    protected abstract String getModelName();

    @Override
    public Mono<AIResponse> generateInsights(AIRequest request) {
        if (getApiKey() == null || getApiKey().trim().isEmpty()) {
            return Mono.error(new IllegalStateException(getProviderName() + " API key is not configured"));
        }

        String prompt = promptBuilder.buildPrompt(request);

        Map<String, Object> requestBodyMap = Map.of(
                "model", getModelName(),
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestBodyMap);
        } catch (Exception e) {
            return Mono.error(e);
        }

        return webClient.post()
                .uri(getApiUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        JsonNode root = objectMapper.readTree(responseBody);
                        String text = root.path("choices").get(0).path("message").path("content").asText();
                        
                        if (text.startsWith("```json")) {
                            text = text.substring(7);
                        }
                        if (text.endsWith("```")) {
                            text = text.substring(0, text.length() - 3);
                        }
                        
                        return objectMapper.readValue(text.trim(), AIResponse.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse " + getProviderName() + " response", e);
                    }
                });
    }
}
