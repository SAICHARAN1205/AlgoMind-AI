package com.algomind.model.tree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNode {
    private String id;
    private int value;
    private String leftId;
    private String rightId;
    
    // For rendering and highlighting
    private boolean visited;
    private boolean active;
    private int level;
    private double x; // layout coordinate
    private double y; // layout coordinate
}
