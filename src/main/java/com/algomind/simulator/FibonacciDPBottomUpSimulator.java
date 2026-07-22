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
public class FibonacciDPBottomUpSimulator implements AlgorithmSimulator {

    @Override
    public String getAlgorithmName() {
        return "fibonacciDP";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        int n = context.getTarget() != null ? context.getTarget() : 5; // Default to 5
        
        Integer[][] matrix = new Integer[1][n + 1];
        DPTable table = DPTable.builder()
                .rows(1)
                .cols(n + 1)
                .matrix(matrix)
                .completedCells(new HashSet<>())
                .build();
                
        int step = 0;
        
        // Init state
        states.add(ExecutionState.builder()
                .step(step++)
                .executionPhase(ExecutionPhase.INITIALIZATION)
                .operationType(OperationType.INIT)
                .visualizationType(VisualizationType.GRID)
                .dpTable(cloneTable(table))
                .stepTitle("Initialize DP Table")
                .message("Create a 1D DP table of size " + (n + 1) + " to store Fibonacci results.")
                .build());

        // Base cases
        matrix[0][0] = 0;
        table.getCompletedCells().add("0,0");
        states.add(ExecutionState.builder()
                .step(step++)
                .executionPhase(ExecutionPhase.TABULATION)
                .operationType(OperationType.DP_TRANSITION)
                .visualizationType(VisualizationType.GRID)
                .dpTable(cloneTable(table, new int[]{0, 0}, null))
                .stepTitle("Base Case: dp[0]")
                .message("Set dp[0] = 0")
                .educationalNote("The 0th Fibonacci number is 0 by definition.")
                .build());

        if (n >= 1) {
            matrix[0][1] = 1;
            table.getCompletedCells().add("0,1");
            states.add(ExecutionState.builder()
                    .step(step++)
                    .executionPhase(ExecutionPhase.TABULATION)
                    .operationType(OperationType.DP_TRANSITION)
                    .visualizationType(VisualizationType.GRID)
                    .dpTable(cloneTable(table, new int[]{0, 1}, null))
                    .stepTitle("Base Case: dp[1]")
                    .message("Set dp[1] = 1")
                    .educationalNote("The 1st Fibonacci number is 1 by definition.")
                    .build());
        }

        // Iteration
        for (int i = 2; i <= n; i++) {
            List<int[]> dependencies = List.of(new int[]{0, i - 1}, new int[]{0, i - 2});
            
            // Highlight dependencies before calculating
            states.add(ExecutionState.builder()
                    .step(step++)
                    .executionPhase(ExecutionPhase.TABULATION)
                    .operationType(OperationType.CALCULATE)
                    .visualizationType(VisualizationType.GRID)
                    .dpTable(cloneTable(table, new int[]{0, i}, dependencies))
                    .stepTitle("Calculate dp[" + i + "]")
                    .message("Looking up dp[" + (i-1) + "] and dp[" + (i-2) + "]")
                    .educationalNote("dp[" + i + "] depends on the two previous computed states. This is the optimal substructure.")
                    .build());
                    
            matrix[0][i] = matrix[0][i - 1] + matrix[0][i - 2];
            table.getCompletedCells().add("0," + i);
            
            states.add(ExecutionState.builder()
                    .step(step++)
                    .executionPhase(ExecutionPhase.TABULATION)
                    .operationType(OperationType.DP_TRANSITION)
                    .visualizationType(VisualizationType.GRID)
                    .dpTable(cloneTable(table, new int[]{0, i}, dependencies))
                    .stepTitle("Update dp[" + i + "]")
                    .message("Set dp[" + i + "] = " + matrix[0][i])
                    .build());
        }

        states.add(ExecutionState.builder()
                .step(step++)
                .executionPhase(ExecutionPhase.COMPLETION)
                .operationType(OperationType.COMPLETE)
                .visualizationType(VisualizationType.GRID)
                .dpTable(cloneTable(table))
                .stepTitle("Finished")
                .message("The final answer is at dp[" + n + "] = " + matrix[0][n])
                .educationalNote("Bottom-up DP fills the table iteratively from smallest to largest subproblems, ensuring O(N) time and O(N) space.")
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
