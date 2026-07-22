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
public class BubbleSortSimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "bubbleSort";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        int stepCount = 1;
        int[] arr = context.getArray().clone();
        int n = arr.length;
        
        states.add(createState(stepCount++, 1, arr, 0, 0, "Initialization of Bubble Sort", OperationType.INIT, ExecutionPhase.INITIALIZATION, "Initialize", "Start the bubble sort algorithm.", List.of(), false));

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                
                states.add(createState(stepCount++, 3, arr, i, j, "Comparing elements to determine which is larger.", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Compare", "Comparing adjacent elements.", List.of(j, j + 1), false));
                
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                    
                    states.add(createState(stepCount++, 4, arr, i, j, "Elements were swapped because the left value was larger than the right value.", OperationType.SWAP, ExecutionPhase.SWAP, "Swap Elements", "Pushing larger value to the right.", List.of(j, j + 1), true));
                }
            }
            
            states.add(createState(stepCount++, 5, arr, i, n - i - 1, "Largest element settled at end", OperationType.POINTER_MOVE, ExecutionPhase.ITERATION, "End of Pass", "The largest element in the unsorted portion is now in its final position.", List.of(n - i - 1), false));

            if (!swapped) {
                states.add(createState(stepCount++, 6, arr, i, 0, "No swaps occurred in this pass, array is sorted", OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Early Exit", "The array is fully sorted early.", List.of(), false));
                return states;
            }
        }
        
        states.add(createState(stepCount++, 7, arr, n, 0, "Sorting completed", OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Done", "Array is sorted.", List.of(), false));
        return states;
    }

    private ExecutionState createState(int step, int lineNumber, int[] array, int i, int j, String message, OperationType opType, ExecutionPhase phase, String stepTitle, String note, List<Integer> highlighted, boolean swap) {
        Map<String, Integer> variables = new HashMap<>();
        variables.put("i", i);
        variables.put("j", j);
        
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
