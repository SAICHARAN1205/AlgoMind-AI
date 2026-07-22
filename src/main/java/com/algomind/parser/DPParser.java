package com.algomind.parser;

import com.algomind.model.ParsedAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DPParser implements AlgorithmParser {

    private static final List<String> SUPPORTED = Arrays.asList("fibonaccidp", "knapsackdp", "lcsdp");

    @Override
    public boolean supports(String code) {
        String cleanCode = code.replaceAll("\\s+", "").toLowerCase();
        for (String alg : SUPPORTED) {
            if (cleanCode.startsWith(alg)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ParsedAlgorithm parse(String code) {
        String cleanCode = code.replaceAll("\\s+", "").toLowerCase();
        
        if (cleanCode.startsWith("fibonaccidp")) {
            return ParsedAlgorithm.builder().algorithmName("fibonacciDP").build();
        } else if (cleanCode.startsWith("knapsackdp")) {
            return ParsedAlgorithm.builder().algorithmName("knapsackDP").build();
        } else if (cleanCode.startsWith("lcsdp")) {
            return ParsedAlgorithm.builder().algorithmName("lcsDP").build();
        }
        
        return null;
    }
}
