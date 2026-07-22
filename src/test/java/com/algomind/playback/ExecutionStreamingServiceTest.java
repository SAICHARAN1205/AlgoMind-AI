package com.algomind.playback;

import com.algomind.dto.ExecuteRequest;
import com.algomind.dto.ExecutionMessage;
import com.algomind.dto.PlaybackControlRequest;
import com.algomind.engine.ExecutionEngine;
import com.algomind.model.ExecutionSession;
import com.algomind.model.ExecutionState;
import com.algomind.model.PlaybackAction;
import com.algomind.model.PlaybackStatus;
import com.algomind.session.SessionManager;
import com.algomind.timeline.ExecutionTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionStreamingServiceTest {

    @Mock
    private ExecutionEngine executionEngine;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private ExecutionStreamingService streamingService;

    @BeforeEach
    void setUp() {
        streamingService = new ExecutionStreamingService(executionEngine, sessionManager, messagingTemplate);
    }

    @Test
    void handleControlAction_pauseAndPlay_updatesSessionStatus() {
        String sessionId = "test-session";
        ExecutionSession session = ExecutionSession.builder()
                .sessionId(sessionId)
                .playbackStatus(PlaybackStatus.PLAYING)
                .build();
                
        when(sessionManager.getSession(sessionId)).thenReturn(Optional.of(session));

        PlaybackControlRequest pauseRequest = new PlaybackControlRequest(sessionId, PlaybackAction.PAUSE, null);
        streamingService.handleControlAction(pauseRequest);
        assertEquals(PlaybackStatus.PAUSED, session.getPlaybackStatus());

        PlaybackControlRequest playRequest = new PlaybackControlRequest(sessionId, PlaybackAction.PLAY, null);
        streamingService.handleControlAction(playRequest);
        assertEquals(PlaybackStatus.PLAYING, session.getPlaybackStatus());
    }

    @Test
    void handleControlAction_changeSpeed_updatesSessionSpeed() {
        String sessionId = "test-session";
        ExecutionSession session = ExecutionSession.builder()
                .sessionId(sessionId)
                .playbackSpeed(1.0)
                .build();
                
        when(sessionManager.getSession(sessionId)).thenReturn(Optional.of(session));

        PlaybackControlRequest speedRequest = new PlaybackControlRequest(sessionId, PlaybackAction.CHANGE_SPEED, 2.5);
        streamingService.handleControlAction(speedRequest);
        
        assertEquals(2.5, session.getPlaybackSpeed());
    }

    @Test
    void handleControlAction_stop_updatesSessionStatus() {
        String sessionId = "test-session";
        ExecutionSession session = ExecutionSession.builder()
                .sessionId(sessionId)
                .playbackStatus(PlaybackStatus.PLAYING)
                .build();
                
        when(sessionManager.getSession(sessionId)).thenReturn(Optional.of(session));

        PlaybackControlRequest stopRequest = new PlaybackControlRequest(sessionId, PlaybackAction.STOP, null);
        streamingService.handleControlAction(stopRequest);
        
        assertEquals(PlaybackStatus.STOPPED, session.getPlaybackStatus());
    }
}
