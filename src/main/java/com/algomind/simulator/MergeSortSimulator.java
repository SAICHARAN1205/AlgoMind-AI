package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.exception.InvalidInputException;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.recursion.ExecutionStatus;
import com.algomind.model.recursion.RecursionTreeNode;
import com.algomind.model.recursion.StackFrame;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MergeSortSimulator implements AlgorithmSimulator {

    private int stepCount = 1;
    private int nodeCounter = 1;

    @Override
    public String getAlgorithmName() {
        return "mergeSort";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        if (context.getArray() == null || context.getArray().length == 0) {
            throw new InvalidInputException("Array must be provided for merge sort.");
        }

        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        nodeCounter = 1;

        List<StackFrame> stack = new ArrayList<>();
        Map<String, RecursionTreeNode> nodes = new LinkedHashMap<>();
        
        int[] arr = context.getArray().clone();

        simulateMergeSort(arr, 0, arr.length - 1, null, stack, nodes, states);

        return states;
    }

    private void simulateMergeSort(int[] arr, int left, int right, String parentId, List<StackFrame> stack, Map<String, RecursionTreeNode> nodes, List<ExecutionState> states) {
        String nodeId = "node-" + nodeCounter++;
        int depth = stack.size() + 1;
        
        int[] subArray = Arrays.copyOfRange(arr, left, right + 1);

        RecursionTreeNode node = RecursionTreeNode.builder()
                .nodeId(nodeId)
                .parentId(parentId)
                .depth(depth)
                .startIndex(left)
                .endIndex(right)
                .subArray(subArray)
                .operationType(OperationType.RECURSIVE_CALL)
                .executionStatus(ExecutionStatus.CREATED)
                .explanation("Calling mergeSort from index " + left + " to " + right + ".")
                .build();

        nodes.put(nodeId, node);

        StackFrame frame = StackFrame.builder()
                .functionName("mergeSort")
                .parameters(Map.of("left", String.valueOf(left), "right", String.valueOf(right)))
                .localVariables(new HashMap<>())
                .depth(depth)
                .build();
        stack.add(frame);

        node.setExecutionStatus(ExecutionStatus.EXECUTING);
        node.setOperationType(OperationType.RECURSIVE_CALL);
        node.setExplanation("Merge sort divides the array into smaller parts. Current range: [" + left + "..." + right + "]");
        states.add(createState(nodes, nodeId, stack, arr, OperationType.RECURSIVE_CALL, ExecutionPhase.COMPUTATION, "Recursive Call", node.getExplanation(), left, right, -1));

        if (left < right) {
            int mid = left + (right - left) / 2;
            frame.getLocalVariables().put("mid", String.valueOf(mid));
            
            node.setOperationType(OperationType.SPLIT);
            node.setExplanation("Splitting array at midpoint " + mid + " into left [" + left + "..." + mid + "] and right [" + (mid + 1) + "..." + right + "].");
            states.add(createState(nodes, nodeId, stack, arr, OperationType.SPLIT, ExecutionPhase.COMPUTATION, "Divide Phase", node.getExplanation(), left, right, mid));
            
            simulateMergeSort(arr, left, mid, nodeId, stack, nodes, states);
            simulateMergeSort(arr, mid + 1, right, nodeId, stack, nodes, states);
            
            node.setOperationType(OperationType.MERGE_START);
            node.setExplanation("Sorted subarrays are merged back together. Merging [" + left + "..." + mid + "] and [" + (mid + 1) + "..." + right + "].");
            states.add(createState(nodes, nodeId, stack, arr, OperationType.MERGE_START, ExecutionPhase.COMPUTATION, "Merge Start", node.getExplanation(), left, right, mid));
            
            merge(arr, left, mid, right, nodeId, nodes, stack, states);
        } else {
            node.setOperationType(OperationType.BASE_CASE);
            node.setExplanation("When the subarray contains only one element, recursion stops.");
            states.add(createState(nodes, nodeId, stack, arr, OperationType.BASE_CASE, ExecutionPhase.COMPUTATION, "Base Case", node.getExplanation(), left, right, -1));
        }

        node.setOperationType(OperationType.RETURN);
        node.setExecutionStatus(ExecutionStatus.RETURNED);
        node.setExplanation("Returning from mergeSort [" + left + "..." + right + "]. Subarray is now sorted: " + Arrays.toString(Arrays.copyOfRange(arr, left, right + 1)));
        
        stack.remove(stack.size() - 1);
        states.add(createState(nodes, nodeId, stack, arr, OperationType.RETURN, ExecutionPhase.COMPUTATION, "Return Phase", node.getExplanation(), left, right, -1));
        
        node.setExecutionStatus(ExecutionStatus.COMPLETED);
    }
    
    private void merge(int[] arr, int left, int mid, int right, String activeNodeId, Map<String, RecursionTreeNode> nodes, List<StackFrame> stack, List<ExecutionState> states) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        
        int[] L = new int[n1];
        int[] R = new int[n2];
        
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        
        int i = 0, j = 0;
        int k = left;
        
        while (i < n1 && j < n2) {
            String explanation = "Comparing L[" + i + "]=" + L[i] + " with R[" + j + "]=" + R[j] + ".";
            nodes.get(activeNodeId).setOperationType(OperationType.MERGE_COMPARE);
            nodes.get(activeNodeId).setExplanation(explanation);
            
            states.add(createStateWithMerge(nodes, activeNodeId, stack, arr, OperationType.MERGE_COMPARE, ExecutionPhase.COMPUTATION, "Merge Compare", explanation, left, right, mid, k, left + i, mid + 1 + j));
            
            if (L[i] <= R[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
            
            nodes.get(activeNodeId).setSubArray(Arrays.copyOfRange(arr, left, right + 1));
            states.add(createStateWithMerge(nodes, activeNodeId, stack, arr, OperationType.MERGE_COMPLETE, ExecutionPhase.COMPUTATION, "Merge Place", "Placed element at index " + (k-1) + ".", left, right, mid, k-1, -1, -1));
        }
        
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
            nodes.get(activeNodeId).setSubArray(Arrays.copyOfRange(arr, left, right + 1));
            states.add(createStateWithMerge(nodes, activeNodeId, stack, arr, OperationType.MERGE_COMPLETE, ExecutionPhase.COMPUTATION, "Merge Place Remaining", "Copying remaining from left.", left, right, mid, k-1, -1, -1));
        }
        
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
            nodes.get(activeNodeId).setSubArray(Arrays.copyOfRange(arr, left, right + 1));
            states.add(createStateWithMerge(nodes, activeNodeId, stack, arr, OperationType.MERGE_COMPLETE, ExecutionPhase.COMPUTATION, "Merge Place Remaining", "Copying remaining from right.", left, right, mid, k-1, -1, -1));
        }
    }

    private ExecutionState createState(Map<String, RecursionTreeNode> nodes, String activeNodeId, List<StackFrame> stack, int[] arr, OperationType opType, ExecutionPhase phase, String stepTitle, String note, int left, int right, int mid) {
        return createStateWithMerge(nodes, activeNodeId, stack, arr, opType, phase, stepTitle, note, left, right, mid, -1, -1, -1);
    }

    private ExecutionState createStateWithMerge(Map<String, RecursionTreeNode> nodes, String activeNodeId, List<StackFrame> stack, int[] arr, OperationType opType, ExecutionPhase phase, String stepTitle, String note, int left, int right, int mid, int mergeIndex, int compareI, int compareJ) {
        List<StackFrame> stackSnapshot = new ArrayList<>();
        for (StackFrame f : stack) {
            stackSnapshot.add(StackFrame.builder()
                    .functionName(f.getFunctionName())
                    .parameters(new HashMap<>(f.getParameters()))
                    .localVariables(new HashMap<>(f.getLocalVariables()))
                    .depth(f.getDepth())
                    .build());
        }

        Map<String, RecursionTreeNode> treeSnapshot = new LinkedHashMap<>();
        for (Map.Entry<String, RecursionTreeNode> entry : nodes.entrySet()) {
            RecursionTreeNode n = entry.getValue();
            treeSnapshot.put(entry.getKey(), RecursionTreeNode.builder()
                    .nodeId(n.getNodeId())
                    .parentId(n.getParentId())
                    .depth(n.getDepth())
                    .startIndex(n.getStartIndex())
                    .endIndex(n.getEndIndex())
                    .subArray(n.getSubArray().clone())
                    .operationType(n.getOperationType())
                    .executionStatus(n.getExecutionStatus())
                    .explanation(n.getExplanation())
                    .build());
        }

        Map<String, Integer> variables = new HashMap<>();
        variables.put("left", left);
        variables.put("right", right);
        if (mid != -1) variables.put("mid", mid);
        if (mergeIndex != -1) variables.put("mergeIndex", mergeIndex);
        if (compareI != -1) variables.put("compareI", compareI);
        if (compareJ != -1) variables.put("compareJ", compareJ);
        variables.put("recursionDepth", stack.size());

        List<Integer> highlightedIndices = new ArrayList<>();
        if (compareI != -1) highlightedIndices.add(compareI);
        if (compareJ != -1) highlightedIndices.add(compareJ);
        if (mergeIndex != -1) highlightedIndices.add(mergeIndex);
        
        if (highlightedIndices.isEmpty() && left != -1 && right != -1) {
            for (int x = left; x <= right; x++) {
                highlightedIndices.add(x);
            }
        }

        return ExecutionState.builder()
                .step(stepCount++)
                .lineNumber(0)
                .currentLine(0)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O(N log N)")
                .spaceComplexity("O(N)")
                .array(arr.clone())
                .highlightedIndices(highlightedIndices)
                .variables(variables)
                .visualizationType(VisualizationType.TREE)
                .mergeTree(treeSnapshot)
                .activeNodeId(activeNodeId)
                .callStack(stackSnapshot)
                .message(note)
                .build();
    }
}
