package com.algomind.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeAnalysisEngine {

    private final AlgorithmPatternMatcher patternMatcher;

    @Autowired
    public CodeAnalysisEngine(AlgorithmPatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }

    public DetectionResult detectAlgorithmWithConfidence(String code) {
        return patternMatcher.detect(code);
    }

    public String detectAlgorithm(String code) {
        DetectionResult result = patternMatcher.detect(code);
        return result.getConfidence() >= 0.5 ? result.getAlgorithm() : "UNKNOWN";
    }
}
