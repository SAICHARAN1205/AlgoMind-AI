package com.algomind.model.recursion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StackFrame {
    private String functionName;
    private Map<String, String> parameters;
    private Map<String, String> localVariables;
    private String returnAddress;
    private int depth;
}
