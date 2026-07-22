package com.algomind.explanation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExplanationEngineTest {

    private ExplanationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ExplanationEngine();
    }

    @Test
    void testExplainComplexity() {
        String bubbleSortComplexity = engine.explainComplexity("bubbleSort");
        assertTrue(bubbleSortComplexity.contains("O(N²)"));
        
        String binarySearchComplexity = engine.explainComplexity("binarySearch");
        assertTrue(binarySearchComplexity.contains("O(log N)"));
    }

    @Test
    void testGenerateInsights() {
        Map<String, String> insights = engine.generateInsights("binarySearch");
        assertNotNull(insights);
        assertTrue(insights.containsKey("Key Idea"));
        assertTrue(insights.containsKey("Prerequisite"));
    }
}
