package com.algomind.model.recursion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.algomind.model.OperationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecursionTreeNode {
    private String nodeId;
    private String parentId;
    private int depth;
    private int startIndex;
    private int endIndex;
    private int[] subArray;
    private OperationType operationType;
    private ExecutionStatus executionStatus;
    private String explanation;
}
