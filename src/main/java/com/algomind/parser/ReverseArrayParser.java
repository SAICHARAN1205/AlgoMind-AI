package com.algomind.parser;

import com.algomind.model.ParsedAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ReverseArrayParser implements AlgorithmParser {

    @Override
    public boolean supports(String code) {
        return code != null && code.trim().startsWith("reverseArray");
    }

    @Override
    public ParsedAlgorithm parse(String code) {
        return ParsedAlgorithm.builder()
                .algorithmName("reverseArray")
                .extractedVariables(Collections.emptyList()) // Simplified for Phase 3
                .build();
    }
}
