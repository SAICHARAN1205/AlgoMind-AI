package com.algomind.ai.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    private String hint;
    private String errorExplanation;
    private String complexityExplanation;
    private String edgeCaseExplanation;
    private String improvementSuggestion;
    private String optimizationHint;
    private String summary;
}
