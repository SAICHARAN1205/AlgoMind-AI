package com.algomind.parser;

import com.algomind.model.ParsedAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class BubbleSortParser implements AlgorithmParser {

    @Override
    public boolean supports(String code) {
        return code != null && code.trim().startsWith("bubbleSort");
    }

    @Override
    public ParsedAlgorithm parse(String code) {
        return ParsedAlgorithm.builder()
                .algorithmName("bubbleSort")
                .extractedVariables(Collections.emptyList()) // Simplified for Phase 3
                .build();
    }
}
