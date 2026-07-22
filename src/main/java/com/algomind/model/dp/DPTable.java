package com.algomind.model.dp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DPTable {
    private int rows;
    private int cols;
    private Integer[][] matrix; // null represents uncomputed state
    
    private int[] activeCell; // [row, col]
    private List<int[]> currentTransition; // Dependencies for the active cell
    private Set<String> completedCells; // Set of "row,col"
    
    private String educationalInsight;
}
