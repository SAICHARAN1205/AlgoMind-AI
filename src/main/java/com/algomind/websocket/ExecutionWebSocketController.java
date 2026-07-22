package com.algomind.websocket;

import com.algomind.dto.ExecuteRequest;
import com.algomind.dto.PlaybackControlRequest;
import com.algomind.playback.ExecutionStreamingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
public class ExecutionWebSocketController {

    private final ExecutionStreamingService streamingService;

    public ExecutionWebSocketController(ExecutionStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    /**
     * Endpoint to start an execution. Clients can optionally pass a specific sessionId in headers
     * or the server will generate one.
     */
    @MessageMapping("/start")
    public void startExecution(@Payload @Valid ExecuteRequest request, 
                               @Header(value = "sessionId", defaultValue = "") String sessionIdHeader) {
                               
        String sessionId = sessionIdHeader;
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        
        log.info("Received start request for session: {}", sessionId);
        streamingService.startStreaming(sessionId, request);
    }

    /**
     * Endpoint to handle playback controls (pause, play, speed changes, etc).
     */
    @MessageMapping("/control")
    public void handleControl(@Payload PlaybackControlRequest controlRequest) {
        if (controlRequest == null || controlRequest.getSessionId() == null) {
            log.warn("Invalid control request received");
            return;
        }
        log.info("Received control action {} for session: {}", controlRequest.getAction(), controlRequest.getSessionId());
        streamingService.handleControlAction(controlRequest);
    }
}
