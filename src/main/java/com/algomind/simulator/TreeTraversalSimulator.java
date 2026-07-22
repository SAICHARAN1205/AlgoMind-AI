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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TreeTraversalSimulator {

    private int stepCount = 1;
    
    public List<ExecutionState> simulate(String traversalType) {
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        
        // Build a static tree for traversals
        //       10
        //      /  \
        //     5    20
        //    / \   / \
        //   3   7 15  25
        Map<String, TreeNode> nodes = new LinkedHashMap<>();
        nodes.put("10", createNode("10", 10, "5", "20", 0, 0, 0));
        nodes.put("5", createNode("5", 5, "3", "7", 1, -100, 100));
        nodes.put("20", createNode("20", 20, "15", "25", 1, 100, 100));
        nodes.put("3", createNode("3", 3, null, null, 2, -150, 200));
        nodes.put("7", createNode("7", 7, null, null, 2, -50, 200));
        nodes.put("15", createNode("15", 15, null, null, 2, 50, 200));
        nodes.put("25", createNode("25", 25, null, null, 2, 150, 200));

        List<Integer> traversalOrder = new ArrayList<>();
        
        states.add(createState(nodes, "10", null, traversalOrder, "START", OperationType.START, ExecutionPhase.INITIALIZATION, "Initialize Tree", "Starting " + traversalType.toUpperCase() + " traversal on the binary tree."));

        if ("inorder".equalsIgnoreCase(traversalType)) {
            inorder(nodes, "10", states, traversalOrder);
        } else if ("preorder".equalsIgnoreCase(traversalType)) {
            preorder(nodes, "10", states, traversalOrder);
        } else if ("postorder".equalsIgnoreCase(traversalType)) {
            postorder(nodes, "10", states, traversalOrder);
        }

        states.add(createState(nodes, "10", null, traversalOrder, "COMPLETE", OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Traversal Complete", "Successfully finished " + traversalType.toUpperCase() + " traversal."));

        return states;
    }
    
    private void inorder(Map<String, TreeNode> nodes, String currentId, List<ExecutionState> states, List<Integer> traversalOrder) {
        if (currentId == null || !nodes.containsKey(currentId)) return;
        
        TreeNode node = nodes.get(currentId);
        
        states.add(createState(nodes, "10", currentId, traversalOrder, "VISIT", OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Arrive at Node " + node.getValue(), "Inorder goes Left -> Root -> Right. First, we explore the left subtree."));
        
        if (node.getLeftId() != null) {
            states.add(createState(nodes, "10", currentId, traversalOrder, "TRAVERSE_LEFT", OperationType.TRAVERSE_LEFT, ExecutionPhase.COMPUTATION, "Traverse Left from " + node.getValue(), "Moving to left child: " + node.getLeftId()));
            inorder(nodes, node.getLeftId(), states, traversalOrder);
        }
        
        // Process Root
        node.setVisited(true);
        traversalOrder.add(node.getValue());
        states.add(createState(nodes, "10", currentId, traversalOrder, "PROCESS", OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Process Node " + node.getValue(), "Left subtree done. Now processing node " + node.getValue() + " and adding to traversal order."));
        
        if (node.getRightId() != null) {
            states.add(createState(nodes, "10", currentId, traversalOrder, "TRAVERSE_RIGHT", OperationType.TRAVERSE_RIGHT, ExecutionPhase.COMPUTATION, "Traverse Right from " + node.getValue(), "Moving to right child: " + node.getRightId()));
            inorder(nodes, node.getRightId(), states, traversalOrder);
        }
    }

    private void preorder(Map<String, TreeNode> nodes, String currentId, List<ExecutionState> states, List<Integer> traversalOrder) {
        if (currentId == null || !nodes.containsKey(currentId)) return;
        
        TreeNode node = nodes.get(currentId);
        
        // Process Root
        node.setVisited(true);
        traversalOrder.add(node.getValue());
        states.add(createState(nodes, "10", currentId, traversalOrder, "PROCESS", OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Process Node " + node.getValue(), "Preorder goes Root -> Left -> Right. Processing node " + node.getValue() + " first."));
        
        if (node.getLeftId() != null) {
            states.add(createState(nodes, "10", currentId, traversalOrder, "TRAVERSE_LEFT", OperationType.TRAVERSE_LEFT, ExecutionPhase.COMPUTATION, "Traverse Left from " + node.getValue(), "Moving to left child: " + node.getLeftId()));
            preorder(nodes, node.getLeftId(), states, traversalOrder);
        }
        
        if (node.getRightId() != null) {
            states.add(createState(nodes, "10", currentId, traversalOrder, "TRAVERSE_RIGHT", OperationType.TRAVERSE_RIGHT, ExecutionPhase.COMPUTATION, "Traverse Right from " + node.getValue(), "Moving to right child: " + node.getRightId()));
            preorder(nodes, node.getRightId(), states, traversalOrder);
        }
    }

    private void postorder(Map<String, TreeNode> nodes, String currentId, List<ExecutionState> states, List<Integer> traversalOrder) {
        if (currentId == null || !nodes.containsKey(currentId)) return;
        
        TreeNode node = nodes.get(currentId);
        
        states.add(createState(nodes, "10", currentId, traversalOrder, "VISIT", OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Arrive at Node " + node.getValue(), "Postorder goes Left -> Right -> Root. Exploring left subtree first."));
        
        if (node.getLeftId() != null) {
            states.add(createState(nodes, "10", currentId, traversalOrder, "TRAVERSE_LEFT", OperationType.TRAVERSE_LEFT, ExecutionPhase.COMPUTATION, "Traverse Left from " + node.getValue(), "Moving to left child: " + node.getLeftId()));
            postorder(nodes, node.getLeftId(), states, traversalOrder);
        }
        
        if (node.getRightId() != null) {
            states.add(createState(nodes, "10", currentId, traversalOrder, "TRAVERSE_RIGHT", OperationType.TRAVERSE_RIGHT, ExecutionPhase.COMPUTATION, "Traverse Right from " + node.getValue(), "Moving to right child: " + node.getRightId()));
            postorder(nodes, node.getRightId(), states, traversalOrder);
        }
        
        // Process Root
        node.setVisited(true);
        traversalOrder.add(node.getValue());
        states.add(createState(nodes, "10", currentId, traversalOrder, "PROCESS", OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Process Node " + node.getValue(), "Both subtrees done. Now processing node " + node.getValue() + "."));
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

    private ExecutionState createState(Map<String, TreeNode> nodes, String rootId, String activeNodeId, List<Integer> traversalOrder, String activeOp, OperationType opType, ExecutionPhase phase, String stepTitle, String note) {
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
                .traversalOrder(new ArrayList<>(traversalOrder))
                .activeOperation(activeOp)
                .explanation(note)
                .build();

        return ExecutionState.builder()
                .step(stepCount++)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O(N)")
                .spaceComplexity("O(H) where H is tree height")
                .visualizationType(VisualizationType.TREE)
                .treeState(treeState)
                .message(note)
                .build();
    }
}
