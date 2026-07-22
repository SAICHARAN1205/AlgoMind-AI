package com.algomind.session;

import com.algomind.model.ExecutionSession;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final ConcurrentHashMap<String, ExecutionSession> activeSessions = new ConcurrentHashMap<>();

    public void addSession(ExecutionSession session) {
        if (session == null || session.getSessionId() == null) {
            throw new IllegalArgumentException("Session or Session ID cannot be null");
        }
        activeSessions.put(session.getSessionId(), session);
    }

    public Optional<ExecutionSession> getSession(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(activeSessions.get(sessionId));
    }

    public void removeSession(String sessionId) {
        if (sessionId != null) {
            activeSessions.remove(sessionId);
        }
    }
    
    public boolean sessionExists(String sessionId) {
        return sessionId != null && activeSessions.containsKey(sessionId);
    }
}
