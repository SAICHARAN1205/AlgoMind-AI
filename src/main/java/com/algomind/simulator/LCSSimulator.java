package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.dp.DPTable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class LCSSimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "lcsDP";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        
        // Mock default strings for Phase 8 visualization
        String s1 = "abcde";
        String s2 = "ace"; 
        
        int m = s1.length();
        int n = s2.length();
        
        Integer[][] matrix = new Integer[m + 1][n + 1];
        
        DPTable table = DPTable.builder()
                .rows(m + 1)
                .cols(n + 1)
                .matrix(matrix)
                .completedCells(new HashSet<>())
                .build();

        int step = 0;
        states.add(ExecutionState.builder()
                .step(step++)
                .executionPhase(ExecutionPhase.INITIALIZATION)
                .operationType(OperationType.INIT)
                .visualizationType(VisualizationType.GRID)
                .dpTable(cloneTable(table))
                .stepTitle("Initialize LCS Table")
                .message("Table size: (" + (m+1) + " x " + (n+1) + ")")
                .educationalNote("Rows represent s1 prefixes, columns represent s2 prefixes.")
                .build());

        // Base cases: 0 length
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    matrix[i][j] = 0;
                    table.getCompletedCells().add(i + "," + j);
                }
            }
        }
        
        states.add(ExecutionState.builder()
                .step(step++)
                .executionPhase(ExecutionPhase.TABULATION)
                .operationType(OperationType.DP_TRANSITION)
                .visualizationType(VisualizationType.GRID)
                .dpTable(cloneTable(table))
                .stepTitle("Base Cases Filled")
                .message("LCS with an empty string is 0.")
                .build());

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                List<int[]> dependencies = new ArrayList<>();
                String note;
                
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dependencies.add(new int[]{i - 1, j - 1});
                    matrix[i][j] = 1 + matrix[i - 1][j - 1];
                    note = "Characters MATCH! Take diagonal + 1. dp["+i+"]["+j+"] = 1 + dp["+(i-1)+"]["+(j-1)+"]";
                } else {
                    dependencies.add(new int[]{i - 1, j});
                    dependencies.add(new int[]{i, j - 1});
                    matrix[i][j] = Math.max(matrix[i - 1][j], matrix[i][j - 1]);
                    note = "Characters MISMATCH. Take max(above, left).";
                }
                
                table.getCompletedCells().add(i + "," + j);
                
                states.add(ExecutionState.builder()
                        .step(step++)
                        .executionPhase(ExecutionPhase.TABULATION)
                        .operationType(OperationType.DP_TRANSITION)
                        .visualizationType(VisualizationType.GRID)
                        .dpTable(cloneTable(table, new int[]{i, j}, dependencies))
                        .stepTitle("Calculate dp[" + i + "][" + j + "]")
                        .message("Comparing '" + s1.charAt(i-1) + "' and '" + s2.charAt(j-1) + "'")
                        .educationalNote(note)
                        .build());
            }
        }
        
        states.add(ExecutionState.builder()
                .step(step++)
                .executionPhase(ExecutionPhase.COMPLETION)
                .operationType(OperationType.COMPLETE)
                .visualizationType(VisualizationType.GRID)
                .dpTable(cloneTable(table))
                .stepTitle("Finished")
                .message("Longest Common Subsequence length is " + matrix[m][n])
                .build());

        return states;
    }
    
    private DPTable cloneTable(DPTable src) {
        return cloneTable(src, null, null);
    }

    private DPTable cloneTable(DPTable src, int[] activeCell, List<int[]> currentTransition) {
        Integer[][] clonedMatrix = new Integer[src.getRows()][src.getCols()];
        for (int i = 0; i < src.getRows(); i++) {
            System.arraycopy(src.getMatrix()[i], 0, clonedMatrix[i], 0, src.getCols());
        }
        return DPTable.builder()
                .rows(src.getRows())
                .cols(src.getCols())
                .matrix(clonedMatrix)
                .activeCell(activeCell)
                .currentTransition(currentTransition)
                .completedCells(new HashSet<>(src.getCompletedCells()))
                .build();
    }
}
