package com.algomind.parser;

import com.algomind.model.ParsedAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BinarySearchParser implements AlgorithmParser {

    // Regex to match "binarySearch(arr, 7)" or similar
    private static final Pattern PATTERN = Pattern.compile("binarySearch\\s*\\(\\s*[a-zA-Z0-9_]+\\s*,\\s*(\\d+)\\s*\\)");

    @Override
    public boolean supports(String code) {
        return code != null && code.trim().startsWith("binarySearch");
    }

    @Override
    public ParsedAlgorithm parse(String code) {
        Integer targetValue = null;
        if (code != null) {
            Matcher matcher = PATTERN.matcher(code.trim());
            if (matcher.find()) {
                targetValue = Integer.parseInt(matcher.group(1));
            }
        }
        
        return ParsedAlgorithm.builder()
                .algorithmName("binarySearch")
                .extractedVariables(Collections.emptyList())
                .targetValue(targetValue)
                .build();
    }
}
