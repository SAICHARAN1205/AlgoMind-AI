package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.exception.InvalidInputException;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchSimulatorTest {

    private BinarySearchSimulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new BinarySearchSimulator();
    }

    @Test
    void testAlgorithmName() {
        assertEquals("binarySearch", simulator.getAlgorithmName());
    }

    @Test
    void testTargetFound() {
        int[] input = {1, 3, 5, 7, 9};
        SimulationContext context = SimulationContext.builder().array(input).target(7).build();
        
        List<ExecutionState> states = simulator.simulate(context);
        assertNotNull(states);
        
        ExecutionState finalState = states.get(states.size() - 1);
        assertEquals(OperationType.FOUND, finalState.getOperationType());
        assertTrue(finalState.getHighlightedIndices().contains(3)); // index of 7
    }

    @Test
    void testTargetNotFound() {
        int[] input = {1, 3, 5, 7, 9};
        SimulationContext context = SimulationContext.builder().array(input).target(4).build();
        
        List<ExecutionState> states = simulator.simulate(context);
        
        ExecutionState finalState = states.get(states.size() - 1);
        assertEquals(OperationType.COMPLETE, finalState.getOperationType());
        assertEquals("Target not found", finalState.getMessage());
    }
    
    @Test
    void testMissingTargetThrowsException() {
        int[] input = {1, 2, 3};
        SimulationContext context = SimulationContext.builder().array(input).build();
        
        assertThrows(InvalidInputException.class, () -> simulator.simulate(context));
    }
}
