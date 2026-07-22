package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.graph.GraphEdge;
import com.algomind.model.graph.GraphExecutionState;
import com.algomind.model.graph.GraphNode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BFSSimulator implements AlgorithmSimulator {

    private int stepCount = 1;

    @Override
    public String getAlgorithmName() {
        return "bfs";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        
        // Define hardcoded graph:
        // 0 -> [1, 2]
        // 1 -> [3, 4]
        // 2 -> [5]
        // 3 -> []
        // 4 -> [5]
        // 5 -> []
        
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

        Queue<Integer> queue = new LinkedList<>();
        List<Integer> traversalOrder = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> levels = new HashMap<>();
        
        // Start BFS
        int startNode = 0;
        queue.add(startNode);
        visited.add(startNode);
        levels.put(startNode, 0);
        nodes.get(startNode).setVisited(true);
        nodes.get(startNode).setLevel(0);
        
        states.add(createState(nodes, edges, queue, traversalOrder, startNode, OperationType.START, ExecutionPhase.INITIALIZATION, "Start BFS", "Starting BFS from node " + startNode + ".", visited));
        
        states.add(createState(nodes, edges, queue, traversalOrder, startNode, OperationType.ENQUEUE, ExecutionPhase.COMPUTATION, "Enqueue Node", "Node " + startNode + " is added to the queue.", visited));

        while (!queue.isEmpty()) {
            int current = queue.poll();
            traversalOrder.add(current);
            nodes.get(current).setActive(true);
            
            states.add(createState(nodes, edges, queue, traversalOrder, current, OperationType.DEQUEUE, ExecutionPhase.COMPUTATION, "Dequeue Node", "Dequeue node " + current + " to process its neighbors.", visited));
            
            states.add(createState(nodes, edges, queue, traversalOrder, current, OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Visit Node", "Node " + current + " is now being visited.", visited));

            for (int neighbor : adjList.get(current)) {
                // Highlight edge
                setEdgeActive(edges, current, neighbor, true);
                
                states.add(createState(nodes, edges, queue, traversalOrder, current, OperationType.EXPLORE_NEIGHBOR, ExecutionPhase.COMPUTATION, "Explore Neighbor", "Exploring neighbor " + neighbor + " from node " + current + ".", visited));
                
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    levels.put(neighbor, levels.get(current) + 1);
                    nodes.get(neighbor).setVisited(true);
                    nodes.get(neighbor).setLevel(levels.get(neighbor));
                    
                    queue.add(neighbor);
                    setEdgeTraversed(edges, current, neighbor, true);
                    
                    states.add(createState(nodes, edges, queue, traversalOrder, neighbor, OperationType.ENQUEUE, ExecutionPhase.COMPUTATION, "Enqueue Neighbor", "Node " + neighbor + " is unvisited. Add it to the queue.", visited));
                } else {
                    states.add(createState(nodes, edges, queue, traversalOrder, neighbor, OperationType.SKIP_VISITED, ExecutionPhase.COMPUTATION, "Skip Visited", "Node " + neighbor + " has already been visited. Skip it to avoid cycles.", visited));
                }
                
                setEdgeActive(edges, current, neighbor, false);
            }
            
            nodes.get(current).setActive(false);
        }

        states.add(createState(nodes, edges, queue, traversalOrder, -1, OperationType.COMPLETE, ExecutionPhase.COMPLETION, "BFS Complete", "BFS has visited all reachable nodes level by level.", visited));

        return states;
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

    private ExecutionState createState(Map<Integer, GraphNode> nodes, List<GraphEdge> edges, Queue<Integer> queue, List<Integer> traversalOrder, int currentNode, OperationType opType, ExecutionPhase phase, String stepTitle, String note, Set<Integer> visited) {
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

        GraphExecutionState graphState = GraphExecutionState.builder()
                .nodes(nodesCopy)
                .edges(edgesCopy)
                .queue(new ArrayList<>(queue))
                .traversalOrder(new ArrayList<>(traversalOrder))
                .currentNode(currentNode == -1 ? null : currentNode)
                .explanation(note)
                .operationType(opType)
                .build();
                
        Map<String, Integer> vars = new HashMap<>();
        vars.put("currentNode", currentNode);
        vars.put("queueSize", queue.size());
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
                .graphState(graphState)
                .message(note)
                .build();
    }
}
