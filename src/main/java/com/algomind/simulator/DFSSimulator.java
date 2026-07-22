package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.graph.DFSExecutionState;
import com.algomind.model.graph.GraphEdge;
import com.algomind.model.graph.GraphNode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DFSSimulator implements AlgorithmSimulator {

    private int stepCount = 1;

    @Override
    public String getAlgorithmName() {
        return "dfs";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        
        Map<Integer, List<Integer>> adjList = new HashMap<>();
        adjList.put(0, Arrays.asList(1, 2));
        adjList.put(1, Arrays.asList(3, 4));
        adjList.put(2, Collections.singletonList(5));
        adjList.put(3, Collections.emptyList());
        adjList.put(4, Collections.singletonList(5));
        adjList.put(5, Collections.emptyList());

        Map<Integer, GraphNode> nodes = new LinkedHashMap<>();
        for (int i = 0; i <= 5; i++) {
            nodes.put(i, GraphNode.builder().id(String.valueOf(i)).label(String.valueOf(i)).visited(false).active(false).level(-1).build());
        }
        
        List<GraphEdge> edges = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : adjList.entrySet()) {
            for (Integer neighbor : entry.getValue()) {
                edges.add(GraphEdge.builder().source(String.valueOf(entry.getKey())).target(String.valueOf(neighbor)).active(false).traversed(false).build());
            }
        }

        List<Integer> recursionStack = new ArrayList<>();
        List<Integer> traversalOrder = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        
        int startNode = 0;
        
        states.add(createState(nodes, edges, recursionStack, traversalOrder, startNode, 0, OperationType.START, ExecutionPhase.INITIALIZATION, "Start DFS", "Starting DFS from node " + startNode + ".", visited));
        
        dfs(startNode, -1, 0, adjList, nodes, edges, recursionStack, traversalOrder, visited, states);

        states.add(createState(nodes, edges, recursionStack, traversalOrder, -1, 0, OperationType.COMPLETE, ExecutionPhase.COMPLETION, "DFS Complete", "DFS has visited all reachable nodes deeply.", visited));

        return states;
    }
    
    private void dfs(int current, int parent, int depth, Map<Integer, List<Integer>> adjList, Map<Integer, GraphNode> nodes, List<GraphEdge> edges, List<Integer> recursionStack, List<Integer> traversalOrder, Set<Integer> visited, List<ExecutionState> states) {
        
        recursionStack.add(current);
        visited.add(current);
        traversalOrder.add(current);
        
        nodes.get(current).setActive(true);
        nodes.get(current).setVisited(true);
        nodes.get(current).setLevel(depth);
        
        states.add(createState(nodes, edges, recursionStack, traversalOrder, current, depth, OperationType.RECURSIVE_CALL, ExecutionPhase.COMPUTATION, "Recursive Call", "Pushing node " + current + " onto the recursion stack.", visited));
        
        states.add(createState(nodes, edges, recursionStack, traversalOrder, current, depth, OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Visit Node", "Node " + current + " is now being visited and marked as visited.", visited));

        for (int neighbor : adjList.get(current)) {
            setEdgeActive(edges, current, neighbor, true);
            
            states.add(createState(nodes, edges, recursionStack, traversalOrder, current, depth, OperationType.EXPLORE_NEIGHBOR, ExecutionPhase.COMPUTATION, "Explore Neighbor", "Exploring neighbor " + neighbor + " from node " + current + ".", visited));
            
            if (!visited.contains(neighbor)) {
                setEdgeTraversed(edges, current, neighbor, true);
                
                nodes.get(current).setActive(false);
                dfs(neighbor, current, depth + 1, adjList, nodes, edges, recursionStack, traversalOrder, visited, states);
                nodes.get(current).setActive(true);
                
            } else {
                states.add(createState(nodes, edges, recursionStack, traversalOrder, neighbor, depth, OperationType.SKIP_VISITED, ExecutionPhase.COMPUTATION, "Skip Visited", "Node " + neighbor + " has already been visited. Skip it to avoid cycles.", visited));
            }
            
            setEdgeActive(edges, current, neighbor, false);
        }
        
        nodes.get(current).setActive(false);
        
        if (parent != -1) {
            setEdgeActive(edges, parent, current, true);
            states.add(createState(nodes, edges, recursionStack, traversalOrder, current, depth, OperationType.BACKTRACK, ExecutionPhase.COMPUTATION, "Backtrack", "Node " + current + " has no more unvisited neighbors. Backtracking to parent node " + parent + ".", visited));
            setEdgeActive(edges, parent, current, false);
        }
        
        recursionStack.remove(recursionStack.size() - 1);
        states.add(createState(nodes, edges, recursionStack, traversalOrder, current, depth, OperationType.RETURN, ExecutionPhase.COMPUTATION, "Return", "Popping node " + current + " from the recursion stack.", visited));
    }

    private void setEdgeActive(List<GraphEdge> edges, int source, int target, boolean active) {
        for (GraphEdge edge : edges) {
            if (edge.getSource().equals(String.valueOf(source)) && edge.getTarget().equals(String.valueOf(target))) {
                edge.setActive(active);
            }
        }
    }
    
    private void setEdgeTraversed(List<GraphEdge> edges, int source, int target, boolean traversed) {
        for (GraphEdge edge : edges) {
            if (edge.getSource().equals(String.valueOf(source)) && edge.getTarget().equals(String.valueOf(target))) {
                edge.setTraversed(traversed);
            }
        }
    }

    private ExecutionState createState(Map<Integer, GraphNode> nodes, List<GraphEdge> edges, List<Integer> recursionStack, List<Integer> traversalOrder, int currentNode, int currentDepth, OperationType opType, ExecutionPhase phase, String stepTitle, String note, Set<Integer> visited) {
        List<GraphNode> nodesCopy = new ArrayList<>();
        for (GraphNode node : nodes.values()) {
            nodesCopy.add(GraphNode.builder()
                    .id(node.getId())
                    .label(node.getLabel())
                    .visited(node.isVisited())
                    .active(node.isActive())
                    .level(node.getLevel())
                    .explanation(node.getExplanation())
                    .build());
        }
        
        List<GraphEdge> edgesCopy = new ArrayList<>();
        for (GraphEdge edge : edges) {
            edgesCopy.add(GraphEdge.builder()
                    .source(edge.getSource())
                    .target(edge.getTarget())
                    .active(edge.isActive())
                    .traversed(edge.isTraversed())
                    .build());
        }

        DFSExecutionState dfsState = DFSExecutionState.builder()
                .nodes(nodesCopy)
                .edges(edgesCopy)
                .recursionStack(new ArrayList<>(recursionStack))
                .traversalOrder(new ArrayList<>(traversalOrder))
                .currentNode(currentNode == -1 ? null : currentNode)
                .currentDepth(currentDepth)
                .explanation(note)
                .operationType(opType)
                .build();
                
        Map<String, Integer> vars = new HashMap<>();
        vars.put("currentNode", currentNode);
        vars.put("stackSize", recursionStack.size());
        vars.put("recursionDepth", currentDepth);
        vars.put("visitedCount", visited.size());

        return ExecutionState.builder()
                .step(stepCount++)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O(V + E)")
                .spaceComplexity("O(V)")
                .variables(vars)
                .visualizationType(VisualizationType.GRAPH)
                .dfsState(dfsState)
                .message(note)
                .build();
    }
}
