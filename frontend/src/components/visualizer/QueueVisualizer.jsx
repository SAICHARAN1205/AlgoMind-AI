import React from 'react';
import { useExecution } from '../../context/ExecutionContext';
import { motion, AnimatePresence } from 'framer-motion';

const QueueVisualizer = () => {
  const { currentState } = useExecution();
  
  if (!currentState || !currentState.queueState) return null;
  
  const { queueElements, front, rear, activeOperation, highlightedElement } = currentState.queueState;
  
  return (
    <div className="w-full h-full flex flex-col items-center justify-center bg-slate-950 p-6 relative">
      
      {/* Container for Queue */}
      <div className="w-[80%] h-32 flex items-center justify-start gap-4 p-4 overflow-x-hidden relative border-y-4 border-slate-700 bg-slate-900/50 shadow-inner">
          
          {/* Front Pointer (Fixed to the left) */}
          <div className="absolute left-4 top-2 flex flex-col items-center z-10">
              <span className="text-[10px] font-mono font-bold text-amber-500 bg-slate-900 px-2 py-0.5 rounded-t border-x border-t border-amber-900">FRONT</span>
              <div className="w-0.5 h-32 bg-amber-500/50"></div>
          </div>
          
          {/* Rear Pointer (Moves with the last element) */}
          {queueElements.length > 0 && (
              <motion.div 
                  initial={false}
                  animate={{ left: `${(queueElements.length * (64 + 16)) + 16}px` }} // 64px width + 16px gap
                  className="absolute top-2 flex flex-col items-center z-10 transition-all duration-300"
              >
                  <span className="text-[10px] font-mono font-bold text-rose-500 bg-slate-900 px-2 py-0.5 rounded-t border-x border-t border-rose-900">REAR</span>
                  <div className="w-0.5 h-32 bg-rose-500/50"></div>
              </motion.div>
          )}

          {/* Empty State */}
          {queueElements.length === 0 && (
              <div className="absolute inset-0 flex items-center justify-center opacity-30">
                  <span className="font-mono text-slate-500 font-bold uppercase tracking-widest">Empty</span>
              </div>
          )}
          
          {/* Queue Elements */}
          <div className="flex gap-4 items-center z-0 ml-8">
              <AnimatePresence mode="popLayout">
                  {queueElements.map((val, idx) => {
                      const isFront = idx === front; // front is always 0 in our logic
                      const isHighlighted = activeOperation === 'PEEK' && isFront;
                      const isJustAdded = activeOperation === 'ENQUEUE' && idx === queueElements.length - 1;
                      
                      let bgClass = "bg-slate-800 border-slate-600 text-slate-300";
                      if (isHighlighted) bgClass = "bg-cyan-900/50 border-cyan-400 text-cyan-300 shadow-[0_0_15px_rgba(34,211,238,0.4)]";
                      if (isJustAdded) bgClass = "bg-emerald-900/50 border-emerald-400 text-emerald-300 shadow-[0_0_15px_rgba(16,185,129,0.4)]";

                      return (
                          <motion.div
                              layout
                              key={`queue-item-${val}-${idx}`}
                              initial={{ opacity: 0, x: 50, scale: 0.9 }}
                              animate={{ opacity: 1, x: 0, scale: 1 }}
                              exit={{ opacity: 0, x: -50, scale: 0.8 }}
                              transition={{ type: 'spring', stiffness: 300, damping: 20 }}
                              className={`w-16 h-16 flex-shrink-0 rounded-lg border-2 flex items-center justify-center relative transition-colors duration-300 ${bgClass}`}
                          >
                              <span className="font-mono text-xl font-bold">{val}</span>
                          </motion.div>
                      );
                  })}
              </AnimatePresence>
          </div>
          
      </div>

      {/* Explanation Banner */}
      <div className="absolute bottom-6 bg-slate-900/80 backdrop-blur-sm border border-slate-700 px-6 py-3 rounded-full shadow-lg">
          <p className="text-sm font-medium text-slate-300">
              <span className="text-purple-400 font-bold mr-2">Operation:</span> 
              {activeOperation}
          </p>
      </div>
      
    </div>
  );
};

export default QueueVisualizer;
