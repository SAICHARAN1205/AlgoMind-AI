package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionState;
import com.algomind.model.VisualizationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnapsackSimulatorTest {

    @Test
    void testKnapsackDPGridGeneration() {
        KnapsackSimulator simulator = new KnapsackSimulator();
        SimulationContext context = SimulationContext.builder().build(); // Mock inputs used inside
        
        List<ExecutionState> states = simulator.simulate(context);
        
        assertFalse(states.isEmpty());
        
        // Final state should contain the answer
        ExecutionState finalState = states.get(states.size() - 1);
        assertEquals(VisualizationType.GRID, finalState.getVisualizationType());
        
        // For weights={1,2,3}, vals={60,100,120}, cap=5
        // max value is 220 (items 100+120)
        assertNotNull(finalState.getDpTable());
        Integer[][] matrix = finalState.getDpTable().getMatrix();
        assertEquals(220, matrix[3][5]); // n=3 items, cap=5
    }
}
