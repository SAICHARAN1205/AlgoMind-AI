package com.algomind.execution.tracer;

import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutionTracer {
    private final List<ExecutionState> states = new ArrayList<>();
    private int stepCounter = 0;

    public void recordState(int lineNumber, 
                            OperationType operationType, 
                            String message, 
                            int[] arrayState, 
                            Map<String, Integer> variables, 
                            boolean swapOccurred, 
                            List<Integer> highlightedIndices) {
                            
        ExecutionState state = ExecutionState.builder()
                .step(stepCounter++)
                .lineNumber(lineNumber)
                .currentLine(lineNumber)
                .operationType(operationType)
                .executionPhase(ExecutionPhase.EXECUTION)
                .stepTitle("Executing Line " + lineNumber)
                .message(message)
                .array(arrayState)
                .variables(variables)
                .swapOccurred(swapOccurred)
                .highlightedIndices(highlightedIndices != null ? highlightedIndices : new ArrayList<>())
                .build();
                
        states.add(state);
    }

    public List<ExecutionState> getStates() {
        return states;
    }
}
