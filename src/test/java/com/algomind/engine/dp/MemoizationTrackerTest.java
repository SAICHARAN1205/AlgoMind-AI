package com.algomind.engine.dp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoizationTrackerTest {

    @Test
    void testMemoizationCache() {
        MemoizationTracker tracker = new MemoizationTracker();
        
        assertFalse(tracker.hasValue(3, 5));
        
        tracker.storeValue(3, 5, 42);
        assertTrue(tracker.hasValue(3, 5));
        assertEquals(42, tracker.getValue(3, 5));
        
        assertNotNull(tracker.generateHitExplanation(3, 5));
    }
}
