package com.algomind.ai.provider;

import com.algomind.ai.dto.AIRequest;
import com.algomind.ai.dto.AIResponse;
import com.algomind.ai.prompt.PromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GeminiProvider implements AIProvider {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final PromptBuilder promptBuilder;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiProvider(PromptBuilder promptBuilder, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.promptBuilder = promptBuilder;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public Mono<AIResponse> generateInsights(AIRequest request) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return Mono.error(new IllegalStateException("Gemini API key is not configured"));
        }

        String prompt = promptBuilder.buildPrompt(request);
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        String requestBody;
        try {
            requestBody = "{\"contents\": [{\"parts\": [{\"text\": " + objectMapper.writeValueAsString(prompt) + "}]}]}";
        } catch (Exception e) {
            return Mono.error(e);
        }

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        JsonNode root = objectMapper.readTree(responseBody);
                        String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                        
                        if (text.startsWith("```json")) {
                            text = text.substring(7);
                        }
                        if (text.endsWith("```")) {
                            text = text.substring(0, text.length() - 3);
                        }
                        
                        return objectMapper.readValue(text.trim(), AIResponse.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse Gemini response", e);
                    }
                });
    }
}
