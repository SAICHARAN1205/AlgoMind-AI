package com.algomind.timeline;

import com.algomind.model.ExecutionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import com.algomind.model.VisualizationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionTimeline {
    private int totalSteps;
    private String algorithmName;
    private String executionSummary;
    private String estimatedComplexity;
    private Map<String, String> educationalInsights;
    private VisualizationType visualizationType;
    private List<ExecutionState> executionStates;
}
