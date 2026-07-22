package com.algomind.engine.dp;

import java.util.HashMap;
import java.util.Map;

public class MemoizationTracker {
    
    private final Map<String, Integer> cache = new HashMap<>();

    public boolean hasValue(int row, int col) {
        return cache.containsKey(getKey(row, col));
    }

    public Integer getValue(int row, int col) {
        return cache.get(getKey(row, col));
    }

    public void storeValue(int row, int col, int value) {
        cache.put(getKey(row, col), value);
    }

    private String getKey(int row, int col) {
        return row + "," + col;
    }

    public String generateHitExplanation(int row, int col) {
        return "Value for state [" + row + "][" + col + "] already computed earlier. Recursion avoids recalculating this overlapping subproblem.";
    }
}
