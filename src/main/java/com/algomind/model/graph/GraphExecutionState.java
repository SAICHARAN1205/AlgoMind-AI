package com.algomind.model.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.algomind.model.OperationType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphExecutionState {
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;
    private List<Integer> queue;
    private List<Integer> traversalOrder;
    private Integer currentNode;
    private String explanation;
    private OperationType operationType;
}
