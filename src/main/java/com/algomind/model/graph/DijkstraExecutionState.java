package com.algomind.model.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.algomind.model.OperationType;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DijkstraExecutionState {
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;
    private List<String> priorityQueue;
    private Map<Integer, Integer> distanceMap;
    private Integer currentNode;
    private Set<Integer> visitedNodes;
    private GraphEdge activeEdge;
    private String explanation;
    private OperationType operationType;
}
