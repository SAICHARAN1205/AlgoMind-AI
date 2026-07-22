package com.algomind.engine;

import com.algomind.dto.ExecuteRequest;
import com.algomind.exception.AlgorithmNotSupportedException;
import com.algomind.explanation.ExplanationEngine;
import com.algomind.factory.ParserFactory;
import com.algomind.parser.BubbleSortParser;
import com.algomind.parser.ReverseArrayParser;
import com.algomind.simulator.AlgorithmSimulator;
import com.algomind.simulator.BubbleSortSimulator;
import com.algomind.simulator.ReverseArraySimulator;
import com.algomind.timeline.ExecutionTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionEngineTest {

    private ExecutionEngine engine;

    @BeforeEach
    void setUp() {
        List<AlgorithmSimulator> simulators = List.of(
            new ReverseArraySimulator(),
            new BubbleSortSimulator()
        );
        ParserFactory parserFactory = new ParserFactory(List.of(
            new ReverseArrayParser(),
            new BubbleSortParser()
        ));
        ExplanationEngine explanationEngine = new ExplanationEngine();
        
        com.algomind.execution.analyzer.ASTAnalyzer astAnalyzer = new com.algomind.execution.analyzer.ASTAnalyzer();
        com.algomind.explanation.CodeAnalysisEngine codeAnalysisEngine = new com.algomind.explanation.CodeAnalysisEngine();
        
        engine = new ExecutionEngine(simulators, parserFactory, explanationEngine, astAnalyzer, codeAnalysisEngine);
    }

    @Test
    void testExecuteWithAlgorithmName() {
        ExecuteRequest request = new ExecuteRequest();
        request.setAlgorithm("reverseArray");
        request.setArray(new int[]{1, 2, 3});
        
        ExecutionTimeline timeline = engine.execute(request);
        assertNotNull(timeline);
        assertEquals("reverseArray", timeline.getAlgorithmName());
        assertFalse(timeline.getExecutionStates().isEmpty());
    }

    @Test
    void testExecuteWithParsedCode() {
        ExecuteRequest request = new ExecuteRequest();
        request.setCode("bubbleSort(arr)");
        request.setArray(new int[]{3, 1, 2});
        
        ExecutionTimeline timeline = engine.execute(request);
        assertNotNull(timeline);
        assertEquals("bubbleSort", timeline.getAlgorithmName());
        assertTrue(timeline.getEstimatedComplexity().contains("O(N²)"));
    }

    @Test
    void testExecuteUnsupportedCode() {
        ExecuteRequest request = new ExecuteRequest();
        request.setCode("quickSort(arr)");
        request.setArray(new int[]{1, 2, 3});
        
        assertThrows(AlgorithmNotSupportedException.class, () -> engine.execute(request));
    }
}
