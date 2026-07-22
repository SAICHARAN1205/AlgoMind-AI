package com.algomind.model.recursion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecursionNode {
    private String nodeId;
    private String functionName;
    private Map<String, String> parameters;
    private String returnValue;
    private int depth;
    private String parentId;
    private List<String> childrenIds;
    private ExecutionStatus executionStatus;
    private String educationalNote;
}
