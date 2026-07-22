package com.algomind.explanation;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExplanationEngine {

    public String explainComplexity(String algorithm) {
        return switch (algorithm) {
            case "bubbleSort" -> "O(N²) because nested loops compare almost every pair.";
            case "binarySearch" -> "O(log N) because the search space halves after every comparison.";
            case "reverseArray" -> "O(N) because we iterate through half the array swapping elements.";
            case "fibonacciDP" -> "O(N) because we only compute each state once and reuse it.";
            case "knapsackDP" -> "O(N * W) where N is number of items and W is capacity. We fill a 2D table.";
            case "lcsDP" -> "O(M * N) where M and N are the lengths of the two strings. We fill a 2D table.";
            default -> "Complexity varies based on the algorithm.";
        };
    }

    public Map<String, String> generateInsights(String algorithm) {
        Map<String, String> insights = new HashMap<>();
        switch (algorithm) {
            case "bubbleSort" -> {
                insights.put("Key Idea", "Bubble sort repeatedly pushes largest values right");
                insights.put("Optimization", "If no swaps occur in a pass, the array is already sorted.");
            }
            case "binarySearch" -> {
                insights.put("Key Idea", "Eliminate half of the remaining elements each step.");
                insights.put("Prerequisite", "The array MUST be sorted for binary search to work.");
            }
            case "reverseArray" -> {
                insights.put("Key Idea", "Two pointers move inward after every swap.");
                insights.put("Efficiency", "Done in-place with minimal extra memory.");
            }
            case "fibonacciDP" -> {
                insights.put("Overlapping Subproblems", "Fibonacci recalculates the same values. DP avoids this by caching results.");
                insights.put("Bottom-Up Tabulation", "We solve the smallest subproblems first (base cases) and iteratively build up the solution.");
            }
            case "knapsackDP" -> {
                insights.put("Optimal Substructure", "The optimal solution involves taking the maximum between including an item or excluding it.");
                insights.put("State", "dp[i][w] represents the max value using the first i items with capacity w.");
            }
            case "lcsDP" -> {
                insights.put("Transitions", "If characters match, we add 1 to the diagonal. If they mismatch, we take the max of the cell above or to the left.");
                insights.put("Overlapping Subproblems", "DP avoids recalculating the LCS of the same substring prefixes.");
            }
        }
        return insights;
    }
    
    public String explainOperation(String operation, String context) {
        return switch (operation) {
            case "SWAP" -> "Elements were swapped because " + context;
            case "COMPARE" -> "Comparing elements to determine " + context;
            default -> "Operation performed: " + operation;
        };
    }
}
