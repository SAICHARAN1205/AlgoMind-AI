import React from 'react';
import { useExecution } from '../../context/ExecutionContext';
import { motion, AnimatePresence } from 'framer-motion';

const StackVisualizer = () => {
  const { currentState } = useExecution();
  
  if (!currentState || !currentState.stackState) return null;
  
  const { stackElements, topIndex, activeOperation, highlightedElement } = currentState.stackState;
  
  return (
    <div className="w-full h-full flex flex-col items-center justify-center bg-slate-950 p-6 relative">
      
      {/* Background Container for Stack */}
      <div className="w-48 h-[80%] border-x-4 border-b-4 border-slate-700 rounded-b-xl relative flex flex-col-reverse justify-start p-2 gap-2 overflow-hidden shadow-inner bg-slate-900/50">
          
          {/* Top Pointer Indicator */}
          {topIndex >= 0 && (
             <div 
                 className="absolute left-[-60px] flex items-center gap-2 transition-all duration-300 z-10"
                 style={{ bottom: `${(topIndex * (48 + 8)) + 8}px` }} // 48px height + 8px gap
             >
                 <span className="text-xs font-mono font-bold text-amber-500 bg-slate-900 px-2 py-1 rounded border border-amber-900 shadow-lg">TOP</span>
                 <svg className="w-4 h-4 text-amber-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                 </svg>
             </div>
          )}

          {/* Empty State */}
          {stackElements.length === 0 && (
              <div className="absolute inset-0 flex items-center justify-center opacity-30">
                  <span className="font-mono text-slate-500 font-bold uppercase tracking-widest">Empty</span>
              </div>
          )}
          
          <AnimatePresence>
              {stackElements.map((val, idx) => {
                  const isTop = idx === topIndex;
                  const isHighlighted = activeOperation === 'PEEK' && isTop;
                  const isJustAdded = activeOperation === 'PUSH' && isTop;
                  
                  let bgClass = "bg-slate-800 border-slate-600 text-slate-300";
                  if (isHighlighted) bgClass = "bg-cyan-900/50 border-cyan-400 text-cyan-300 shadow-[0_0_15px_rgba(34,211,238,0.4)]";
                  if (isJustAdded) bgClass = "bg-emerald-900/50 border-emerald-400 text-emerald-300 shadow-[0_0_15px_rgba(16,185,129,0.4)]";

                  return (
                      <motion.div
                          key={`stack-item-${val}-${idx}`}
                          initial={{ opacity: 0, y: -50, scale: 0.9 }}
                          animate={{ opacity: 1, y: 0, scale: 1 }}
                          exit={{ opacity: 0, y: -50, scale: 0.8 }}
                          transition={{ type: 'spring', stiffness: 300, damping: 20 }}
                          className={`w-full h-12 flex-shrink-0 rounded-lg border-2 flex items-center justify-center relative z-0 transition-colors duration-300 ${bgClass}`}
                      >
                          <span className="font-mono text-lg font-bold">{val}</span>
                      </motion.div>
                  );
              })}
          </AnimatePresence>
          
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

export default StackVisualizer;
