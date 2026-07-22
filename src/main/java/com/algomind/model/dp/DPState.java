package com.algomind.model.dp;

import com.algomind.model.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DPState {
    private int row;
    private int col;
    private Integer value;
    private List<int[]> previousDependencies; // e.g., [{row-1, col}, {row-1, col-w}]
    private String explanation;
    private OperationType operationType;
    private boolean isUpdated;
    private boolean highlighted;
}
