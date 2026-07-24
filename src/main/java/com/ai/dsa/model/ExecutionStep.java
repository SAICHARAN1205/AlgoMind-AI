package com.ai.dsa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a single step in algorithm execution for visualization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionStep {
    
    private int stepNumber;
    private String description;
    private String operation;
    private Map<String, Object> currentState;
    private Map<String, Object> previousState;
    private List<Integer> highlightedIndices;
    private List<Integer> comparedIndices;
    private List<Integer> swappedIndices;
    private boolean isCompleted;
    private long timestamp;
    private String visualizationData;
    
    @Builder.Default
    private StepType stepType = StepType.OPERATION;
    
    public enum StepType {
        INITIALIZATION,
        COMPARISON,
        SWAP,
        INSERTION,
        DELETION,
        SEARCH,
        TRAVERSAL,
        OPERATION,
        COMPLETION
    }
}