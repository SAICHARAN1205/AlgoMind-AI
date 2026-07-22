package com.algomind.model.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphNode {
    private String id;
    private String label;
    private boolean visited;
    private boolean active;
    private int level;
    private String explanation;
}
