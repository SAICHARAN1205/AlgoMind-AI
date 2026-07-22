package com.algomind.controller;

import com.algomind.dto.ExecuteRequest;
import com.algomind.dto.ExecuteResponse;
import com.algomind.engine.ExecutionEngine;
import com.algomind.timeline.ExecutionTimeline;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ExecutionController {

    private final ExecutionEngine executionEngine;

    @Autowired
    public ExecutionController(ExecutionEngine executionEngine) {
        this.executionEngine = executionEngine;
    }

    @PostMapping("/execute")
    public ResponseEntity<ExecuteResponse> executeAlgorithm(@Valid @RequestBody ExecuteRequest request) {
        ExecutionTimeline timeline = executionEngine.execute(request);
        ExecuteResponse response = new ExecuteResponse(timeline);
        return ResponseEntity.ok(response);
    }
}
