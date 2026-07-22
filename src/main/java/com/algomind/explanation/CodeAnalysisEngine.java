package com.algomind.explanation;

import com.algomind.execution.ast.ASTNodeInfo;
import com.algomind.model.OperationType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("explanationCodeAnalysisEngine")
public class CodeAnalysisEngine {

    public String estimateComplexity(ASTNodeInfo root) {
        int maxDepth = findMaxLoopDepth(root, 0);
        
        if (maxDepth == 0) {
            return "O(1) - Constant Time";
        } else if (maxDepth == 1) {
            // Simplified check, could be O(log n) if binary search pattern is detected
            return "O(n) - Linear Time";
        } else if (maxDepth == 2) {
            return "O(n²) - Quadratic Time";
        } else {
            return "O(n^" + maxDepth + ") - Polynomial Time";
        }
    }
    
    private int findMaxLoopDepth(ASTNodeInfo node, int currentDepth) {
        int nextDepth = currentDepth;
        if (node.getOperationType() != null && node.getOperationType().equals(OperationType.LOOP_START.name())) {
            nextDepth++;
        }
        
        int maxDepth = nextDepth;
        if (node.getChildren() != null) {
            for (ASTNodeInfo child : node.getChildren()) {
                maxDepth = Math.max(maxDepth, findMaxLoopDepth(child, nextDepth));
            }
        }
        return maxDepth;
    }

    public Map<String, String> generateInsights(ASTNodeInfo root) {
        Map<String, String> insights = new HashMap<>();
        
        boolean hasLoop = hasNodeType(root, "WhileStmt") || hasNodeType(root, "ForStmt");
        boolean hasArrayWrite = hasOperationType(root, OperationType.ARRAY_WRITE.name());
        boolean hasCondition = hasOperationType(root, OperationType.CONDITION_EVALUATION.name());
        
        if (hasLoop) {
            insights.put("Loop Structure", "The code uses a loop to iterate over variables or array elements. This allows repeated execution until the condition fails.");
        }
        if (hasArrayWrite) {
            insights.put("Array Mutation", "The algorithm modifies the array in-place, changing its state over time (e.g., swapping or overwriting values).");
        }
        if (hasCondition) {
            insights.put("Conditional Logic", "The code makes decisions based on certain conditions, leading to different execution paths.");
        }
        
        if (insights.isEmpty()) {
            insights.put("General", "This is a straight-line algorithm with basic operations.");
        }
        
        return insights;
    }
    
    private boolean hasNodeType(ASTNodeInfo node, String type) {
        if (type.equals(node.getNodeType())) return true;
        if (node.getChildren() != null) {
            for (ASTNodeInfo child : node.getChildren()) {
                if (hasNodeType(child, type)) return true;
            }
        }
        return false;
    }
    
    private boolean hasOperationType(ASTNodeInfo node, String opType) {
        if (opType.equals(node.getOperationType())) return true;
        if (node.getChildren() != null) {
            for (ASTNodeInfo child : node.getChildren()) {
                if (hasOperationType(child, opType)) return true;
            }
        }
        return false;
    }
}
