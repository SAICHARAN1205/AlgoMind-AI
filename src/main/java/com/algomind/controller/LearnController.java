package com.algomind.controller;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionState;
import com.algomind.simulator.BubbleSortSimulator;
import com.algomind.simulator.BinarySearchSimulator;
import com.algomind.simulator.MergeSortSimulator;
import com.algomind.simulator.FibonacciDPSimulator;
import com.algomind.simulator.BFSSimulator;
import com.algomind.simulator.DFSSimulator;
import com.algomind.simulator.DijkstraSimulator;
import com.algomind.simulator.StackSimulator;
import com.algomind.simulator.QueueSimulator;
import com.algomind.simulator.TreeTraversalSimulator;
import com.algomind.simulator.BSTSimulator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/learn")
@CrossOrigin(origins = "*") // For development
public class LearnController {

    private final BubbleSortSimulator bubbleSortSimulator;
    private final BinarySearchSimulator binarySearchSimulator;
    private final MergeSortSimulator mergeSortSimulator;
    private final FibonacciDPSimulator fibonacciDPSimulator;
    private final BFSSimulator bfsSimulator;
    private final DFSSimulator dfsSimulator;
    private final DijkstraSimulator dijkstraSimulator;
    private final StackSimulator stackSimulator;
    private final QueueSimulator queueSimulator;
    private final TreeTraversalSimulator treeTraversalSimulator;
    private final BSTSimulator bstSimulator;

    public LearnController(BubbleSortSimulator bubbleSortSimulator, BinarySearchSimulator binarySearchSimulator, MergeSortSimulator mergeSortSimulator, FibonacciDPSimulator fibonacciDPSimulator, BFSSimulator bfsSimulator, DFSSimulator dfsSimulator, DijkstraSimulator dijkstraSimulator, StackSimulator stackSimulator, QueueSimulator queueSimulator, TreeTraversalSimulator treeTraversalSimulator, BSTSimulator bstSimulator) {
        this.bubbleSortSimulator = bubbleSortSimulator;
        this.binarySearchSimulator = binarySearchSimulator;
        this.mergeSortSimulator = mergeSortSimulator;
        this.fibonacciDPSimulator = fibonacciDPSimulator;
        this.bfsSimulator = bfsSimulator;
        this.dfsSimulator = dfsSimulator;
        this.dijkstraSimulator = dijkstraSimulator;
        this.stackSimulator = stackSimulator;
        this.queueSimulator = queueSimulator;
        this.treeTraversalSimulator = treeTraversalSimulator;
        this.bstSimulator = bstSimulator;
    }

    @GetMapping("/{algorithm}")
    public ResponseEntity<?> getAlgorithmStates(@PathVariable String algorithm) {
        if ("bubble-sort".equalsIgnoreCase(algorithm)) {
            // Default array for Learn Mode
            int[] defaultArray = {34, 12, 45, 9, 88, 23, 56};
            SimulationContext context = SimulationContext.builder()
                .array(defaultArray)
                .build();
            
            List<ExecutionState> states = bubbleSortSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("binary-search".equalsIgnoreCase(algorithm)) {
            // Default sorted array for Binary Search
            int[] defaultArray = {1, 3, 5, 7, 9, 11, 13};
            SimulationContext context = SimulationContext.builder()
                .array(defaultArray)
                .target(11)
                .build();
            
            List<ExecutionState> states = binarySearchSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("merge-sort".equalsIgnoreCase(algorithm)) {
            // Default array for Merge Sort Learn Mode
            int[] defaultArray = {8, 3, 5, 1, 9, 6};
            SimulationContext context = SimulationContext.builder()
                .array(defaultArray)
                .build();
            
            List<ExecutionState> states = mergeSortSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("fibonacci-dp".equalsIgnoreCase(algorithm)) {
            // fib(6) default for Fibonacci DP
            SimulationContext context = SimulationContext.builder()
                .target(6)
                .build();
            
            List<ExecutionState> states = fibonacciDPSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("bfs".equalsIgnoreCase(algorithm)) {
            SimulationContext context = SimulationContext.builder().build();
            List<ExecutionState> states = bfsSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("dfs".equalsIgnoreCase(algorithm)) {
            SimulationContext context = SimulationContext.builder().build();
            List<ExecutionState> states = dfsSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("dijkstra".equalsIgnoreCase(algorithm)) {
            SimulationContext context = SimulationContext.builder().build();
            List<ExecutionState> states = dijkstraSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("stack".equalsIgnoreCase(algorithm)) {
            SimulationContext context = SimulationContext.builder().build();
            List<ExecutionState> states = stackSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if ("queue".equalsIgnoreCase(algorithm)) {
            SimulationContext context = SimulationContext.builder().build();
            List<ExecutionState> states = queueSimulator.simulate(context);
            return ResponseEntity.ok(states);
        } else if (algorithm.startsWith("tree-traversal-")) {
            String type = algorithm.replace("tree-traversal-", "");
            List<ExecutionState> states = treeTraversalSimulator.simulate(type);
            return ResponseEntity.ok(states);
        } else if ("bst".equalsIgnoreCase(algorithm)) {
            List<ExecutionState> states = bstSimulator.simulate();
            return ResponseEntity.ok(states);
        }

        return ResponseEntity.badRequest().body(Map.of("message", "Algorithm not currently supported in Learn Mode."));
    }
}
