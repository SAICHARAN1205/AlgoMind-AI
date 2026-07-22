package com.algomind.playback;

import com.algomind.dto.ExecuteRequest;
import com.algomind.dto.ExecutionMessage;
import com.algomind.dto.PlaybackControlRequest;
import com.algomind.engine.ExecutionEngine;
import com.algomind.model.ExecutionSession;
import com.algomind.model.ExecutionState;
import com.algomind.model.PlaybackStatus;
import com.algomind.session.SessionManager;
import com.algomind.timeline.ExecutionTimeline;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ExecutionStreamingService {

    private static final long DEFAULT_DELAY_MS = 1000;
    
    private final ExecutionEngine executionEngine;
    private final SessionManager sessionManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final ExecutorService executorService;

    public ExecutionStreamingService(ExecutionEngine executionEngine, 
                                     SessionManager sessionManager, 
                                     SimpMessagingTemplate messagingTemplate) {
        this.executionEngine = executionEngine;
        this.sessionManager = sessionManager;
        this.messagingTemplate = messagingTemplate;
        // Using a cached thread pool to handle concurrent users efficiently
        this.executorService = Executors.newCachedThreadPool();
    }

    public void startStreaming(String sessionId, ExecuteRequest request) {
        log.info("Starting execution stream for session: {}", sessionId);
        
        try {
            ExecutionTimeline timeline = executionEngine.execute(request);
            
            ExecutionSession session = ExecutionSession.builder()
                    .sessionId(sessionId)
                    .algorithmName(timeline.getAlgorithmName())
                    .currentStep(0)
                    .totalSteps(timeline.getTotalSteps())
                    .playbackStatus(PlaybackStatus.PLAYING)
                    .playbackSpeed(1.0)
                    .createdAt(LocalDateTime.now())
                    .executionTimeline(timeline)
                    .build();
            
            sessionManager.addSession(session);
            
            // Submit the streaming task to a background thread
            executorService.submit(() -> streamExecution(sessionId));
            
        } catch (Exception e) {
            log.error("Failed to start execution for session {}: {}", sessionId, e.getMessage());
            sendError(sessionId, "Failed to start execution: " + e.getMessage());
        }
    }

    public void handleControlAction(PlaybackControlRequest controlRequest) {
        String sessionId = controlRequest.getSessionId();
        sessionManager.getSession(sessionId).ifPresentOrElse(session -> {
            switch (controlRequest.getAction()) {
                case PLAY:
                case RESUME:
                    if (session.getPlaybackStatus() == PlaybackStatus.PAUSED) {
                        session.setPlaybackStatus(PlaybackStatus.PLAYING);
                    }
                    break;
                case PAUSE:
                    session.setPlaybackStatus(PlaybackStatus.PAUSED);
                    break;
                case STOP:
                    session.setPlaybackStatus(PlaybackStatus.STOPPED);
                    break;
                case NEXT_STEP:
                    if (session.getPlaybackStatus() == PlaybackStatus.PAUSED && 
                        session.getCurrentStep() < session.getTotalSteps() - 1) {
                        session.setCurrentStep(session.getCurrentStep() + 1);
                        sendCurrentState(session);
                    }
                    break;
                case PREVIOUS_STEP:
                    if (session.getPlaybackStatus() == PlaybackStatus.PAUSED && 
                        session.getCurrentStep() > 0) {
                        session.setCurrentStep(session.getCurrentStep() - 1);
                        sendCurrentState(session);
                    }
                    break;
                case CHANGE_SPEED:
                    if (controlRequest.getSpeed() != null && controlRequest.getSpeed() > 0) {
                        session.setPlaybackSpeed(controlRequest.getSpeed());
                    }
                    break;
            }
        }, () -> log.warn("Control action requested for unknown session: {}", sessionId));
    }

    private void streamExecution(String sessionId) {
        try {
            while (sessionManager.sessionExists(sessionId)) {
                ExecutionSession session = sessionManager.getSession(sessionId).orElse(null);
                
                if (session == null || session.getPlaybackStatus() == PlaybackStatus.STOPPED) {
                    sessionManager.removeSession(sessionId);
                    break;
                }
                
                if (session.getPlaybackStatus() == PlaybackStatus.COMPLETED) {
                    break;
                }

                if (session.getPlaybackStatus() == PlaybackStatus.PAUSED) {
                    Thread.sleep(100); // Sleep briefly while paused
                    continue;
                }

                if (session.getCurrentStep() < session.getTotalSteps()) {
                    sendCurrentState(session);
                    
                    session.setCurrentStep(session.getCurrentStep() + 1);
                    
                    if (session.getCurrentStep() >= session.getTotalSteps()) {
                        session.setPlaybackStatus(PlaybackStatus.COMPLETED);
                        sendCurrentState(session); // Send the completed state
                        sessionManager.removeSession(sessionId);
                        break;
                    }
                }

                long sleepTime = (long) (DEFAULT_DELAY_MS / session.getPlaybackSpeed());
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            log.warn("Streaming interrupted for session: {}", sessionId);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Error during streaming for session {}: {}", sessionId, e.getMessage(), e);
            sendError(sessionId, "Streaming encountered an error: " + e.getMessage());
        }
    }

    private void sendCurrentState(ExecutionSession session) {
        int index = Math.min(session.getCurrentStep(), session.getTotalSteps() - 1);
        ExecutionState state = session.getExecutionTimeline().getExecutionStates().get(index);
        
        ExecutionMessage message = ExecutionMessage.builder()
                .sessionId(session.getSessionId())
                .currentStep(session.getCurrentStep())
                .totalSteps(session.getTotalSteps())
                .executionState(state)
                .playbackStatus(session.getPlaybackStatus())
                .timestamp(System.currentTimeMillis())
                .build();
                
        messagingTemplate.convertAndSend("/topic/execution/" + session.getSessionId(), message);
    }
    
    private void sendError(String sessionId, String errorMessage) {
        ExecutionMessage errorMsg = ExecutionMessage.builder()
                .sessionId(sessionId)
                .playbackStatus(PlaybackStatus.STOPPED)
                .executionState(ExecutionState.builder()
                        .message(errorMessage)
                        .build())
                .timestamp(System.currentTimeMillis())
                .build();
        messagingTemplate.convertAndSend("/topic/execution/" + sessionId, errorMsg);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down ExecutionStreamingService executor");
        executorService.shutdownNow();
    }
}
