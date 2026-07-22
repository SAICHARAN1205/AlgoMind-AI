package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSortSimulatorTest {

    private BubbleSortSimulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new BubbleSortSimulator();
    }

    @Test
    void testAlgorithmName() {
        assertEquals("bubbleSort", simulator.getAlgorithmName());
    }

    @Test
    void testSimulateSorting() {
        int[] input = {5, 3, 8, 4, 2};
        SimulationContext context = SimulationContext.builder().array(input).build();
        List<ExecutionState> states = simulator.simulate(context);
        
        assertNotNull(states);
        assertFalse(states.isEmpty());
        
        ExecutionState finalState = states.get(states.size() - 1);
        assertArrayEquals(new int[]{2, 3, 4, 5, 8}, finalState.getArray());
        assertEquals(OperationType.COMPLETE, finalState.getOperationType());
    }
    
    @Test
    void testAlreadySortedArray() {
        int[] input = {1, 2, 3, 4, 5};
        SimulationContext context = SimulationContext.builder().array(input).build();
        List<ExecutionState> states = simulator.simulate(context);
        
        // Should complete faster since it's already sorted (no swaps = early exit)
        ExecutionState finalState = states.get(states.size() - 1);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, finalState.getArray());
    }
}
