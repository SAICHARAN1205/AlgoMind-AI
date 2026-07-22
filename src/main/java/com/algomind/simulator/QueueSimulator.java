package com.algomind.simulator;

import com.algomind.dto.SimulationContext;
import com.algomind.model.ExecutionPhase;
import com.algomind.model.ExecutionState;
import com.algomind.model.OperationType;
import com.algomind.model.QueueExecutionState;
import com.algomind.model.VisualizationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueueSimulator implements AlgorithmSimulator {

    private int stepCount = 1;

    @Override
    public String getAlgorithmName() {
        return "queue";
    }

    @Override
    public List<ExecutionState> simulate(SimulationContext context) {
        List<ExecutionState> states = new ArrayList<>();
        stepCount = 1;

        List<Integer> queueElements = new ArrayList<>();
        int front = 0;
        int rear = -1;

        states.add(createState(queueElements, front, rear, "START", null, OperationType.START, ExecutionPhase.INITIALIZATION, "Initialize Queue", "The queue is initially empty."));

        // Enqueue 10
        queueElements.add(10);
        rear++;
        states.add(createState(queueElements, front, rear, "ENQUEUE", 10, OperationType.ENQUEUE, ExecutionPhase.COMPUTATION, "Enqueue 10", "Insert 10 at the rear of the queue."));

        // Enqueue 20
        queueElements.add(20);
        rear++;
        states.add(createState(queueElements, front, rear, "ENQUEUE", 20, OperationType.ENQUEUE, ExecutionPhase.COMPUTATION, "Enqueue 20", "Insert 20 at the rear of the queue."));

        // Enqueue 30
        queueElements.add(30);
        rear++;
        states.add(createState(queueElements, front, rear, "ENQUEUE", 30, OperationType.ENQUEUE, ExecutionPhase.COMPUTATION, "Enqueue 30", "Insert 30 at the rear of the queue."));

        // Peek Front
        states.add(createState(queueElements, front, rear, "PEEK", queueElements.get(front), OperationType.PEEK, ExecutionPhase.COMPUTATION, "Peek Front", "Peek returns the front element (10) without removing it."));

        // Dequeue
        int dequeued = queueElements.remove(0); // since it's a dynamic list visualization, we actually pop index 0, so rear decreases
        rear--;
        states.add(createState(queueElements, front, rear, "DEQUEUE", dequeued, OperationType.DEQUEUE, ExecutionPhase.COMPUTATION, "Dequeue", "Dequeue removes the front element (" + dequeued + "). Queue follows First In First Out (FIFO)."));

        // Enqueue 40
        queueElements.add(40);
        rear++;
        states.add(createState(queueElements, front, rear, "ENQUEUE", 40, OperationType.ENQUEUE, ExecutionPhase.COMPUTATION, "Enqueue 40", "Insert 40 at the rear of the queue."));

        // Dequeue
        dequeued = queueElements.remove(0);
        rear--;
        states.add(createState(queueElements, front, rear, "DEQUEUE", dequeued, OperationType.DEQUEUE, ExecutionPhase.COMPUTATION, "Dequeue", "Dequeue removes the front element (" + dequeued + ")."));

        states.add(createState(queueElements, front, rear, "COMPLETE", null, OperationType.COMPLETE, ExecutionPhase.COMPLETION, "Operations Complete", "Queue operations demonstrated successfully."));

        return states;
    }

    private ExecutionState createState(List<Integer> queueElements, int front, int rear, String activeOp, Integer highlighted, OperationType opType, ExecutionPhase phase, String stepTitle, String note) {
        QueueExecutionState queueState = QueueExecutionState.builder()
                .queueElements(new ArrayList<>(queueElements))
                .front(front)
                .rear(rear)
                .activeOperation(activeOp)
                .highlightedElement(highlighted)
                .explanation(note)
                .build();

        Map<String, Integer> vars = new HashMap<>();
        vars.put("frontIndex", front); // in our visualizer, front is always index 0 of the current list representation
        vars.put("rearIndex", rear);
        vars.put("size", queueElements.size());

        return ExecutionState.builder()
                .step(stepCount++)
                .operationType(opType)
                .executionPhase(phase)
                .stepTitle(stepTitle)
                .educationalNote(note)
                .timeComplexity("O(1) per operation")
                .spaceComplexity("O(N)")
                .variables(vars)
                .visualizationType(VisualizationType.QUEUE)
                .queueState(queueState)
                .message(note)
                .build();
    }
}
