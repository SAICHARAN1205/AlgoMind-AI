package com.algomind.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import com.algomind.model.recursion.RecursionNode;
import com.algomind.model.recursion.RecursionTreeNode;
import com.algomind.model.recursion.StackFrame;
import com.algomind.model.dp.DPTable;
import com.algomind.model.dp.DPTableState;
import com.algomind.model.graph.GraphExecutionState;
import com.algomind.model.graph.DFSExecutionState;
import com.algomind.model.graph.DijkstraExecutionState;
import com.algomind.model.tree.TreeExecutionState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionState {
    
    private int step;
    private int lineNumber;
    private int currentLine;
    
    private OperationType operationType;
    private ExecutionPhase executionPhase;
    
    private String stepTitle;
    private String educationalNote;
    
    private String timeComplexity;
    private String spaceComplexity;
    
    private int[] array;
    private List<Integer> highlightedIndices;
    private Map<String, Integer> variables;
    
    private boolean swapOccurred;
    private String message;
    
    private VisualizationType visualizationType;
    private Map<String, RecursionNode> recursionTree;
    private String activeNodeId;
    private List<StackFrame> callStack;
    
    private Map<String, RecursionTreeNode> mergeTree;
    
    private DPTable dpTable;
    private DPTableState dpTableState;
    private String dpMode;
    
    private GraphExecutionState graphState;
    private DFSExecutionState dfsState;
    private DijkstraExecutionState dijkstraState;
    
    private StackExecutionState stackState;
    private QueueExecutionState queueState;
    
    private TreeExecutionState treeState;
}
