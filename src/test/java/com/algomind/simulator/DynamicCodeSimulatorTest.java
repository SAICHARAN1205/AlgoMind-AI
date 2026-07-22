package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.execution.interpreter.SafeExecutionInterpreter;
import com.algomind.execution.validator.ExecutionValidator;
import com.algomind.model.ExecutionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynamicCodeSimulatorTest {

    private DynamicCodeSimulator simulator;

    @BeforeEach
    void setUp() {
        ExecutionValidator validator = new ExecutionValidator();
        SafeExecutionInterpreter interpreter = new SafeExecutionInterpreter();
        simulator = new DynamicCodeSimulator(validator, interpreter);
    }

    @Test
    void testDynamicSimulationWithSimpleLoop() {
        String code = "int i = 0; while(i < 3) { i++; }";
        
        SimulationContext context = SimulationContext.builder()
                .rawCode(code)
                .array(new int[]{1, 2, 3})
                .build();
                
        List<ExecutionState> states = simulator.simulate(context);
        
        assertFalse(states.isEmpty());
        // Last state should have i = 3
        ExecutionState lastState = states.get(states.size() - 1);
        assertEquals(3, lastState.getVariables().get("i"));
    }

    @Test
    void testDynamicSimulationWithArraySwap() {
        String code = "int temp = arr[0]; arr[0] = arr[1]; arr[1] = temp;";
        
        SimulationContext context = SimulationContext.builder()
                .rawCode(code)
                .array(new int[]{10, 20})
                .build();
                
        List<ExecutionState> states = simulator.simulate(context);
        
        assertFalse(states.isEmpty());
        ExecutionState lastState = states.get(states.size() - 1);
        
        // Array should be swapped
        assertArrayEquals(new int[]{20, 10}, lastState.getArray());
        assertEquals(10, lastState.getVariables().get("temp"));
    }
}
