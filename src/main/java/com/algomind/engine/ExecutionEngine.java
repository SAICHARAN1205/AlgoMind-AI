package com.algomind.engine;

import com.algomind.dto.ExecuteRequest;
import com.algomind.dto.SimulationContext;
import com.algomind.exception.AlgorithmNotSupportedException;
import com.algomind.exception.InvalidInputException;
import com.algomind.explanation.ExplanationEngine;
import com.algomind.factory.ParserFactory;
import com.algomind.model.ExecutionState;
import com.algomind.model.ParsedAlgorithm;
import com.algomind.simulator.AlgorithmSimulator;
import com.algomind.timeline.ExecutionTimeline;
import com.algomind.model.VisualizationType;
import com.algomind.execution.analyzer.ASTAnalyzer;
import com.algomind.execution.ast.ASTNodeInfo;
import com.algomind.explanation.CodeAnalysisEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExecutionEngine {

    private final Map<String, AlgorithmSimulator> simulatorMap;
    private final ParserFactory parserFactory;
    private final ExplanationEngine explanationEngine;

    private final ASTAnalyzer astAnalyzer;
    private final CodeAnalysisEngine codeAnalysisEngine;

    @Autowired
    public ExecutionEngine(List<AlgorithmSimulator> simulators, 
                           ParserFactory parserFactory, 
                           ExplanationEngine explanationEngine,
                           ASTAnalyzer astAnalyzer,
                           CodeAnalysisEngine codeAnalysisEngine) {
        this.simulatorMap = simulators.stream()
                .collect(Collectors.toMap(AlgorithmSimulator::getAlgorithmName, Function.identity()));
        this.parserFactory = parserFactory;
        this.explanationEngine = explanationEngine;
        this.astAnalyzer = astAnalyzer;
        this.codeAnalysisEngine = codeAnalysisEngine;
    }

    public ExecutionTimeline execute(ExecuteRequest request) {
        if (!request.isValid()) {
            throw new InvalidInputException("You must provide either an algorithm name or a code snippet.");
        }

        String algorithmName = request.getAlgorithm();
        ParsedAlgorithm parsedAlg = null;
        Integer target = request.getTarget();

        // If code is provided, parse it
        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            parsedAlg = parserFactory.parseCode(request.getCode());
            algorithmName = parsedAlg.getAlgorithmName();
            if (parsedAlg.getTargetValue() != null) {
                target = parsedAlg.getTargetValue();
            }
        }

        AlgorithmSimulator simulator = simulatorMap.get(algorithmName);
        if (simulator == null) {
            throw new AlgorithmNotSupportedException("Algorithm '" + algorithmName + "' is not supported.");
        }
        
        SimulationContext context = SimulationContext.builder()
                .array(request.getArray())
                .target(target)
                .rawCode(request.getCode())
                .parsedAlgorithm(parsedAlg)
                .build();
                
        List<ExecutionState> states = simulator.simulate(context);
        
        String estimatedComplexity = explanationEngine.explainComplexity(algorithmName);
        Map<String, String> educationalInsights = explanationEngine.generateInsights(algorithmName);

        if ("Dynamic Execution".equals(algorithmName) && request.getCode() != null) {
            try {
                ASTNodeInfo astRoot = astAnalyzer.analyze(request.getCode());
                estimatedComplexity = codeAnalysisEngine.estimateComplexity(astRoot);
                educationalInsights = codeAnalysisEngine.generateInsights(astRoot);
            } catch (Exception e) {
                // Ignore parsing errors for insights to not fail the execution if parsing fails
                educationalInsights.put("Analysis Error", "Could not analyze code structure for insights.");
            }
        }
        
        return ExecutionTimeline.builder()
                .algorithmName(algorithmName)
                .totalSteps(states.size())
                .executionStates(states)
                .executionSummary(algorithmName + " completed in " + states.size() + " steps.")
                .estimatedComplexity(estimatedComplexity)
                .educationalInsights(educationalInsights)
                .visualizationType(states.isEmpty() || states.get(0).getVisualizationType() == null ? VisualizationType.ARRAY : states.get(0).getVisualizationType())
                .build();
    }
}
