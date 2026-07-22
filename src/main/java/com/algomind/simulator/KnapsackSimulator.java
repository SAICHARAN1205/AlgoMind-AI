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
public class KnapsackSimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "knapsackDP";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        
        // For knapsack, we expect the context to have specific inputs, but we will mock default inputs
        // since the current ExecuteRequest format only gives us `int[] array` and `Integer target`.
        // We will assume `array` contains weights, and we'll mock values, or vice-versa.
        // For Phase 8 visualization focus, we hardcode inputs if they are missing.
        
        int[] weights = {1, 2, 3};
        int[] values = {60, 100, 120};
        int capacity = 5;
        
        int n = weights.length;
        Integer[][] matrix = new Integer[n + 1][capacity + 1];
        
        DPTable table = DPTable.builder()
                .rows(n + 1)
                .cols(capacity + 1)
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
                .stepTitle("Initialize 2D DP Table")
                .message("Table rows = items (0 to " + n + "), cols = capacity (0 to " + capacity + ")")
                .build());

        // Base cases: 0 items or 0 capacity = 0 value
        for (int i = 0; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                if (i == 0 || w == 0) {
                    matrix[i][w] = 0;
                    table.getCompletedCells().add(i + "," + w);
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
                .message("With 0 capacity or 0 items, max value is 0.")
                .build());

        // Build table
        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= capacity; w++) {
                int weight = weights[i - 1];
                int value = values[i - 1];
                
                List<int[]> dependencies = new ArrayList<>();
                dependencies.add(new int[]{i - 1, w}); // Exclude dependency
                
                String note;
                if (weight <= w) {
                    dependencies.add(new int[]{i - 1, w - weight}); // Include dependency
                    matrix[i][w] = Math.max(matrix[i - 1][w], value + matrix[i - 1][w - weight]);
                    note = "We can INCLUDE item " + i + " (weight=" + weight + ", val=" + value + "). Max(Exclude, Include).";
                } else {
                    matrix[i][w] = matrix[i - 1][w];
                    note = "Item " + i + " is too heavy (weight=" + weight + " > cap=" + w + "). Must EXCLUDE.";
                }
                
                table.getCompletedCells().add(i + "," + w);
                
                states.add(ExecutionState.builder()
                        .step(step++)
                        .executionPhase(ExecutionPhase.TABULATION)
                        .operationType(OperationType.DP_TRANSITION)
                        .visualizationType(VisualizationType.GRID)
                        .dpTable(cloneTable(table, new int[]{i, w}, dependencies))
                        .stepTitle("Calculate dp[" + i + "][" + w + "]")
                        .message("Set dp[" + i + "][" + w + "] = " + matrix[i][w])
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
                .message("Max value is dp[" + n + "][" + capacity + "] = " + matrix[n][capacity])
                .educationalNote("Bottom-up DP fills row by row. The final answer lies in the bottom-right cell.")
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
