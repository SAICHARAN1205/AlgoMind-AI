package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionState;

import java.util.List;

public interface AlgorithmSimulator {
    /**
     * Identifies the algorithm this simulator supports.
     */
    String getAlgorithmName();

    /**
     * Simulates the algorithm based on the context and returns step-by-step states.
     */
    List<ExecutionState> simulate(SimulationContext context);
}
