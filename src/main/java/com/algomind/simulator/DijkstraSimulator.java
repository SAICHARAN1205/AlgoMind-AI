package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.VisualizationType;
import com.algomind.model.graph.DijkstraExecutionState;
import com.algomind.model.graph.GraphEdge;
import com.algomind.model.graph.GraphNode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DijkstraSimulator implements AlgorithmSimulator {

    private int stepCount = 1;

    // Helper class for priority queue elements
    private static class PQNode implements Comparable<PQNode> {
        int id;
        int distance;

        public PQNode(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }

        @Override
        public int compareTo(PQNode o) {
            return Integer.compare(this.distance, o.distance);
        }
        
        @Override
        public String toString() {
            return "(" + id + ", " + distance + ")";
        }
    }

    private static class Edge {
        int to;
        int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    @Override
    public String getAlgorithmName() {
        return "dijkstra";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;
        
        // Define hardcoded weighted graph:
        // 0 -> (1,4), (2,1)
        // 1 -> (3,1)
        // 2 -> (1,2), (3,5)
        // 3 -> (4,3)
        // 4 -> []
        Map<Integer, List<Edge>> adjList = new HashMap<>();
        adjList.put(0, Arrays.asList(new Edge(1, 4), new Edge(2, 1)));
        adjList.put(1, Collections.singletonList(new Edge(3, 1)));
        adjList.put(2, Arrays.asList(new Edge(1, 2), new Edge(3, 5)));
        adjList.put(3, Collections.singletonList(new Edge(4, 3)));
        adjList.put(4, Collections.emptyList());

        Map<Integer, GraphNode> nodes = new LinkedHashMap<>();
        for (int i = 0; i <= 4; i++) {
            nodes.put(i, GraphNode.builder().id(String.valueOf(i)).label(String.valueOf(i)).visited(false).active(false).level(-1).build());
        }
        
        List<GraphEdge> edges = new ArrayList<>();
        for (Map.Entry<Integer, List<Edge>> entry : adjList.entrySet()) {
            for (Edge e : entry.getValue()) {
                edges.add(GraphEdge.builder().source(String.valueOf(entry.getKey())).target(String.valueOf(e.to)).active(false).traversed(false).weight(e.weight).build());
            }
        }

        PriorityQueue<PQNode> pq = new PriorityQueue<>();
        Map<Integer, Integer> distanceMap = new HashMap<>();
        Set<Integer> visitedNodes = new HashSet<>();
        
        // Initialize distances
        for (int i = 0; i <= 4; i++) {
            distanceMap.put(i, Integer.MAX_VALUE);
        }
        
        int startNode = 0;
        distanceMap.put(startNode, 0);
        pq.add(new PQNode(startNode, 0));
        
        states.add(createState(nodes, edges, pq, distanceMap, null, null, visitedNodes, OperationType.START, ExecutionPhase.INITIALIZATION, "Start Dijkstra", "Initialize all distances to infinity, except the start node (0)."));
        states.add(createState(nodes, edges, pq, distanceMap, startNode, null, visitedNodes, OperationType.ADD_TO_PRIORITY_QUEUE, ExecutionPhase.INITIALIZATION, "Add Start Node", "Add start node 0 with distance 0 to the Priority Queue."));

        while (!pq.isEmpty()) {
            PQNode currentPQ = pq.poll();
            int u = currentPQ.id;
            
            states.add(createState(nodes, edges, pq, distanceMap, u, null, visitedNodes, OperationType.PICK_MIN_DISTANCE, ExecutionPhase.COMPUTATION, "Extract Min", "Extract node " + u + " from PQ with minimum distance " + currentPQ.distance + "."));
            
            if (visitedNodes.contains(u)) {
                states.add(createState(nodes, edges, pq, distanceMap, u, null, visitedNodes, OperationType.SKIP_NODE, ExecutionPhase.COMPUTATION, "Skip Node", "Node " + u + " is already visited with a shorter path. Skipping."));
                continue;
            }
            
            visitedNodes.add(u);
            nodes.get(u).setVisited(true);
            nodes.get(u).setActive(true);
            states.add(createState(nodes, edges, pq, distanceMap, u, null, visitedNodes, OperationType.VISIT_NODE, ExecutionPhase.COMPUTATION, "Visit Node", "Mark node " + u + " as finalized. Its shortest path is guaranteed."));
            
            for (Edge edge : adjList.get(u)) {
                int v = edge.to;
                int weight = edge.weight;
                
                GraphEdge activeEdge = findEdge(edges, u, v);
                if (activeEdge != null) activeEdge.setActive(true);
                
                states.add(createState(nodes, edges, pq, distanceMap, u, activeEdge, visitedNodes, OperationType.EXPLORE_NEIGHBOR, ExecutionPhase.COMPUTATION, "Check Neighbor", "Check edge from " + u + " to " + v + " with weight " + weight + "."));
                
                if (!visitedNodes.contains(v)) {
                    states.add(createState(nodes, edges, pq, distanceMap, u, activeEdge, visitedNodes, OperationType.RELAX_EDGE, ExecutionPhase.COMPUTATION, "Relax Edge", "Calculate path: dist[" + u + "] + weight = " + distanceMap.get(u) + " + " + weight + " = " + (distanceMap.get(u) + weight) + ". Compare with dist[" + v + "] (" + (distanceMap.get(v) == Integer.MAX_VALUE ? "∞" : distanceMap.get(v)) + ")."));
                    
                    if (distanceMap.get(u) + weight < distanceMap.get(v)) {
                        distanceMap.put(v, distanceMap.get(u) + weight);
                        if (activeEdge != null) activeEdge.setTraversed(true); // mark as part of shortest path visually
                        
                        states.add(createState(nodes, edges, pq, distanceMap, u, activeEdge, visitedNodes, OperationType.UPDATE_DISTANCE, ExecutionPhase.COMPUTATION, "Update Distance", "Shorter path found! Update dist[" + v + "] to " + distanceMap.get(v) + "."));
                        
                        pq.add(new PQNode(v, distanceMap.get(v)));
                        states.add(createState(nodes, edges, pq, distanceMap, u, activeEdge, visitedNodes, OperationType.ADD_TO_PRIORITY_QUEUE, ExecutionPhase.COMPUTATION, "Add to PQ", "Add updated node " + v + " to Priority Queue with new distance."));
                    }
                }
                
                if (activeEdge != null) activeEdge.setActive(false);
            }
            
            nodes.get(u).setActive(false);
        }

        states.add(createState(nodes, edges, pq, distanceMap, null, null, visitedNodes, OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Dijkstra Complete", "All reachable nodes have been finalized. Shortest paths computed."));

        return states;
    }
    
    private GraphEdge findEdge(List<GraphEdge> edges, int u, int v) {
        for (GraphEdge edge : edges) {
            if (edge.getSource().equals(String.valueOf(u)) && edge.getTarget().equals(String.valueOf(v))) {
                return edge;
            }
        }
        return null;
    }

    private ExecutionState createState(Map<Integer, GraphNode> nodes, List<GraphEdge> edges, PriorityQueue<PQNode> pq, Map<Integer, Integer> distanceMap, Integer currentNode, GraphEdge activeEdge, Set<Integer> visitedNodes, OperationType opType, ExecutionPhase phase, String stepTitle, String note) {
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
                    .weight(edge.getWeight())
                    .build());
        }
        
        List<String> pqCopy = new ArrayList<>();
        // PriorityQueue iteration is not ordered, so we clone, poll, and add to list to show ordered representation
        PriorityQueue<PQNode> tempPQ = new PriorityQueue<>(pq);
        while (!tempPQ.isEmpty()) {
            pqCopy.add(tempPQ.poll().toString());
        }

        DijkstraExecutionState dijkstraState = DijkstraExecutionState.builder()
                .nodes(nodesCopy)
                .edges(edgesCopy)
                .priorityQueue(pqCopy)
                .distanceMap(new HashMap<>(distanceMap))
                .currentNode(currentNode)
                .visitedNodes(new HashSet<>(visitedNodes))
                .activeEdge(activeEdge != null ? GraphEdge.builder().source(activeEdge.getSource()).target(activeEdge.getTarget()).build() : null)
                .explanation(note)
                .operationType(opType)
                .build();
                
        Map<String, Integer> vars = new HashMap<>();
        if (currentNode != null) vars.put("currentNode", currentNode);
        vars.put("queueSize", pq.size());
        vars.put("visitedCount", visitedNodes.size());
        if (currentNode != null && distanceMap.containsKey(currentNode)) {
             int d = distanceMap.get(currentNode);
             if (d != Integer.MAX_VALUE) vars.put("currentDistance", d);
        }

        return ExecutionState.builder()
                .step(stepCount++)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O((V + E) log V)")
                .spaceComplexity("O(V)")
                .variables(vars)
                .visualizationType(VisualizationType.GRAPH)
                .dijkstraState(dijkstraState)
                .message(note)
                .build();
    }
}
