package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.dp.DPTableState;
import com.algomind.model.recursion.ExecutionStatus;
import com.algomind.model.recursion.RecursionTreeNode;
import com.algomind.model.recursion.StackFrame;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FibonacciDPSimulator implements AlgorithmSimulator {

    private int stepCount = 1;
    private int nodeCounter = 1;
    
    // Simulates recursive, memoized, and bottom-up DP sequentially
    @Override
    public String getAlgorithmName() {
        return "fibonacci-dp";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        int n = context.getTarget() != null ? context.getTarget() : 6;
        
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        
        // 1. Recursive Simulation
        simulateRecursivePhase(n, states);
        
        // 2. Memoized Simulation
        simulateMemoizedPhase(n, states);
        
        // 3. Bottom-Up Simulation
        simulateBottomUpPhase(n, states);
        
        return states;
    }
    
    // --- PHASE 1: RECURSIVE ---
    private void simulateRecursivePhase(int n, List<ExecutionState> states) {
        nodeCounter = 1;
        List<StackFrame> stack = new ArrayList<>();
        Map<String, RecursionTreeNode> nodes = new LinkedHashMap<>();
        
        executeRecursive(n, null, stack, nodes, states);
    }
    
    private int executeRecursive(int n, String parentId, List<StackFrame> stack, Map<String, RecursionTreeNode> nodes, List<ExecutionState> states) {
        String nodeId = "rec-node-" + nodeCounter++;
        int depth = stack.size() + 1;

        RecursionTreeNode node = RecursionTreeNode.builder()
                .nodeId(nodeId)
                .parentId(parentId)
                .depth(depth)
                .operationType(OperationType.RECURSIVE_CALL)
                .executionStatus(ExecutionStatus.CREATED)
                .explanation("Calculating fib(" + n + ") recursively.")
                .build();
        nodes.put(nodeId, node);
        
        StackFrame frame = StackFrame.builder()
                .functionName("fib")
                .parameters(Map.of("n", String.valueOf(n)))
                .localVariables(new HashMap<>())
                .depth(depth)
                .build();
        stack.add(frame);
        
        node.setExecutionStatus(ExecutionStatus.EXECUTING);
        states.add(createTreeState(nodes, nodeId, stack, OperationType.RECURSIVE_CALL, "RECURSIVE", "Recursive Call", node.getExplanation(), n, -1));
        
        int result;
        if (n <= 1) {
            result = n;
            node.setOperationType(OperationType.BASE_CASE);
            node.setExplanation("Base case reached: fib(" + n + ") = " + n);
            states.add(createTreeState(nodes, nodeId, stack, OperationType.BASE_CASE, "RECURSIVE", "Base Case", node.getExplanation(), n, -1));
        } else {
            node.setOperationType(OperationType.SPLIT);
            node.setExplanation("Branching to fib(" + (n - 1) + ") and fib(" + (n - 2) + ").");
            states.add(createTreeState(nodes, nodeId, stack, OperationType.SPLIT, "RECURSIVE", "Branching", node.getExplanation(), n, -1));
            
            int left = executeRecursive(n - 1, nodeId, stack, nodes, states);
            int right = executeRecursive(n - 2, nodeId, stack, nodes, states);
            
            result = left + right;
            node.setOperationType(OperationType.VALUE_COMPUTED);
            node.setExplanation("fib(" + n + ") = " + left + " + " + right + " = " + result);
            states.add(createTreeState(nodes, nodeId, stack, OperationType.VALUE_COMPUTED, "RECURSIVE", "Result Computed", node.getExplanation(), n, result));
        }
        
        node.setOperationType(OperationType.RETURN);
        node.setExecutionStatus(ExecutionStatus.RETURNED);
        stack.remove(stack.size() - 1);
        states.add(createTreeState(nodes, nodeId, stack, OperationType.RETURN, "RECURSIVE", "Return", "Returning " + result, n, result));
        
        return result;
    }

    // --- PHASE 2: MEMOIZED ---
    private void simulateMemoizedPhase(int n, List<ExecutionState> states) {
        nodeCounter = 1;
        List<StackFrame> stack = new ArrayList<>();
        Map<String, RecursionTreeNode> nodes = new LinkedHashMap<>();
        Map<Integer, Integer> cache = new HashMap<>();
        
        executeMemoized(n, null, stack, nodes, states, cache);
    }
    
    private int executeMemoized(int n, String parentId, List<StackFrame> stack, Map<String, RecursionTreeNode> nodes, List<ExecutionState> states, Map<Integer, Integer> cache) {
        String nodeId = "mem-node-" + nodeCounter++;
        int depth = stack.size() + 1;

        RecursionTreeNode node = RecursionTreeNode.builder()
                .nodeId(nodeId)
                .parentId(parentId)
                .depth(depth)
                .operationType(OperationType.RECURSIVE_CALL)
                .executionStatus(ExecutionStatus.CREATED)
                .explanation("Calculating fib(" + n + ").")
                .build();
        nodes.put(nodeId, node);
        
        StackFrame frame = StackFrame.builder()
                .functionName("fibMemo")
                .parameters(Map.of("n", String.valueOf(n)))
                .localVariables(new HashMap<>())
                .depth(depth)
                .build();
        stack.add(frame);
        
        node.setExecutionStatus(ExecutionStatus.EXECUTING);
        states.add(createTreeState(nodes, nodeId, stack, OperationType.RECURSIVE_CALL, "MEMOIZED", "Recursive Call", node.getExplanation(), n, -1));
        
        if (cache.containsKey(n)) {
            int result = cache.get(n);
            node.setOperationType(OperationType.CACHE_HIT);
            node.setExplanation("Cache hit! fib(" + n + ") was already computed earlier (" + result + "), bypassing recursion.");
            states.add(createTreeState(nodes, nodeId, stack, OperationType.CACHE_HIT, "MEMOIZED", "Cache Hit", node.getExplanation(), n, result));
            
            node.setOperationType(OperationType.RETURN);
            node.setExecutionStatus(ExecutionStatus.RETURNED);
            stack.remove(stack.size() - 1);
            states.add(createTreeState(nodes, nodeId, stack, OperationType.RETURN, "MEMOIZED", "Return Cached", "Returning cached " + result, n, result));
            return result;
        }
        
        int result;
        if (n <= 1) {
            result = n;
            node.setOperationType(OperationType.BASE_CASE);
            node.setExplanation("Base case reached: fib(" + n + ") = " + n);
            states.add(createTreeState(nodes, nodeId, stack, OperationType.BASE_CASE, "MEMOIZED", "Base Case", node.getExplanation(), n, -1));
        } else {
            node.setOperationType(OperationType.SPLIT);
            node.setExplanation("Branching to fib(" + (n - 1) + ") and fib(" + (n - 2) + ").");
            states.add(createTreeState(nodes, nodeId, stack, OperationType.SPLIT, "MEMOIZED", "Branching", node.getExplanation(), n, -1));
            
            int left = executeMemoized(n - 1, nodeId, stack, nodes, states, cache);
            int right = executeMemoized(n - 2, nodeId, stack, nodes, states, cache);
            
            result = left + right;
            node.setOperationType(OperationType.VALUE_COMPUTED);
            node.setExplanation("fib(" + n + ") = " + left + " + " + right + " = " + result + ".");
            states.add(createTreeState(nodes, nodeId, stack, OperationType.VALUE_COMPUTED, "MEMOIZED", "Result Computed", node.getExplanation(), n, result));
        }
        
        cache.put(n, result);
        node.setOperationType(OperationType.CACHE_WRITE);
        node.setExplanation("Caching fib(" + n + ") = " + result + " for future use.");
        states.add(createTreeState(nodes, nodeId, stack, OperationType.CACHE_WRITE, "MEMOIZED", "Cache Write", node.getExplanation(), n, result));
        
        node.setOperationType(OperationType.RETURN);
        node.setExecutionStatus(ExecutionStatus.RETURNED);
        stack.remove(stack.size() - 1);
        states.add(createTreeState(nodes, nodeId, stack, OperationType.RETURN, "MEMOIZED", "Return", "Returning " + result, n, result));
        
        return result;
    }

    // --- PHASE 3: BOTTOM-UP ---
    private void simulateBottomUpPhase(int n, List<ExecutionState> states) {
        Integer[] table = new Integer[n + 1];
        Set<Integer> computedIndices = new HashSet<>();
        
        // Base cases
        table[0] = 0;
        computedIndices.add(0);
        states.add(createDPState(table, 0, Collections.emptyList(), computedIndices, OperationType.BASE_CASE, "BOTTOM_UP", "Initialize DP Table", "Setting dp[0] = 0", n, 0));
        
        if (n >= 1) {
            table[1] = 1;
            computedIndices.add(1);
            states.add(createDPState(table, 1, Collections.emptyList(), computedIndices, OperationType.BASE_CASE, "BOTTOM_UP", "Initialize DP Table", "Setting dp[1] = 1", n, 1));
        }
        
        // Iterative tabulation
        for (int i = 2; i <= n; i++) {
            List<Integer> deps = Arrays.asList(i - 1, i - 2);
            states.add(createDPState(table, i, deps, computedIndices, OperationType.DEPENDENCY_ACCESS, "BOTTOM_UP", "Access Dependencies", "To compute dp[" + i + "], we need dp[" + (i-1) + "] and dp[" + (i-2) + "].", n, -1));
            
            table[i] = table[i - 1] + table[i - 2];
            computedIndices.add(i);
            states.add(createDPState(table, i, deps, computedIndices, OperationType.TABLE_UPDATE, "BOTTOM_UP", "Update Table", "dp[" + i + "] = " + table[i-1] + " + " + table[i-2] + " = " + table[i], n, table[i]));
        }
        
        states.add(createDPState(table, n, Collections.emptyList(), computedIndices, OperationType.COMPLETE, "BOTTOM_UP", "Complete", "Bottom-up dynamic programming complete! Result: " + table[n], n, table[n]));
    }

    // --- STATE CREATION HELPERS ---
    private ExecutionState createTreeState(Map<String, RecursionTreeNode> nodes, String activeNodeId, List<StackFrame> stack, OperationType opType, String dpMode, String stepTitle, String note, int n, int val) {
        Map<String, RecursionTreeNode> treeSnapshot = new LinkedHashMap<>();
        for (Map.Entry<String, RecursionTreeNode> entry : nodes.entrySet()) {
            RecursionTreeNode n_node = entry.getValue();
            treeSnapshot.put(entry.getKey(), RecursionTreeNode.builder()
                    .nodeId(n_node.getNodeId())
                    .parentId(n_node.getParentId())
                    .depth(n_node.getDepth())
                    .operationType(n_node.getOperationType())
                    .executionStatus(n_node.getExecutionStatus())
                    .explanation(n_node.getExplanation())
                    .build());
        }
        
        Map<String, Integer> vars = new HashMap<>();
        vars.put("n", n);
        vars.put("recursionDepth", stack.size());
        if (val != -1) vars.put("currentValue", val);

        return ExecutionState.builder()
                .step(stepCount++)
                .dpMode(dpMode)
                .operationType(opType)
                .executionPhase(ExecutionPhase.COMPUTATION)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity(dpMode.equals("RECURSIVE") ? "O(2^N)" : "O(N)")
                .spaceComplexity(dpMode.equals("BOTTOM_UP") ? "O(N)" : "O(N)")
                .variables(vars)
                .visualizationType(VisualizationType.TREE)
                .mergeTree(treeSnapshot)
                .activeNodeId(activeNodeId)
                .message(note)
                .build();
    }
    
    private ExecutionState createDPState(Integer[] table, int activeIndex, List<Integer> deps, Set<Integer> computed, OperationType opType, String dpMode, String stepTitle, String note, int n, int val) {
        DPTableState dpState = DPTableState.builder()
                .table(table.clone())
                .activeIndex(activeIndex)
                .dependencyIndices(new ArrayList<>(deps))
                .computedIndices(new HashSet<>(computed))
                .explanation(note)
                .operationType(opType)
                .build();
                
        Map<String, Integer> vars = new HashMap<>();
        vars.put("n", n);
        vars.put("activeIndex", activeIndex);
        if (val != -1) vars.put("currentValue", val);

        return ExecutionState.builder()
                .step(stepCount++)
                .dpMode(dpMode)
                .operationType(opType)
                .executionPhase(ExecutionPhase.COMPUTATION)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O(N)")
                .spaceComplexity("O(N)")
                .variables(vars)
                .visualizationType(VisualizationType.GRID)
                .dpTableState(dpState)
                .message(note)
                .build();
    }
}
