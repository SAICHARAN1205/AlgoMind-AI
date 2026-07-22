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
public class FibonacciSimulator implements AlgorithmSimulator {

    private int stepCount = 1;
    private int nodeCounter = 1;

    @Override
    public String getAlgorithmName() {
        return "fibonacci";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        if (context.getTarget() == null) {
            throw new InvalidInputException("Target must be provided for fibonacci (the nth term).");
        }

        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        nodeCounter = 1;

        List<StackFrame> stack = new ArrayList<>();
        Map<String, RecursionNode> nodes = new HashMap<>();

        simulateFibonacci(context.getTarget(), null, stack, nodes, states);

        return states;
    }

    private int simulateFibonacci(int n, String parentId, List<StackFrame> stack, Map<String, RecursionNode> nodes, List<ExecutionState> states) {
        String nodeId = "node-" + nodeCounter++;
        int depth = stack.size() + 1;

        RecursionNode node = RecursionNode.builder()
                .nodeId(nodeId)
                .functionName("fibonacci")
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
                .functionName("fibonacci")
                .parameters(Map.of("n", String.valueOf(n)))
                .localVariables(new HashMap<>())
                .depth(depth)
                .build();
        stack.add(frame);

        node.setExecutionStatus(ExecutionStatus.EXECUTING);
        node.setEducationalNote("Calling fibonacci(" + n + ").");
        states.add(createState(nodes, nodeId, stack, OperationType.INIT, ExecutionPhase.INITIALIZATION, "Function Call", "Pushing fibonacci(" + n + ") onto the stack."));

        int result;
        if (n <= 1) {
            result = n;
            node.setEducationalNote("Base condition met (n <= 1). Returning " + n + ".");
            states.add(createState(nodes, nodeId, stack, OperationType.COMPARE, ExecutionPhase.COMPARISON, "Base Case", "n is " + n + ", which is <= 1. Reached the base condition."));
        } else {
            node.setEducationalNote("Recursive step: fibonacci(" + (n - 1) + ") + fibonacci(" + (n - 2) + ")");
            states.add(createState(nodes, nodeId, stack, OperationType.ITERATION, ExecutionPhase.ITERATION, "Branching", "Fibonacci recursively splits into two smaller subproblems: n-1 and n-2."));

            int leftResult = simulateFibonacci(n - 1, nodeId, stack, nodes, states);
            frame.getLocalVariables().put("leftResult", String.valueOf(leftResult));
            node.setEducationalNote("Left branch returned " + leftResult + ". Now evaluating right branch fibonacci(" + (n - 2) + ").");
            states.add(createState(nodes, nodeId, stack, OperationType.CALCULATE, ExecutionPhase.COMPUTATION, "Left Branch Done", "Received " + leftResult + " from fibonacci(" + (n - 1) + ")."));

            int rightResult = simulateFibonacci(n - 2, nodeId, stack, nodes, states);
            frame.getLocalVariables().put("rightResult", String.valueOf(rightResult));
            node.setEducationalNote("Right branch returned " + rightResult + ".");
            
            result = leftResult + rightResult;
            states.add(createState(nodes, nodeId, stack, OperationType.CALCULATE, ExecutionPhase.COMPUTATION, "Computation", "Sum " + leftResult + " + " + rightResult + " = " + result));
        }

        node.setReturnValue(String.valueOf(result));
        node.setExecutionStatus(ExecutionStatus.RETURNED);
        node.setEducationalNote("Returning " + result + " to caller.");

        stack.remove(stack.size() - 1);
        states.add(createState(nodes, nodeId, stack, OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Return Phase", "Popping fibonacci(" + n + ") from stack."));

        node.setExecutionStatus(ExecutionStatus.COMPLETED);

        return result;
    }

    private ExecutionState createState(Map<String, RecursionNode> nodes, String activeNodeId, List<StackFrame> stack, OperationType opType, ExecutionPhase phase, String stepTitle, String note) {
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
                .lineNumber(0)
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
