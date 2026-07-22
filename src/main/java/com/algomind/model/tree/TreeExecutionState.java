package com.algomind.model.tree;

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
public class TreeExecutionState {
    private Map<String, TreeNode> nodes;
    private String rootId;
    private String activeNodeId;
    private List<Integer> traversalOrder; // To track 5 -> 10 -> 20 etc.
    private String activeOperation;
    private String explanation;
}
