package com.algomind.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueExecutionState {
    private List<Integer> queueElements;
    private int front;
    private int rear;
    private String activeOperation; // "ENQUEUE", "DEQUEUE", "PEEK"
    private Integer highlightedElement;
    private String explanation;
}
