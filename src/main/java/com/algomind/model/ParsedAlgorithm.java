package com.algomind.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedAlgorithm {
    private String algorithmName;
    private List<String> extractedVariables;
    private Integer targetValue;
    private Map<String, Object> metadata;
}
