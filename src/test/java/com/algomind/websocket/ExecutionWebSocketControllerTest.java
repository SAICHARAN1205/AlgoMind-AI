package com.algomind.websocket;

import com.algomind.dto.ExecuteRequest;
import com.algomind.dto.PlaybackControlRequest;
import com.algomind.model.PlaybackAction;
import com.algomind.playback.ExecutionStreamingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionWebSocketControllerTest {

    @Mock
    private ExecutionStreamingService streamingService;

    private ExecutionWebSocketController controller;

    @BeforeEach
    void setUp() {
        controller = new ExecutionWebSocketController(streamingService);
    }

    @Test
    void startExecution_withValidSessionId_callsStreamingService() {
        ExecuteRequest request = new ExecuteRequest();
        String sessionId = "custom-session-id";
        
        controller.startExecution(request, sessionId);
        
        verify(streamingService, times(1)).startStreaming(eq(sessionId), eq(request));
    }

    @Test
    void startExecution_withoutSessionId_generatesSessionIdAndCallsService() {
        ExecuteRequest request = new ExecuteRequest();
        
        controller.startExecution(request, null);
        
        verify(streamingService, times(1)).startStreaming(any(String.class), eq(request));
    }

    @Test
    void handleControl_withValidRequest_callsStreamingService() {
        PlaybackControlRequest controlRequest = new PlaybackControlRequest("test-id", PlaybackAction.PAUSE, null);
        
        controller.handleControl(controlRequest);
        
        verify(streamingService, times(1)).handleControlAction(controlRequest);
    }
    
    @Test
    void handleControl_withNullSessionId_doesNothing() {
        PlaybackControlRequest controlRequest = new PlaybackControlRequest(null, PlaybackAction.PAUSE, null);
        
        controller.handleControl(controlRequest);
        
        verify(streamingService, never()).handleControlAction(any());
    }
}
