package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.exception.InvalidInputException;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BinarySearchSimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "binarySearch";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        if (context.getTarget() == null) {
            throw new InvalidInputException("Target must be provided for binary search.");
        }
        
        List<ExecutionState> states = new ArrayList<>();
        int stepCount = 1;
        int[] arr = context.getArray().clone();
        int target = context.getTarget();
        
        int low = 0;
        int high = arr.length - 1;
        int mid = -1;

        states.add(createState(stepCount++, 1, arr, low, high, mid, target, "Initialized low and high pointers", OperationType.INIT, ExecutionPhase.INITIALIZATION, "Initialize", "Set bounds to the ends of the array.", List.of(low, high)));

        while (low <= high) {
            mid = low + (high - low) / 2;
            states.add(createState(stepCount++, 2, arr, low, high, mid, target, "Calculated mid index: " + mid, OperationType.MID_CALCULATION, ExecutionPhase.ITERATION, "Calculate Mid", "Find the middle of the current search space.", List.of(mid)));

            states.add(createState(stepCount++, 3, arr, low, high, mid, target, "Comparing target with mid element", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Compare Target", "Check if we found the target.", List.of(mid)));

            if (arr[mid] == target) {
                states.add(createState(stepCount++, 3, arr, low, high, mid, target, "Target matches the middle element!", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Match Found", "The middle element is exactly the target.", List.of(mid)));
                states.add(createState(stepCount++, 4, arr, low, high, mid, target, "Target found at index " + mid, OperationType.FOUND, ExecutionPhase.COMPLETION, "Found!", "The target was found at the mid index.", List.of(mid)));
                return states;
            } else if (arr[mid] < target) {
                states.add(createState(stepCount++, 3, arr, low, high, mid, target, "Target is greater than middle element.", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Compare Result", "Target > Mid", List.of(mid)));
                low = mid + 1;
                states.add(createState(stepCount++, 5, arr, low, high, mid, target, "Target is greater, searching right half.", OperationType.MOVE_RIGHT, ExecutionPhase.ITERATION, "Move Right", "Discard the left half.", List.of(low)));
            } else {
                states.add(createState(stepCount++, 3, arr, low, high, mid, target, "Target is smaller than middle element.", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Compare Result", "Target < Mid", List.of(mid)));
                high = mid - 1;
                states.add(createState(stepCount++, 6, arr, low, high, mid, target, "Target is smaller, searching left half.", OperationType.MOVE_LEFT, ExecutionPhase.ITERATION, "Move Left", "Discard the right half.", List.of(high)));
            }
        }

        states.add(createState(stepCount++, 7, arr, low, high, mid, target, "Target not found in array", OperationType.NOT_FOUND, ExecutionPhase.COMPLETION, "Not Found", "The search space is exhausted and target is missing.", List.of()));
        return states;
    }

    private ExecutionState createState(int step, int lineNumber, int[] array, int low, int high, int mid, int target, String message, OperationType opType, ExecutionPhase phase, String stepTitle, String note, List<Integer> highlighted) {
        Map<String, Integer> variables = new HashMap<>();
        variables.put("low", low);
        variables.put("high", high);
        variables.put("mid", mid);
        variables.put("target", target);
        
        return ExecutionState.builder()
                .step(step)
                .lineNumber(lineNumber)
                .currentLine(lineNumber)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .array(array.clone()) 
                .highlightedIndices(highlighted)
                .variables(variables)
                .swapOccurred(false)
                .message(message)
                .timeComplexity("O(log N)")
                .spaceComplexity("O(1)")
                .build();
    }
}
