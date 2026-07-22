package com.algomind.ai.service;

import com.algomind.ai.dto.AIRequest;
import com.algomind.ai.dto.AIResponse;
import com.algomind.ai.factory.AIProviderFactory;
import com.algomind.ai.provider.AIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AIMentorService {

    private final AIProviderFactory providerFactory;

    @Value("${ai.enabled:true}")
    private boolean aiEnabled;

    public AIMentorService(AIProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    public Mono<AIResponse> getInsights(AIRequest request) {
        if (!aiEnabled) {
            System.out.println("AI is disabled via configuration. Using static fallback responses.");
            return Mono.just(getFallbackResponse(request));
        }

        AIProvider primaryProvider = providerFactory.getActiveProvider();
        System.out.println("Calling " + primaryProvider.getProviderName() + " API...");
        
        return primaryProvider.generateInsights(request)
                .doOnNext(response -> System.out.println(primaryProvider.getProviderName() + " response received"))
                .onErrorResume(e -> {
                    System.err.println("Primary AI Provider (" + primaryProvider.getProviderName() + ") failed: " + e.getMessage());
                    
                    if (!primaryProvider.getProviderName().equals("gemini")) {
                        System.out.println("Falling back to Gemini API...");
                        return providerFactory.getProvider("gemini").generateInsights(request);
                    }
                    return Mono.error(e); // Gemini already failed
                })
                .onErrorResume(e -> {
                    System.err.println("AI provider failed. Using fallback mentor responses.");
                    return Mono.just(getFallbackResponse(request));
                });
    }

    private AIResponse getFallbackResponse(AIRequest request) {
        String algo = request.getAlgorithmType() != null ? request.getAlgorithmType().toLowerCase() : "";
        
        String hint = "Think about how you can break down the problem into smaller steps.";
        String improvement = "Consider using more descriptive variable names.";
        String optimization = "Try to avoid redundant calculations inside your loops.";
        String error = "";
        String complexity = "O(N) time generally, but depends on your exact loops.";
        String edge = "Consider empty inputs, arrays of size 1, or already sorted data.";
        String summary = "You're doing great, keep experimenting!";
        
        if (request.getErrorMessage() != null && !request.getErrorMessage().isEmpty()) {
            String rawError = request.getErrorMessage();
            if (rawError.contains("ArrayIndexOutOfBoundsException") || rawError.contains("IndexError")) {
                error = "You tried accessing an array index outside the valid range (e.g. index N in an array of size N).";
            } else if (rawError.contains("StackOverflowError") || rawError.contains("RecursionError")) {
                error = "Your recursion may not be reaching a proper base case stopping condition.";
            } else if (rawError.contains("NullPointerException") || rawError.contains("TypeError")) {
                error = "You are trying to use an object that hasn't been initialized yet.";
            } else {
                error = "An error occurred during execution: " + rawError;
            }
        }

        if (algo.contains("bubble")) {
            hint = "Can you bubble the largest item to the end in a single pass?";
            improvement = "You can add a boolean flag to track if a swap occurred and break early.";
            optimization = "Avoid comparing elements that are already sorted at the end of the array.";
            complexity = "Time Complexity: O(N²) because of nested loops. Space Complexity: O(1).";
            edge = "What if the array is already sorted? Can you optimize your loop to stop early?";
            summary = "Bubble sort repeatedly compares neighboring values.";
        } else if (algo.contains("binary")) {
            hint = "Are you updating the low and high pointers correctly to halve the search space?";
            improvement = "Use mid = low + (high - low) / 2 to prevent integer overflow.";
            optimization = "Binary search is already highly optimal for sorted data.";
            complexity = "Time Complexity: O(log N) because the search space halves each step. Space: O(1).";
            edge = "Consider an empty array, or when the target is at the very beginning or end.";
            summary = "Binary search is incredibly fast for sorted data.";
        } else if (algo.contains("dfs")) {
            hint = "Are you keeping track of visited nodes to avoid infinite cycles?";
            improvement = "Make sure your base cases or visit checks happen early in the function.";
            optimization = "Use an iterative stack if deep recursion causes StackOverflow.";
            complexity = "Time Complexity: O(V + E) since we visit every node and edge. Space: O(V) for the recursion stack.";
            edge = "What if the graph has isolated nodes or disconnected components?";
            summary = "DFS explores deeply into a path before returning.";
        }

        return AIResponse.builder()
                .hint(hint)
                .improvementSuggestion(improvement)
                .optimizationHint(optimization)
                .errorExplanation(error)
                .complexityExplanation(complexity)
                .edgeCaseExplanation(edge)
                .summary(summary)
                .build();
    }
}
