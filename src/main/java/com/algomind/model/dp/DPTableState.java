package com.algomind.model.dp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.algomind.model.OperationType;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DPTableState {
    private Integer[] table;
    private int activeIndex;
    private List<Integer> dependencyIndices;
    private Set<Integer> computedIndices;
    private String explanation;
    private OperationType operationType;
}
