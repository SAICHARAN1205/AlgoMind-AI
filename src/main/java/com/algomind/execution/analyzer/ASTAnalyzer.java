package com.algomind.execution.analyzer;

import com.algomind.execution.ast.ASTNodeInfo;
import com.algomind.model.OperationType;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ASTAnalyzer {

    public ASTNodeInfo analyze(String code) {
        // Since user provides snippets, we wrap it in a class and method to parse easily
        String wrappedCode = "class DynamicExecution { void execute(int[] arr) { " + code + " } }";
        
        CompilationUnit cu = StaticJavaParser.parse(wrappedCode);
        MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();
        
        return buildNodeInfo(method.getBody().orElseThrow());
    }

    private ASTNodeInfo buildNodeInfo(Node node) {
        ASTNodeInfo.ASTNodeInfoBuilder builder = ASTNodeInfo.builder()
                .nodeType(node.getClass().getSimpleName())
                .lineNumber(node.getBegin().map(p -> p.line).orElse(-1))
                .variablesUsed(extractVariables(node))
                .operationType(determineOperationType(node).name());

        List<ASTNodeInfo> children = new ArrayList<>();
        for (Node child : node.getChildNodes()) {
            children.add(buildNodeInfo(child));
        }
        builder.children(children);

        return builder.build();
    }

    private Set<String> extractVariables(Node node) {
        Set<String> vars = new HashSet<>();
        node.findAll(NameExpr.class).forEach(n -> vars.add(n.getNameAsString()));
        node.findAll(VariableDeclarationExpr.class).forEach(n -> 
            n.getVariables().forEach(v -> vars.add(v.getNameAsString()))
        );
        return vars;
    }

    private OperationType determineOperationType(Node node) {
        if (node instanceof ArrayAccessExpr) {
            return OperationType.ARRAY_ACCESS;
        } else if (node instanceof AssignExpr) {
            AssignExpr expr = (AssignExpr) node;
            if (expr.getTarget() instanceof ArrayAccessExpr) {
                return OperationType.ARRAY_WRITE;
            }
            return OperationType.VARIABLE_ASSIGNMENT;
        } else if (node instanceof IfStmt) {
            return OperationType.CONDITION_EVALUATION;
        } else if (node instanceof WhileStmt || node instanceof ForStmt) {
            return OperationType.LOOP_START;
        } else if (node instanceof MethodCallExpr) {
            // Very simplified recursion check
            return OperationType.RECURSIVE_CALL; 
        }
        return OperationType.VARIABLE_ASSIGNMENT; // default
    }
}
