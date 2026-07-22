package com.algomind.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StackExecutionState {
    private List<Integer> stackElements;
    private int topIndex;
    private String activeOperation; // "PUSH", "POP", "PEEK"
    private Integer highlightedElement;
    private String explanation;
}
