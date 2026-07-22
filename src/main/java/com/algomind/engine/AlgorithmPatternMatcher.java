package com.algomind.engine;

import org.springframework.stereotype.Component;

@Component
public class AlgorithmPatternMatcher {

    public DetectionResult detect(String code) {
        if (code == null || code.trim().isEmpty()) {
            return new DetectionResult("UNKNOWN", 0.0);
        }

        String lowerCode = code.toLowerCase().replaceAll("\\s+", " ");
        
        DetectionResult bestMatch = new DetectionResult("UNKNOWN", 0.0);
        
        // 1. Selection Sort Heuristics
        double selectionScore = 0.0;
        if (lowerCode.contains("min_idx") || lowerCode.contains("minindex") || lowerCode.contains("min ")) selectionScore += 0.4;
        if (lowerCode.contains("for") && lowerCode.contains("j = i + 1")) selectionScore += 0.3;
        if (lowerCode.contains("arr[j] < arr[min") || lowerCode.contains("arr[j] < arr[minindex]")) selectionScore += 0.3;
        if (selectionScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("selection-sort", selectionScore);

        // 2. Insertion Sort Heuristics
        double insertionScore = 0.0;
        if (lowerCode.contains("key = arr[i]") || lowerCode.contains("key =")) insertionScore += 0.3;
        if (lowerCode.contains("while") && (lowerCode.contains("j >= 0") || lowerCode.contains("j > 0"))) insertionScore += 0.3;
        if (lowerCode.contains("arr[j + 1] = arr[j]") || lowerCode.contains("arr[j+1] = arr[j]")) insertionScore += 0.4;
        if (insertionScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("insertion-sort", insertionScore);

        // 3. Bubble Sort Heuristics
        double bubbleScore = 0.0;
        if (lowerCode.contains("for") && (lowerCode.contains("n - i - 1") || lowerCode.contains("n-i-1"))) bubbleScore += 0.3;
        if (lowerCode.contains("arr[j] > arr[j+1]") || lowerCode.contains("arr[j] > arr[j + 1]")) bubbleScore += 0.4;
        if (lowerCode.contains("swap") || lowerCode.contains("temp = arr[j]")) bubbleScore += 0.2;
        if (bubbleScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("bubble-sort", bubbleScore);

        // 4. Merge Sort Heuristics
        double mergeScore = 0.0;
        if (lowerCode.contains("merge(") || lowerCode.contains("mergesort")) mergeScore += 0.4;
        if (lowerCode.contains("mid =") && (lowerCode.contains("left + (right") || lowerCode.contains("l + (r"))) mergeScore += 0.2;
        if (lowerCode.contains("sort(") && lowerCode.contains("mid")) mergeScore += 0.3;
        if (mergeScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("merge-sort", mergeScore);

        // 5. Binary Search Heuristics
        double binaryScore = 0.0;
        if (lowerCode.contains("while") && (lowerCode.contains("low <= high") || lowerCode.contains("left <= right") || lowerCode.contains("l <= r"))) binaryScore += 0.4;
        if (lowerCode.contains("mid =") || lowerCode.contains("mid=")) binaryScore += 0.3;
        if (lowerCode.contains("arr[mid] ==") || lowerCode.contains("arr[mid] <")) binaryScore += 0.3;
        if (binaryScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("binary-search", binaryScore);

        // 6. BFS
        double bfsScore = 0.0;
        if (lowerCode.contains("queue") || lowerCode.contains("linkedlist")) bfsScore += 0.4;
        if (lowerCode.contains("poll()") || lowerCode.contains("dequeue")) bfsScore += 0.3;
        if (lowerCode.contains("visited") && lowerCode.contains("add")) bfsScore += 0.2;
        if (bfsScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("bfs", bfsScore);

        // 7. DFS
        double dfsScore = 0.0;
        if (lowerCode.contains("dfs(") || lowerCode.contains("stack")) dfsScore += 0.4;
        if (lowerCode.contains("visited") && lowerCode.contains("recursion")) dfsScore += 0.3;
        if (lowerCode.contains("push(") || lowerCode.contains("pop()")) dfsScore += 0.2;
        if (dfsScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("dfs", dfsScore);

        // 8. Dijkstra
        double dijkstraScore = 0.0;
        if (lowerCode.contains("priorityqueue") || lowerCode.contains("dijkstra")) dijkstraScore += 0.5;
        if (lowerCode.contains("distance[") || lowerCode.contains("dist[")) dijkstraScore += 0.3;
        if (lowerCode.contains("poll()") && lowerCode.contains("offer(")) dijkstraScore += 0.1;
        if (dijkstraScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("dijkstra", dijkstraScore);

        // 9. Fibonacci DP
        double fibScore = 0.0;
        if (lowerCode.contains("fib") || lowerCode.contains("fibonacci")) fibScore += 0.5;
        if (lowerCode.contains("dp[i-1] + dp[i-2]") || lowerCode.contains("dp[i - 1] + dp[i - 2]")) fibScore += 0.4;
        if (fibScore > bestMatch.getConfidence()) bestMatch = new DetectionResult("fibonacci-dp", fibScore);

        return bestMatch;
    }
}
