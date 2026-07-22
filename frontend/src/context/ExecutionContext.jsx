import React, { createContext, useContext, useState, useEffect } from 'react';
import usePlaybackEngine from '../hooks/usePlaybackEngine';
import WebSocketService from '../services/WebSocketService';
import { ExecutionService } from '../services/ExecutionService';
import { toast } from 'react-hot-toast';

const ExecutionContext = createContext(null);

export const ExecutionProvider = ({ children }) => {
  const [code, setCode] = useState('bubbleSort([5, 2, 9, 1, 5, 6]);');
  const [executionTimeline, setExecutionTimeline] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId] = useState(`session-${Math.random().toString(36).substring(7)}`);

  const playback = usePlaybackEngine(executionTimeline);

  // Initialize WebSocket connection
  useEffect(() => {
    WebSocketService.connect(
      () => console.log('Connected to WS execution stream'),
      (err) => console.warn('WS connection failed, relying on REST', err)
    );

    return () => WebSocketService.disconnect();
  }, []);

  const executeCode = async (submittedCode) => {
    setIsLoading(true);
    setExecutionTimeline([]);
    playback.actions.reset();
    
    // Subscribe to real-time execution states
    WebSocketService.subscribeToExecution(sessionId, (state) => {
      setExecutionTimeline(prev => {
         const newTimeline = [...prev, state];
         // Auto-play when first states arrive
         if (newTimeline.length === 1 && !playback.isPlaying) {
             playback.actions.play();
         }
         return newTimeline;
      });
    });

    try {
      // Trigger execution
      const result = await ExecutionService.executeCode(submittedCode || code, sessionId);
      
      // If WS failed or returned empty array, use REST result
      if (executionTimeline.length === 0 && result && result.length > 0) {
        setExecutionTimeline(result);
        playback.actions.play();
      }
      
      toast.success('Execution generated successfully');
    } catch (error) {
      toast.error(error?.response?.data?.message || 'Failed to execute code');
      WebSocketService.unsubscribe(`/topic/execution/${sessionId}`);
    } finally {
      setIsLoading(false);
    }
  };

  const loadPrebuiltExecution = async (algorithmId) => {
    setIsLoading(true);
    setExecutionTimeline([]);
    playback.actions.reset();
    
    try {
      const result = await ExecutionService.getLearnAlgorithm(algorithmId);
      if (result && result.length > 0) {
        setExecutionTimeline(result);
        playback.actions.play();
        toast.success(`${algorithmId.replace('-', ' ')} loaded successfully`);
      }
    } catch (error) {
      toast.error(error?.response?.data?.message || 'Failed to load algorithm');
    } finally {
      setIsLoading(false);
    }
  };

  const value = {
    code,
    setCode,
    executionTimeline,
    setExecutionTimeline,
    isLoading,
    setIsLoading,
    executeCode,
    loadPrebuiltExecution,
    ...playback
  };

  return (
    <ExecutionContext.Provider value={value}>
      {children}
    </ExecutionContext.Provider>
  );
};

export const useExecution = () => useContext(ExecutionContext);
