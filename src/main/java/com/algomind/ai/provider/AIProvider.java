package com.algomind.ai.provider;

import com.algomind.ai.dto.AIRequest;
import com.algomind.ai.dto.AIResponse;
import reactor.core.publisher.Mono;

public interface AIProvider {
    /**
     * The unique identifier for this provider (e.g., "gemini", "openai")
     */
    String getProviderName();

    /**
     * Generates all required educational insights (hints, errors, complexity, etc.) 
     * in a single API call for maximum efficiency.
     */
    Mono<AIResponse> generateInsights(AIRequest request);
}
