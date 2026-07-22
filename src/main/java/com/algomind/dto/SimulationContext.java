package com.algomind.dto;

import com.algomind.model.ParsedAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationContext {
    private int[] array;
    private Integer target;
    private String rawCode;
    private ParsedAlgorithm parsedAlgorithm;
    private Map<String, Object> metadata;
}
