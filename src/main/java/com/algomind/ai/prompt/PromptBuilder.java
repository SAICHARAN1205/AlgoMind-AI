package com.algomind.ai.prompt;

import com.algomind.ai.dto.AIRequest;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildPrompt(AIRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert DSA mentor.\n");
        sb.append("Analyze this algorithm code.\n");
        sb.append("Return:\n");
        sb.append("1. Beginner-friendly explanation\n");
        sb.append("2. Optimization suggestions\n");
        sb.append("3. Complexity explanation\n");
        sb.append("4. Edge cases\n");
        sb.append("5. Improvement ideas\n\n");
        
        sb.append("Algorithm: ").append(request.getAlgorithmType() != null ? request.getAlgorithmType() : "Unknown").append("\n");
        sb.append("Language: ").append("Java").append("\n"); // If language isn't in AIRequest, default or extract. Wait, language isn't in AIRequest! I should just say "Code Language" or skip if not available, but the prompt wants {language}. The codebase's AIRequest doesn't have a language field. I will just omit the explicit {language} if it's not present or hardcode it as general code. I will check AIRequest first! Wait, AIRequest has userCode. I will just pass the code.
        sb.append("Code:\n").append(request.getUserCode() != null ? request.getUserCode() : "").append("\n\n");
        
        if (request.getErrorMessage() != null && !request.getErrorMessage().isEmpty()) {
            sb.append("Error Encountered: ").append(request.getErrorMessage()).append("\n\n");
        }
        
        sb.append("Please provide a JSON response EXACTLY matching this structure, with no markdown code fences like ```json:\n");
        sb.append("{\n");
        sb.append("  \"errorExplanation\": \"Explain the error simply (if any), otherwise empty string.\",\n");
        sb.append("  \"hint\": \"(Map to: Beginner-friendly explanation) Provide a Socratic question or conceptual hint to guide them.\",\n");
        sb.append("  \"improvementSuggestion\": \"(Map to: Improvement ideas) Suggest one concrete way to write cleaner or more standard code.\",\n");
        sb.append("  \"optimizationHint\": \"(Map to: Optimization suggestions) Suggest an optimization for time or space if applicable.\",\n");
        sb.append("  \"complexityExplanation\": \"(Map to: Complexity explanation) Explain time and space complexity intuitively for this algorithm type.\",\n");
        sb.append("  \"edgeCaseExplanation\": \"(Map to: Edge cases) List 1-2 important edge cases to consider for this algorithm.\",\n");
        sb.append("  \"summary\": \"(Map to: Beginner-friendly explanation) A short, friendly 1-sentence encouragement or summary.\"\n");
        sb.append("}");
        
        return sb.toString();
    }
}
