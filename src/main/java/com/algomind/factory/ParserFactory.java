package com.algomind.factory;

import com.algomind.exception.AlgorithmNotSupportedException;
import com.algomind.model.ParsedAlgorithm;
import com.algomind.parser.AlgorithmParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParserFactory {

    private final List<AlgorithmParser> parsers;

    @Autowired
    public ParserFactory(List<AlgorithmParser> parsers) {
        this.parsers = parsers;
    }

    public ParsedAlgorithm parseCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new AlgorithmNotSupportedException("Code cannot be empty.");
        }

        for (AlgorithmParser parser : parsers) {
            if (parser.supports(code)) {
                return parser.parse(code);
            }
        }

        // Fallback to Dynamic Execution if it doesn't match predefined simple patterns (like bubbleSort(arr))
        return ParsedAlgorithm.builder()
                .algorithmName("Dynamic Execution")
                .build();
    }
}
