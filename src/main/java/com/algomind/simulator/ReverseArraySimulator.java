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
public class ReverseArraySimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "reverseArray";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        int stepCount = 1;
        int[] currentArray = context.getArray().clone();
        int i = 0;
        int j = currentArray.length - 1;

        states.add(createState(stepCount++, 1, currentArray, i, j, "Pointers initialized", OperationType.INIT, ExecutionPhase.INITIALIZATION, "Initialize Pointers", "Start pointing at both ends of the array.", List.of(i, j), false));

        while (i < j) {
            states.add(createState(stepCount++, 3, currentArray, i, j, "Checking if i < j (true)", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Check Condition", "Ensure pointers haven't crossed.", List.of(i, j), false));

            int temp = currentArray[i];
            currentArray[i] = currentArray[j];
            currentArray[j] = temp;
            
            states.add(createState(stepCount++, 4, currentArray, i, j, "Elements were swapped because we need to reverse their positions.", OperationType.SWAP, ExecutionPhase.SWAP, "Swap Elements", "Swap the outer elements.", List.of(i, j), true));

            i++;
            j--;
            
            states.add(createState(stepCount++, 5, currentArray, i, j, "Pointers moved inward", OperationType.POINTER_MOVE, ExecutionPhase.ITERATION, "Move Pointers", "Move pointers closer to the center.", List.of(i, j), false));
        }
        
        if (i >= j) {
            states.add(createState(stepCount++, 3, currentArray, i, j, "Checking if i < j (false), loop terminates", OperationType.COMPARE, ExecutionPhase.COMPARISON, "Check Condition", "Pointers crossed, loop ends.", List.of(i, j), false));
        }
        
        states.add(createState(stepCount++, 7, currentArray, i, j, "Array reversal complete", OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Done", "The array is now reversed.", List.of(), false));

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
