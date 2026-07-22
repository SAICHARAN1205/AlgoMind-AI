import React from 'react';
import { Play, Pause, Square, SkipBack, SkipForward, FastForward } from 'lucide-react';
import { useExecution } from '../../context/ExecutionContext';

const PlaybackControls = () => {
  const { isPlaying, isFinished, actions, playbackSpeed } = useExecution();

  return (
    <div className="flex items-center gap-4 bg-slate-800/80 p-3 rounded-xl border border-slate-700/50">
      
      <button 
        onClick={actions.stepBackward}
        className="p-2 rounded-lg bg-slate-700 hover:bg-slate-600 transition-colors text-slate-300"
      >
        <SkipBack size={18} fill="currentColor" />
      </button>

      {isPlaying ? (
        <button 
          onClick={actions.pause}
          className="p-3 rounded-full bg-rose-500 hover:bg-rose-600 text-white shadow-lg shadow-rose-500/20 transition-all"
        >
          <Pause size={20} fill="currentColor" />
        </button>
      ) : (
        <button 
          onClick={actions.play}
          className="p-3 rounded-full bg-emerald-500 hover:bg-emerald-600 text-white shadow-lg shadow-emerald-500/20 transition-all"
        >
          <Play size={20} fill="currentColor" className="ml-1" />
        </button>
      )}

      <button 
        onClick={actions.reset}
        className="p-2 rounded-lg bg-slate-700 hover:bg-slate-600 transition-colors text-slate-300"
      >
        <Square size={18} fill="currentColor" />
      </button>

      <button 
        onClick={actions.stepForward}
        className="p-2 rounded-lg bg-slate-700 hover:bg-slate-600 transition-colors text-slate-300"
      >
        <SkipForward size={18} fill="currentColor" />
      </button>

      <div className="w-px h-6 bg-slate-600 mx-2"></div>

      <button 
        onClick={() => actions.setPlaybackSpeed(playbackSpeed === 1 ? 2 : playbackSpeed === 2 ? 0.5 : 1)}
        className="flex items-center gap-1 p-2 rounded-lg bg-slate-700 hover:bg-slate-600 transition-colors text-slate-300 text-xs font-mono font-bold"
      >
        <FastForward size={14} />
        {playbackSpeed}x
      </button>

    </div>
  );
};

export default PlaybackControls;
