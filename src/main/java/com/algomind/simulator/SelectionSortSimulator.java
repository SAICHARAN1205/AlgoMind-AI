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
public class SelectionSortSimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "selection-sort";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        int stepCount = 1;
        int[] arr = context.getArray().clone();
        int n = arr.length;
        
        states.add(createState(stepCount++, 1, arr, 0, 0, 0, "Initialization of Selection Sort", OperationType.INIT, ExecutionPhase.INITIALIZATION, "Initialize", "Start the selection sort algorithm.", List.of(), false));

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            states.add(createState(stepCount++, 2, arr, i, i, minIndex, "Setting minimum index to " + i, OperationType.POINTER_MOVE, ExecutionPhase.ITERATION, "Set Min", "Assume the first unsorted element is the minimum.", List.of(minIndex), false));

            for (int j = i + 1; j < n; j++) {
                states.add(createState(stepCount++, 3, arr, i, j, minIndex, "Comparing arr[j] with arr[minIndex]", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Compare", "Finding if there is a smaller element.", List.of(j, minIndex), false));
                
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                    states.add(createState(stepCount++, 4, arr, i, j, minIndex, "New minimum found at index " + minIndex, OperationType.POINTER_MOVE, ExecutionPhase.ITERATION, "Update Min", "Found a smaller element.", List.of(minIndex), false));
                }
            }
            
            if (minIndex != i) {
                states.add(createState(stepCount++, 5, arr, i, n, minIndex, "Swapping minimum element into sorted position.", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Prepare Swap", "Swap min element with first unsorted element.", List.of(i, minIndex), false));
                int temp = arr[minIndex];
                arr[minIndex] = arr[i];
                arr[i] = temp;
                states.add(createState(stepCount++, 6, arr, i, n, minIndex, "Swapped elements.", OperationType.SWAP, ExecutionPhase.SWAP, "Swap", "Minimum element placed in sorted order.", List.of(i, minIndex), true));
            }
            
            states.add(createState(stepCount++, 7, arr, i, n, minIndex, "Element settled in sorted position.", OperationType.POINTER_MOVE, ExecutionPhase.ITERATION, "End of Pass", "One more element is sorted.", List.of(i), false));
        }
        
        states.add(createState(stepCount++, 8, arr, n, 0, 0, "Sorting completed", OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Done", "Array is sorted.", List.of(), false));
        return states;
    }

    private ExecutionState createState(int step, int lineNumber, int[] array, int i, int j, int minIndex, String message, OperationType opType, ExecutionPhase phase, String stepTitle, String note, List<Integer> highlighted, boolean swap) {
        Map<String, Integer> variables = new HashMap<>();
        variables.put("i", i);
        variables.put("j", j);
        variables.put("minIndex", minIndex);
        
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
