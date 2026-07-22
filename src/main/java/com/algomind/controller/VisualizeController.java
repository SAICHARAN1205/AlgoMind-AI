package com.algomind.controller;

import com.algomind.dto.CodeVisualizeRequest;
import com.algomind.dto.CodeVisualizeResponse;
import com.algomind.dto.SimulationContext;
import com.algomind.engine.CodeAnalysisEngine;
import com.algomind.engine.DetectionResult;
import com.algomind.model.ExecutionState;
import com.algomind.model.VisualizationType;
import com.algomind.simulator.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/visualize")
@CrossOrigin(origins = "*")
public class VisualizeController {

    private final CodeAnalysisEngine codeAnalysisEngine;
    private final BubbleSortSimulator bubbleSortSimulator;
    private final BinarySearchSimulator binarySearchSimulator;
    private final MergeSortSimulator mergeSortSimulator;
    private final FibonacciDPSimulator fibonacciDPSimulator;
    private final BFSSimulator bfsSimulator;
    private final DFSSimulator dfsSimulator;
    private final DijkstraSimulator dijkstraSimulator;

    private final SelectionSortSimulator selectionSortSimulator;
    private final InsertionSortSimulator insertionSortSimulator;

    public VisualizeController(CodeAnalysisEngine codeAnalysisEngine, BubbleSortSimulator bubbleSortSimulator, SelectionSortSimulator selectionSortSimulator, InsertionSortSimulator insertionSortSimulator, BinarySearchSimulator binarySearchSimulator, MergeSortSimulator mergeSortSimulator, FibonacciDPSimulator fibonacciDPSimulator, BFSSimulator bfsSimulator, DFSSimulator dfsSimulator, DijkstraSimulator dijkstraSimulator) {
        this.codeAnalysisEngine = codeAnalysisEngine;
        this.bubbleSortSimulator = bubbleSortSimulator;
        this.selectionSortSimulator = selectionSortSimulator;
        this.insertionSortSimulator = insertionSortSimulator;
        this.binarySearchSimulator = binarySearchSimulator;
        this.mergeSortSimulator = mergeSortSimulator;
        this.fibonacciDPSimulator = fibonacciDPSimulator;
        this.bfsSimulator = bfsSimulator;
        this.dfsSimulator = dfsSimulator;
        this.dijkstraSimulator = dijkstraSimulator;
    }

    @PostMapping
    public ResponseEntity<?> visualizeCode(@RequestBody CodeVisualizeRequest request) {
        String detectedAlgorithm;
        double confidence = 1.0;
        boolean lowConfidence = false;

        if (request.getManualAlgorithm() != null && !request.getManualAlgorithm().isEmpty() && !request.getManualAlgorithm().equalsIgnoreCase("AUTO")) {
            detectedAlgorithm = request.getManualAlgorithm();
        } else {
            DetectionResult result = codeAnalysisEngine.detectAlgorithmWithConfidence(request.getCode());
            detectedAlgorithm = result.getAlgorithm();
            confidence = result.getConfidence();
            if (confidence < 0.5 && !"UNKNOWN".equals(detectedAlgorithm)) {
                lowConfidence = true;
            } else if ("UNKNOWN".equals(detectedAlgorithm)) {
                return ResponseEntity.badRequest().body(Map.of("message", "AlgoMind currently supports beginner-level DSA patterns only. Could not detect a supported algorithm."));
            }
        }

        SimulationContext context = SimulationContext.builder().build();
        List<ExecutionState> states = null;
        VisualizationType type = VisualizationType.ARRAY;
        String explanation = "Algorithm detected!";

        switch (detectedAlgorithm) {
            case "bubble-sort":
                context = SimulationContext.builder().array(new int[]{8, 3, 5, 1, 9, 6}).build();
                states = bubbleSortSimulator.simulate(context);
                type = VisualizationType.ARRAY;
                explanation = "Nested loops and swaps detected. Visualizing as Bubble Sort.";
                break;
            case "selection-sort":
                context = SimulationContext.builder().array(new int[]{8, 3, 5, 1, 9, 6}).build();
                states = selectionSortSimulator.simulate(context);
                type = VisualizationType.ARRAY;
                explanation = "Minimum element finding and swapping detected. Visualizing as Selection Sort.";
                break;
            case "insertion-sort":
                context = SimulationContext.builder().array(new int[]{8, 3, 5, 1, 9, 6}).build();
                states = insertionSortSimulator.simulate(context);
                type = VisualizationType.ARRAY;
                explanation = "Element shifting and backward traversal detected. Visualizing as Insertion Sort.";
                break;
            case "binary-search":
                context = SimulationContext.builder().array(new int[]{1, 3, 5, 6, 8, 9}).target(8).build();
                states = binarySearchSimulator.simulate(context);
                type = VisualizationType.ARRAY;
                explanation = "Low/High pointers and mid calculations detected. Visualizing as Binary Search.";
                break;
            case "merge-sort":
                context = SimulationContext.builder().array(new int[]{8, 3, 5, 1, 9, 6}).build();
                states = mergeSortSimulator.simulate(context);
                type = VisualizationType.TREE;
                explanation = "Recursive mid splits detected. Visualizing as Merge Sort.";
                break;
            case "fibonacci-dp":
                context = SimulationContext.builder().target(6).build();
                states = fibonacciDPSimulator.simulate(context);
                type = VisualizationType.GRID; // or TREE based on internal state, but GRID covers the full DP
                explanation = "Recursive subproblems or DP array pattern detected. Visualizing as Fibonacci DP.";
                break;
            case "bfs":
                context = SimulationContext.builder().build();
                states = bfsSimulator.simulate(context);
                type = VisualizationType.GRAPH;
                explanation = "Queue usage detected. Visualizing as Breadth-First Search.";
                break;
            case "dfs":
                context = SimulationContext.builder().build();
                states = dfsSimulator.simulate(context);
                type = VisualizationType.GRAPH;
                explanation = "Recursion/Stack usage detected. Visualizing as Depth-First Search.";
                break;
            case "dijkstra":
                context = SimulationContext.builder().build();
                states = dijkstraSimulator.simulate(context);
                type = VisualizationType.GRAPH;
                explanation = "Priority Queue usage detected. Visualizing as Dijkstra's Algorithm.";
                break;
        }

        CodeVisualizeResponse response = CodeVisualizeResponse.builder()
                .detectedAlgorithm(detectedAlgorithm)
                .visualizationType(type)
                .explanation(explanation)
                .confidence(confidence)
                .lowConfidence(lowConfidence)
                .states(states)
                .build();

        return ResponseEntity.ok(response);
    }
}
