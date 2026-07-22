package com.algomind.parser;

import com.algomind.model.ParsedAlgorithm;

public interface AlgorithmParser {
    
    /**
     * Checks if this parser supports the given code snippet.
     */
    boolean supports(String code);

    /**
     * Parses the code and extracts relevant algorithm metadata.
     */
    ParsedAlgorithm parse(String code);
}
