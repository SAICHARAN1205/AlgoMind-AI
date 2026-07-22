package com.algomind.execution.interpreter;

import com.algomind.exception.InvalidInputException;
import com.algomind.execution.tracer.ExecutionTracer;
import com.algomind.execution.tracer.VariableTracker;
import com.algomind.model.OperationType;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SafeExecutionInterpreter {

    private static final int MAX_STEPS = 1000;

    public void interpret(String code, VariableTracker tracker, ExecutionTracer tracer) {
        String wrappedCode = "class DynamicExecution { void execute(int[] arr) { " + code + " } }";
        CompilationUnit cu = StaticJavaParser.parse(wrappedCode);
        MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();
        
        BlockStmt body = method.getBody().orElseThrow();
        
        int stepCount = 0;
        executeBlock(body, tracker, tracer, new int[]{stepCount});
    }

    private void executeBlock(BlockStmt block, VariableTracker tracker, ExecutionTracer tracer, int[] stepCount) {
        for (Statement stmt : block.getStatements()) {
            executeStatement(stmt, tracker, tracer, stepCount);
        }
    }

    private void executeStatement(Statement stmt, VariableTracker tracker, ExecutionTracer tracer, int[] stepCount) {
        if (stepCount[0]++ > MAX_STEPS) {
            throw new InvalidInputException("Execution limit exceeded. Infinite loop detected?");
        }

        int line = stmt.getBegin().map(p -> p.line).orElse(-1);

        if (stmt instanceof ExpressionStmt) {
            Expression expr = ((ExpressionStmt) stmt).getExpression();
            executeExpression(expr, tracker, tracer, line);
        } else if (stmt instanceof BlockStmt) {
            executeBlock((BlockStmt) stmt, tracker, tracer, stepCount);
        } else if (stmt instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) stmt;
            boolean condition = evaluateCondition(ifStmt.getCondition(), tracker);
            tracer.recordState(line, OperationType.CONDITION_EVALUATION, "Evaluated if condition: " + condition, tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
            
            if (condition) {
                executeStatement(ifStmt.getThenStmt(), tracker, tracer, stepCount);
            } else if (ifStmt.getElseStmt().isPresent()) {
                executeStatement(ifStmt.getElseStmt().get(), tracker, tracer, stepCount);
            }
        } else if (stmt instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) stmt;
            while (evaluateCondition(whileStmt.getCondition(), tracker)) {
                if (stepCount[0]++ > MAX_STEPS) throw new InvalidInputException("Execution limit exceeded.");
                tracer.recordState(line, OperationType.CONDITION_EVALUATION, "Evaluated while condition: true", tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
                executeStatement(whileStmt.getBody(), tracker, tracer, stepCount);
            }
            tracer.recordState(line, OperationType.CONDITION_EVALUATION, "Evaluated while condition: false", tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
        } else if (stmt instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) stmt;
            forStmt.getInitialization().forEach(init -> {
                if (init instanceof VariableDeclarationExpr) {
                    for (VariableDeclarator decl : ((VariableDeclarationExpr) init).getVariables()) {
                        String name = decl.getNameAsString();
                        int val = evaluateExpression(decl.getInitializer().orElse(new IntegerLiteralExpr("0")), tracker);
                        tracker.setVariable(name, val);
                    }
                } else {
                    executeExpression(init, tracker, tracer, line);
                }
            });

            while (true) {
                boolean condition = true;
                if (forStmt.getCompare().isPresent()) {
                    condition = evaluateCondition(forStmt.getCompare().get(), tracker);
                }
                
                if (stepCount[0]++ > MAX_STEPS) throw new InvalidInputException("Execution limit exceeded.");
                tracer.recordState(line, OperationType.CONDITION_EVALUATION, "Evaluated for condition: " + condition, tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
                
                if (!condition) break;
                
                executeStatement(forStmt.getBody(), tracker, tracer, stepCount);
                forStmt.getUpdate().forEach(update -> executeExpression(update, tracker, tracer, line));
            }
        }
    }

    private void executeExpression(Expression expr, VariableTracker tracker, ExecutionTracer tracer, int line) {
        if (expr instanceof AssignExpr) {
            AssignExpr assign = (AssignExpr) expr;
            int value = evaluateExpression(assign.getValue(), tracker);
            
            if (assign.getTarget() instanceof NameExpr) {
                String name = ((NameExpr) assign.getTarget()).getNameAsString();
                tracker.setVariable(name, value);
                tracer.recordState(line, OperationType.VARIABLE_ASSIGNMENT, "Set " + name + " = " + value, tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
            } else if (assign.getTarget() instanceof ArrayAccessExpr) {
                ArrayAccessExpr access = (ArrayAccessExpr) assign.getTarget();
                int index = evaluateExpression(access.getIndex(), tracker);
                tracker.setArrayElement(index, value);
                
                List<Integer> highlighted = new ArrayList<>();
                highlighted.add(index);
                
                tracer.recordState(line, OperationType.ARRAY_WRITE, "Set arr[" + index + "] = " + value, tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), true, highlighted);
            }
        } else if (expr instanceof VariableDeclarationExpr) {
            for (VariableDeclarator decl : ((VariableDeclarationExpr) expr).getVariables()) {
                String name = decl.getNameAsString();
                int val = evaluateExpression(decl.getInitializer().orElse(new IntegerLiteralExpr("0")), tracker);
                tracker.setVariable(name, val);
                tracer.recordState(line, OperationType.VARIABLE_ASSIGNMENT, "Declared " + name + " = " + val, tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unary = (UnaryExpr) expr;
            if (unary.getExpression() instanceof NameExpr) {
                String name = ((NameExpr) unary.getExpression()).getNameAsString();
                int current = tracker.getVariable(name);
                if (unary.getOperator() == UnaryExpr.Operator.POSTFIX_INCREMENT || unary.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT) {
                    tracker.setVariable(name, current + 1);
                    tracer.recordState(line, OperationType.VARIABLE_ASSIGNMENT, "Incremented " + name + " to " + (current + 1), tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
                } else if (unary.getOperator() == UnaryExpr.Operator.POSTFIX_DECREMENT || unary.getOperator() == UnaryExpr.Operator.PREFIX_DECREMENT) {
                    tracker.setVariable(name, current - 1);
                    tracer.recordState(line, OperationType.VARIABLE_ASSIGNMENT, "Decremented " + name + " to " + (current - 1), tracker.getArraySnapshot(), tracker.getVariablesSnapshot(), false, null);
                }
            }
        }
    }

    private boolean evaluateCondition(Expression expr, VariableTracker tracker) {
        if (expr instanceof BooleanLiteralExpr) {
            return ((BooleanLiteralExpr) expr).getValue();
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binary = (BinaryExpr) expr;
            int left = evaluateExpression(binary.getLeft(), tracker);
            int right = evaluateExpression(binary.getRight(), tracker);
            switch (binary.getOperator()) {
                case LESS: return left < right;
                case LESS_EQUALS: return left <= right;
                case GREATER: return left > right;
                case GREATER_EQUALS: return left >= right;
                case EQUALS: return left == right;
                case NOT_EQUALS: return left != right;
                default: throw new InvalidInputException("Unsupported boolean operator: " + binary.getOperator());
            }
        } else if (expr instanceof UnaryExpr && ((UnaryExpr) expr).getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT) {
            return !evaluateCondition(((UnaryExpr) expr).getExpression(), tracker);
        }
        throw new InvalidInputException("Unsupported condition expression type: " + expr.getClass().getSimpleName());
    }

    private int evaluateExpression(Expression expr, VariableTracker tracker) {
        if (expr instanceof IntegerLiteralExpr) {
            return ((IntegerLiteralExpr) expr).asNumber().intValue();
        } else if (expr instanceof NameExpr) {
            return tracker.getVariable(((NameExpr) expr).getNameAsString());
        } else if (expr instanceof ArrayAccessExpr) {
            ArrayAccessExpr access = (ArrayAccessExpr) expr;
            int index = evaluateExpression(access.getIndex(), tracker);
            return tracker.getArrayElement(index);
        } else if (expr instanceof FieldAccessExpr) {
            FieldAccessExpr field = (FieldAccessExpr) expr;
            if (field.getNameAsString().equals("length") && field.getScope().toString().equals("arr")) {
                return tracker.getArrayLength();
            }
            throw new InvalidInputException("Unsupported field access: " + expr);
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binary = (BinaryExpr) expr;
            int left = evaluateExpression(binary.getLeft(), tracker);
            int right = evaluateExpression(binary.getRight(), tracker);
            switch (binary.getOperator()) {
                case PLUS: return left + right;
                case MINUS: return left - right;
                case MULTIPLY: return left * right;
                case DIVIDE: return left / right;
                case REMAINDER: return left % right;
                default: throw new InvalidInputException("Unsupported math operator: " + binary.getOperator());
            }
        } else if (expr instanceof EnclosedExpr) {
            return evaluateExpression(((EnclosedExpr) expr).getInner(), tracker);
        }
        throw new InvalidInputException("Unsupported expression: " + expr);
    }
}
