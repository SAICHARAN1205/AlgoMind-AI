package com.algomind.execution.validator;

import com.algomind.exception.InvalidInputException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ExecutionValidator {
    
    private static final List<String> BLACKLISTED_KEYWORDS = Arrays.asList(
            "System.", "Thread", "Runnable", "File", "java.io", "java.nio", 
            "Runtime", "Process", "ClassLoader", "Reflection", "Method",
            "Scanner", "new ", "stream()", "import "
    );

    public void validateSafeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidInputException("Code cannot be empty");
        }

        // Basic keyword scanning for safety
        for (String keyword : BLACKLISTED_KEYWORDS) {
            if (code.contains(keyword)) {
                throw new InvalidInputException(
                    "Unsafe or unsupported code detected: '" + keyword + "'. " +
                    "AlgoMind Dynamic Execution only supports beginner DSA constructs (variables, arrays, loops, basic math)."
                );
            }
        }
        
        // Prevent code that is too long
        if (code.length() > 2000) {
             throw new InvalidInputException("Code exceeds maximum allowed length for dynamic execution.");
        }
    }
}
