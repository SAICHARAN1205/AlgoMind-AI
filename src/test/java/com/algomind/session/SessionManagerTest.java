package com.algomind.session;

import com.algomind.model.ExecutionSession;
import com.algomind.model.PlaybackStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @Test
    void addSession_validSession_addsSuccessfully() {
        ExecutionSession session = new ExecutionSession();
        session.setSessionId("test-session-1");
        
        sessionManager.addSession(session);
        
        assertTrue(sessionManager.sessionExists("test-session-1"));
        Optional<ExecutionSession> retrieved = sessionManager.getSession("test-session-1");
        assertTrue(retrieved.isPresent());
        assertEquals("test-session-1", retrieved.get().getSessionId());
    }

    @Test
    void addSession_nullSessionOrId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sessionManager.addSession(null));
        
        ExecutionSession session = new ExecutionSession();
        assertThrows(IllegalArgumentException.class, () -> sessionManager.addSession(session));
    }

    @Test
    void removeSession_existingSession_removesSuccessfully() {
        ExecutionSession session = new ExecutionSession();
        session.setSessionId("test-session-2");
        sessionManager.addSession(session);
        
        sessionManager.removeSession("test-session-2");
        
        assertFalse(sessionManager.sessionExists("test-session-2"));
        assertTrue(sessionManager.getSession("test-session-2").isEmpty());
    }

    @Test
    void removeSession_nonExistingSession_doesNothing() {
        assertDoesNotThrow(() -> sessionManager.removeSession("non-existent"));
    }
}
