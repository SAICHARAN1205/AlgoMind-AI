package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.exception.InvalidInputException;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.recursion.ExecutionStatus;
import com.algomind.model.recursion.RecursionNode;
import com.algomind.model.recursion.StackFrame;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FactorialSimulator implements AlgorithmSimulator {

    private int stepCount = 1;
    private int nodeCounter = 1;

    @Override
    public String getAlgorithmName() {
        return "factorial";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        if (context.getTarget() == null) {
            throw new InvalidInputException("Target must be provided for factorial (the number to compute).");
        }

        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        nodeCounter = 1;
        
        List<StackFrame> stack = new ArrayList<>();
        Map<String, RecursionNode> nodes = new HashMap<>();

        simulateFactorial(context.getTarget(), null, stack, nodes, states);

        return states;
    }

    private int simulateFactorial(int n, String parentId, List<StackFrame> stack, Map<String, RecursionNode> nodes, List<ExecutionState> states) {
        String nodeId = "node-" + nodeCounter++;
        int depth = stack.size() + 1;
        
        RecursionNode node = RecursionNode.builder()
                .nodeId(nodeId)
                .functionName("factorial")
                .parameters(Map.of("n", String.valueOf(n)))
                .depth(depth)
                .parentId(parentId)
                .childrenIds(new ArrayList<>())
                .executionStatus(ExecutionStatus.CREATED)
                .build();
                
        if (parentId != null) {
            nodes.get(parentId).getChildrenIds().add(nodeId);
        }
        nodes.put(nodeId, node);

        StackFrame frame = StackFrame.builder()
                .functionName("factorial")
                .parameters(Map.of("n", String.valueOf(n)))
                .localVariables(new HashMap<>())
                .depth(depth)
                .build();
        stack.add(frame);

        node.setExecutionStatus(ExecutionStatus.EXECUTING);
        node.setEducationalNote("Calling factorial(" + n + "). Stack grows.");
        states.add(createState(nodes, nodeId, stack, OperationType.INIT, ExecutionPhase.INITIALIZATION, "Function Call", "Pushing factorial(" + n + ") onto the stack."));

        int result;
        if (n <= 1) {
            result = 1;
            node.setEducationalNote("Base condition met (n <= 1). Returning 1.");
            states.add(createState(nodes, nodeId, stack, OperationType.COMPARE, ExecutionPhase.COMPARISON, "Base Case", "n is " + n + ", which is <= 1. Reached the base condition."));
        } else {
            node.setEducationalNote("Recursive step: " + n + " * factorial(" + (n - 1) + ")");
            states.add(createState(nodes, nodeId, stack, OperationType.ITERATION, ExecutionPhase.ITERATION, "Recursive Call", "We need to calculate factorial(" + (n - 1) + ") before we can multiply by " + n + "."));
            
            int childResult = simulateFactorial(n - 1, nodeId, stack, nodes, states);
            result = n * childResult;
            frame.getLocalVariables().put("childResult", String.valueOf(childResult));
            node.setEducationalNote("Received " + childResult + " from recursive call. Multiplying by " + n + ".");
            states.add(createState(nodes, nodeId, stack, OperationType.CALCULATE, ExecutionPhase.COMPUTATION, "Computation", "Multiply " + n + " * " + childResult + " = " + result));
        }

        node.setReturnValue(String.valueOf(result));
        node.setExecutionStatus(ExecutionStatus.RETURNED);
        node.setEducationalNote("Returning " + result + " to caller.");
        
        stack.remove(stack.size() - 1); // pop
        states.add(createState(nodes, nodeId, stack, OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Return Phase", "Popping factorial(" + n + ") from stack."));
        
        node.setExecutionStatus(ExecutionStatus.COMPLETED);
        
        return result;
    }

    private ExecutionState createState(Map<String, RecursionNode> nodes, String activeNodeId, List<StackFrame> stack, OperationType opType, ExecutionPhase phase, String stepTitle, String note) {
        // Deep copy the stack to preserve snapshot state
        List<StackFrame> stackSnapshot = new ArrayList<>();
        for (StackFrame f : stack) {
            stackSnapshot.add(StackFrame.builder()
                    .functionName(f.getFunctionName())
                    .parameters(new HashMap<>(f.getParameters()))
                    .localVariables(new HashMap<>(f.getLocalVariables()))
                    .depth(f.getDepth())
                    .build());
        }
        
        Map<String, RecursionNode> treeSnapshot = new HashMap<>();
        for (Map.Entry<String, RecursionNode> entry : nodes.entrySet()) {
            RecursionNode n = entry.getValue();
            treeSnapshot.put(entry.getKey(), RecursionNode.builder()
                    .nodeId(n.getNodeId())
                    .functionName(n.getFunctionName())
                    .parameters(new HashMap<>(n.getParameters()))
                    .returnValue(n.getReturnValue())
                    .depth(n.getDepth())
                    .parentId(n.getParentId())
                    .childrenIds(new ArrayList<>(n.getChildrenIds()))
                    .executionStatus(n.getExecutionStatus())
                    .educationalNote(n.getEducationalNote())
                    .build());
        }

        return ExecutionState.builder()
                .step(stepCount++)
                .lineNumber(0) // Default or map to AST if we parse code
                .currentLine(0)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .visualizationType(VisualizationType.TREE)
                .recursionTree(treeSnapshot)
                .activeNodeId(activeNodeId)
                .callStack(stackSnapshot)
                .message(note)
                .build();
    }
}
