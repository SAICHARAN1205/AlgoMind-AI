package com.algomind.execution.ast;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class ASTNodeInfo {
    private String nodeType;
    private int lineNumber;
    private Set<String> variablesUsed;
    private String operationType;
    private List<ASTNodeInfo> children;
}
