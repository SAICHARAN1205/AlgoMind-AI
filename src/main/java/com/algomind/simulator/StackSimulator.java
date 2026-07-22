package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.StackExecutionState;
import com.algomind.model.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StackSimulator implements AlgorithmSimulator {

    private int stepCount = 1;

    @Override
    public String getAlgorithmName() {
        return "stack";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;

        List<Integer> stackElements = new ArrayList<>();
        int topIndex = -1;

        // Initial State
        states.add(createState(stackElements, topIndex, "START", null, OperationType.START, ExecutionPhase.INITIALIZATION, "Initialize Stack", "The stack is initially empty. Top points to -1."));

        // Push 10
        stackElements.add(10);
        topIndex++;
        states.add(createState(stackElements, topIndex, "PUSH", 10, OperationType.PUSH, ExecutionPhase.COMPUTATION, "Push 10", "Push 10 onto the stack. Top increments."));

        // Push 20
        stackElements.add(20);
        topIndex++;
        states.add(createState(stackElements, topIndex, "PUSH", 20, OperationType.PUSH, ExecutionPhase.COMPUTATION, "Push 20", "Push 20 onto the stack. Top increments."));

        // Push 30
        stackElements.add(30);
        topIndex++;
        states.add(createState(stackElements, topIndex, "PUSH", 30, OperationType.PUSH, ExecutionPhase.COMPUTATION, "Push 30", "Push 30 onto the stack. Top increments."));

        // Peek
        states.add(createState(stackElements, topIndex, "PEEK", stackElements.get(topIndex), OperationType.PEEK, ExecutionPhase.COMPUTATION, "Peek", "Peek returns the top element (30) without removing it."));

        // Pop
        int popped = stackElements.remove(topIndex);
        topIndex--;
        states.add(createState(stackElements, topIndex, "POP", popped, OperationType.POP, ExecutionPhase.COMPUTATION, "Pop", "Pop removes the top element (" + popped + "). Stack follows Last In First Out (LIFO)."));

        // Pop
        popped = stackElements.remove(topIndex);
        topIndex--;
        states.add(createState(stackElements, topIndex, "POP", popped, OperationType.POP, ExecutionPhase.COMPUTATION, "Pop", "Pop removes the top element (" + popped + ")."));

        // Push 40
        stackElements.add(40);
        topIndex++;
        states.add(createState(stackElements, topIndex, "PUSH", 40, OperationType.PUSH, ExecutionPhase.COMPUTATION, "Push 40", "Push 40 onto the stack."));

        states.add(createState(stackElements, topIndex, "COMPLETE", null, OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Operations Complete", "Stack operations demonstrated successfully."));

        return states;
    }

    private ExecutionState createState(List<Integer> stackElements, int topIndex, String activeOp, Integer highlighted, OperationType opType, ExecutionPhase phase, String stepTitle, String note) {
        StackExecutionState stackState = StackExecutionState.builder()
                .stackElements(new ArrayList<>(stackElements))
                .topIndex(topIndex)
                .activeOperation(activeOp)
                .highlightedElement(highlighted)
                .explanation(note)
                .build();

        Map<String, Integer> vars = new HashMap<>();
        vars.put("top", topIndex);
        vars.put("size", stackElements.size());

        return ExecutionState.builder()
                .step(stepCount++)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O(1) per operation")
                .spaceComplexity("O(N)")
                .variables(vars)
                .visualizationType(VisualizationType.STACK)
                .stackState(stackState)
                .message(note)
                .build();
    }
}
