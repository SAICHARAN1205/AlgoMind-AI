package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReverseArraySimulatorTest {

    private ReverseArraySimulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new ReverseArraySimulator();
    }

    @Test
    void testAlgorithmName() {
        assertEquals("reverseArray", simulator.getAlgorithmName());
    }

    @Test
    void testSimulateOddLengthArray() {
        int[] input = {1, 2, 3, 4, 5};
        SimulationContext context = SimulationContext.builder().array(input).build();
        List<ExecutionState> states = simulator.simulate(context);
        
        assertNotNull(states);
        assertFalse(states.isEmpty());
        
        // Final state should have the reversed array
        ExecutionState finalState = states.get(states.size() - 1);
        assertArrayEquals(new int[]{5, 4, 3, 2, 1}, finalState.getArray());
        
        // Initial state should have original array
        ExecutionState initialState = states.get(0);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, initialState.getArray());
        
        // Check deep copy - modifying returned array should not affect next state
        initialState.getArray()[0] = 99;
        ExecutionState secondState = states.get(1);
        assertEquals(1, secondState.getArray()[0]);
    }

    @Test
    void testStateMetadata() {
        int[] input = {10, 20};
        SimulationContext context = SimulationContext.builder().array(input).build();
        List<ExecutionState> states = simulator.simulate(context);
        
        // Ensure new Phase 2 fields are populated
        ExecutionState state1 = states.get(0);
        assertNotNull(state1.getOperationType());
        assertNotNull(state1.getHighlightedIndices());
        assertEquals(0, state1.getVariables().get("i"));
        assertEquals(1, state1.getVariables().get("j"));
    }
}
