package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InsertionSortSimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "insertion-sort";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        int stepCount = 1;
        int[] arr = context.getArray().clone();
        int n = arr.length;
        
        states.add(createState(stepCount++, 1, arr, 0, 0, 0, "Initialization of Insertion Sort", OperationType.INIT, ExecutionPhase.INITIALIZATION, "Initialize", "Start the insertion sort algorithm.", List.of(), false));

        for (int i = 1; i < n; ++i) {
            int key = arr[i];
            int j = i - 1;
            
            states.add(createState(stepCount++, 2, arr, i, j, key, "Selecting element " + key + " as key to insert", OperationType.POINTER_MOVE, ExecutionPhase.ITERATION, "Select Key", "Pick the next element to insert into the sorted portion.", List.of(i), false));

            while (j >= 0 && arr[j] > key) {
                states.add(createState(stepCount++, 3, arr, i, j, key, "Comparing " + arr[j] + " with key " + key, OperationType.COMPARE, ExecutionPhase.COMPARISON, "Compare", "Check if element is greater than key.", List.of(j, j+1), false));
                
                arr[j + 1] = arr[j];
                states.add(createState(stepCount++, 4, arr, i, j, key, "Shifting " + arr[j] + " to the right", OperationType.SWAP, ExecutionPhase.SWAP, "Shift", "Make room for the key.", List.of(j, j+1), true));
                
                j = j - 1;
            }
            
            if (j >= 0) {
                states.add(createState(stepCount++, 5, arr, i, j, key, "Comparing " + arr[j] + " with key " + key + ". No shift needed.", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Compare", "Found correct position.", List.of(j), false));
            }

            arr[j + 1] = key;
            states.add(createState(stepCount++, 6, arr, i, j + 1, key, "Inserted key " + key + " into correct position", OperationType.POINTER_MOVE, ExecutionPhase.ITERATION, "Insert", "Element inserted into sorted portion.", List.of(j + 1), false));
        }
        
        states.add(createState(stepCount++, 7, arr, n, 0, 0, "Sorting completed", OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Done", "Array is sorted.", List.of(), false));
        return states;
    }

    private ExecutionState createState(int step, int lineNumber, int[] array, int i, int j, int key, String message, OperationType opType, ExecutionPhase phase, String stepTitle, String note, List<Integer> highlighted, boolean swap) {
        Map<String, Integer> variables = new HashMap<>();
        variables.put("i", i);
        variables.put("j", j);
        variables.put("key", key);
        
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
                .swapOccurred(swap)
                .message(message)
                .build();
    }
}
