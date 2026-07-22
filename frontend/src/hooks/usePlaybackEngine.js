import { useState, useEffect, useRef, useCallback } from 'react';

const usePlaybackEngine = (executionTimeline = []) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [playbackSpeed, setPlaybackSpeed] = useState(1); // 1x, 2x, 0.5x
  
  const timerRef = useRef(null);

  const maxIndex = Math.max(0, executionTimeline.length - 1);
  const isFinished = currentIndex >= maxIndex && executionTimeline.length > 0;
  
  const currentState = executionTimeline.length > 0 ? executionTimeline[currentIndex] : null;

  const play = useCallback(() => {
    if (isFinished) {
      setCurrentIndex(0); // Restart if finished
    }
    setIsPlaying(true);
  }, [isFinished]);

  const pause = useCallback(() => {
    setIsPlaying(false);
  }, []);

  const reset = useCallback(() => {
    setIsPlaying(false);
    setCurrentIndex(0);
  }, []);

  const stepForward = useCallback(() => {
    setIsPlaying(false);
    setCurrentIndex((prev) => Math.min(prev + 1, maxIndex));
  }, [maxIndex]);

  const stepBackward = useCallback(() => {
    setIsPlaying(false);
    setCurrentIndex((prev) => Math.max(prev - 1, 0));
  }, []);

  useEffect(() => {
    if (isPlaying && !isFinished) {
      const baseInterval = 1000;
      const currentInterval = baseInterval / playbackSpeed;
      
      timerRef.current = setInterval(() => {
        setCurrentIndex((prev) => {
          if (prev >= maxIndex) {
            setIsPlaying(false);
            return prev;
          }
          return prev + 1;
        });
      }, currentInterval);
    } else {
      if (timerRef.current) clearInterval(timerRef.current);
    }

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [isPlaying, isFinished, playbackSpeed, maxIndex]);

  return {
    currentState,
    currentIndex,
    totalSteps: executionTimeline.length,
    isPlaying,
    playbackSpeed,
    isFinished,
    actions: {
      play,
      pause,
      reset,
      stepForward,
      stepBackward,
      setPlaybackSpeed
    }
  };
};

export default usePlaybackEngine;
