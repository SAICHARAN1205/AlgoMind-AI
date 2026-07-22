package com.algomind.dto;

import com.algomind.model.ExecutionState;
import com.algomind.model.PlaybackStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionMessage {
    
    private String sessionId;
    private int currentStep;
    private int totalSteps;
    private ExecutionState executionState;
    private PlaybackStatus playbackStatus;
    private long timestamp;
}
