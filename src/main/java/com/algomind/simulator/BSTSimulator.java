package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.tree.TreeExecutionState;
import com.algomind.model.tree.TreeNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BSTSimulator {

    private int stepCount = 1;

    public List<ExecutionState> simulate() {
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        
        Map<String, TreeNode> nodes = new LinkedHashMap<>();
        
        // Start Empty
        states.add(createState(nodes, null, null, "START", OperationType.START, ExecutionPhase.INITIALIZATION, "Initialize BST", "Starting with an empty Binary Search Tree."));

        // Insert 10
        String rootId = "10";
        nodes.put(rootId, createNode(rootId, 10, null, null, 0, 0, 0));
        states.add(createState(nodes, rootId, rootId, "INSERT", OperationType.INSERT_NODE, ExecutionPhase.COMPUTATION, "Insert 10", "Tree is empty, inserting 10 as the root."));

        // Insert 5
        insertNode(nodes, rootId, 5, states);
        
        // Insert 15
        insertNode(nodes, rootId, 15, states);
        
        // Insert 3
        insertNode(nodes, rootId, 3, states);
        
        // Search 15
        searchNode(nodes, rootId, 15, states);

        states.add(createState(nodes, rootId, null, "COMPLETE", OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Operations Complete", "BST Insert and Search demonstrated."));

        return states;
    }

    private void insertNode(Map<String, TreeNode> nodes, String rootId, int value, List<ExecutionState> states) {
        String currentId = rootId;
        int level = 0;
        double currentX = 0;
        double currentY = 0;
        
        states.add(createState(nodes, rootId, null, "PREPARE_INSERT", OperationType.START, ExecutionPhase.COMPUTATION, "Insert " + value, "Preparing to insert " + value + " into the BST."));
        
        while (true) {
            TreeNode current = nodes.get(currentId);
            states.add(createState(nodes, rootId, currentId, "COMPARE", OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Compare " + value + " with " + current.getValue(), "Is " + value + " < " + current.getValue() + "?"));
            
            if (value < current.getValue()) {
                states.add(createState(nodes, rootId, currentId, "TRAVERSE_LEFT", OperationType.TRAVERSE_LEFT, ExecutionPhase.COMPUTATION, "Go Left", value + " is less than " + current.getValue() + ", so we go left."));
                if (current.getLeftId() == null) {
                    String newId = String.valueOf(value);
                    double offset = 100.0 / (level + 1);
                    current.setLeftId(newId);
                    nodes.put(newId, createNode(newId, value, null, null, level + 1, currentX - offset, currentY + 100));
                    states.add(createState(nodes, rootId, newId, "INSERT", OperationType.INSERT_NODE, ExecutionPhase.COMPUTATION, "Insert " + value, "Found empty left spot. Inserted " + value + "."));
                    break;
                } else {
                    currentId = current.getLeftId();
                    currentX = nodes.get(currentId).getX();
                    currentY = nodes.get(currentId).getY();
                    level++;
                }
            } else {
                states.add(createState(nodes, rootId, currentId, "TRAVERSE_RIGHT", OperationType.TRAVERSE_RIGHT, ExecutionPhase.COMPUTATION, "Go Right", value + " is greater than " + current.getValue() + ", so we go right."));
                if (current.getRightId() == null) {
                    String newId = String.valueOf(value);
                    double offset = 100.0 / (level + 1);
                    current.setRightId(newId);
                    nodes.put(newId, createNode(newId, value, null, null, level + 1, currentX + offset, currentY + 100));
                    states.add(createState(nodes, rootId, newId, "INSERT", OperationType.INSERT_NODE, ExecutionPhase.COMPUTATION, "Insert " + value, "Found empty right spot. Inserted " + value + "."));
                    break;
                } else {
                    currentId = current.getRightId();
                    currentX = nodes.get(currentId).getX();
                    currentY = nodes.get(currentId).getY();
                    level++;
                }
            }
        }
    }

    private void searchNode(Map<String, TreeNode> nodes, String rootId, int target, List<ExecutionState> states) {
        String currentId = rootId;
        states.add(createState(nodes, rootId, null, "PREPARE_SEARCH", OperationType.START, ExecutionPhase.COMPUTATION, "Search " + target, "Searching for " + target + " in the BST."));
        
        while (currentId != null) {
            TreeNode current = nodes.get(currentId);
            states.add(createState(nodes, rootId, currentId, "COMPARE", OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Compare " + target + " with " + current.getValue(), "Checking node " + current.getValue() + "."));
            
            if (current.getValue() == target) {
                states.add(createState(nodes, rootId, currentId, "FOUND", OperationType.SEARCH_NODE, ExecutionPhase.COMPUTATION, "Found " + target, "Target " + target + " found in the BST!"));
                return;
            } else if (target < current.getValue()) {
                states.add(createState(nodes, rootId, currentId, "TRAVERSE_LEFT", OperationType.TRAVERSE_LEFT, ExecutionPhase.COMPUTATION, "Go Left", target + " < " + current.getValue() + ", searching left subtree."));
                currentId = current.getLeftId();
            } else {
                states.add(createState(nodes, rootId, currentId, "TRAVERSE_RIGHT", OperationType.TRAVERSE_RIGHT, ExecutionPhase.COMPUTATION, "Go Right", target + " > " + current.getValue() + ", searching right subtree."));
                currentId = current.getRightId();
            }
        }
    }

    private TreeNode createNode(String id, int value, String leftId, String rightId, int level, double x, double y) {
        return TreeNode.builder()
                .id(id)
                .value(value)
                .leftId(leftId)
                .rightId(rightId)
                .level(level)
                .x(x)
                .y(y)
                .visited(false)
                .active(false)
                .build();
    }

    private ExecutionState createState(Map<String, TreeNode> nodes, String rootId, String activeNodeId, String activeOp, OperationType opType, ExecutionPhase phase, String stepTitle, String note) {
        // Deep copy nodes
        Map<String, TreeNode> nodesCopy = new LinkedHashMap<>();
        for (Map.Entry<String, TreeNode> entry : nodes.entrySet()) {
            TreeNode n = entry.getValue();
            nodesCopy.put(entry.getKey(), TreeNode.builder()
                    .id(n.getId())
                    .value(n.getValue())
                    .leftId(n.getLeftId())
                    .rightId(n.getRightId())
                    .level(n.getLevel())
                    .x(n.getX())
                    .y(n.getY())
                    .visited(n.isVisited())
                    .active(n.getId().equals(activeNodeId))
                    .build());
        }

        TreeExecutionState treeState = TreeExecutionState.builder()
                .nodes(nodesCopy)
                .rootId(rootId)
                .activeNodeId(activeNodeId)
                .traversalOrder(new ArrayList<>())
                .activeOperation(activeOp)
                .explanation(note)
                .build();

        return ExecutionState.builder()
                .step(stepCount++)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O(log N) average, O(N) worst")
                .spaceComplexity("O(H) where H is tree height")
                .visualizationType(VisualizationType.TREE)
                .treeState(treeState)
                .message(note)
                .build();
    }
}
