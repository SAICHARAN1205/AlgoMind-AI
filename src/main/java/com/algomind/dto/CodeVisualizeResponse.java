package com.algomind.dto;

import com.algomind.model.ExecutionState;
import com.algomind.model.VisualizationType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CodeVisualizeResponse {
    private String detectedAlgorithm;
    private VisualizationType visualizationType;
    private String explanation;
    private double confidence;
    private boolean lowConfidence;
    private List<ExecutionState> states;
}
