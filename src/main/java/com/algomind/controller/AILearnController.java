package com.algomind.controller;

import com.algomind.ai.service.AIMentorService;
import com.algomind.ai.dto.AIRequest;
import com.algomind.ai.dto.AIResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mentor")
@CrossOrigin(origins = "*")
public class AILearnController {

    private final AIMentorService aiMentorService;

    public AILearnController(AIMentorService aiMentorService) {
        this.aiMentorService = aiMentorService;
    }

    @PostMapping("/analyze")
    public Mono<ResponseEntity<AIResponse>> getMentorInsights(@RequestBody AIRequest request) {
        return aiMentorService.getInsights(request)
                .map(ResponseEntity::ok);
    }
}
