package com.algomind.model.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphEdge {
    private String source;
    private String target;
    private boolean active;
    private boolean traversed;
    @Builder.Default
    private Integer weight = 1;
}
