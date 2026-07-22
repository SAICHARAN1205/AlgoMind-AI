package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.execution.analyzer.ASTAnalyzer;
import com.algomind.execution.interpreter.SafeExecutionInterpreter;
import com.algomind.execution.tracer.ExecutionTracer;
import com.algomind.execution.tracer.VariableTracker;
import com.algomind.execution.validator.ExecutionValidator;
import com.algomind.model.ExecutionState;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicCodeSimulator implements AlgorithmSimulator {

    private final ExecutionValidator validator;
    private final SafeExecutionInterpreter interpreter;

    public DynamicCodeSimulator(ExecutionValidator validator, SafeExecutionInterpreter interpreter) {
        this.validator = validator;
        this.interpreter = interpreter;
    }

    @Override
    public String getAlgorithmName() {
        return "Dynamic Execution";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        String code = context.getRawCode();
        
        // 1. Validate
        validator.validateSafeCode(code);
        
        // 2. Setup Trackers
        VariableTracker tracker = new VariableTracker(context.getArray());
        ExecutionTracer tracer = new ExecutionTracer();
        
        // 3. Interpret dynamically
        interpreter.interpret(code, tracker, tracer);
        
        return tracer.getStates();
    }
}
