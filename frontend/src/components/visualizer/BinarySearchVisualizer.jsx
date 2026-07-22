import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useExecution } from '../../context/ExecutionContext';

const BinarySearchVisualizer = () => {
  const { currentState } = useExecution();
  
  const array = currentState?.array || [1, 3, 5, 7, 9, 11, 13];
  const activeIndices = currentState?.highlightedIndices || [];
  const low = currentState?.variables?.low ?? 0;
  const high = currentState?.variables?.high ?? array.length - 1;
  const mid = currentState?.variables?.mid ?? -1;
  const target = currentState?.variables?.target ?? 7;
  const opType = currentState?.operationType;
  const found = opType === 'FOUND';

  return (
    <div className="w-full h-full flex flex-col justify-center items-center">
      
      {/* Pointers Row */}
      <div className="flex justify-center items-end gap-4 w-full px-4 mb-2 h-16">
        <AnimatePresence>
          {array.map((_, index) => {
             const isLow = index === low;
             const isHigh = index === high;
             const isMid = index === mid;

             return (
               <div key={`ptr-slot-${index}`} className="w-16 flex flex-col items-center justify-end relative">
                  {isLow && (
                     <motion.div layout layoutId="low-ptr" className="flex flex-col items-center absolute bottom-1">
                        <div className="bg-indigo-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded shadow-lg">L</div>
                        <div className="w-0 h-0 border-l-[4px] border-l-transparent border-r-[4px] border-r-transparent border-t-[6px] border-t-indigo-500"></div>
                     </motion.div>
                  )}
                  {isMid && (
                     <motion.div layout layoutId="mid-ptr" className="flex flex-col items-center absolute bottom-1 z-10">
                        <div className="bg-cyan-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded shadow-lg">M</div>
                        <div className="w-0 h-0 border-l-[4px] border-l-transparent border-r-[4px] border-r-transparent border-t-[6px] border-t-cyan-500"></div>
                     </motion.div>
                  )}
                  {isHigh && (
                     <motion.div layout layoutId="high-ptr" className="flex flex-col items-center absolute bottom-1">
                        <div className="bg-purple-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded shadow-lg">H</div>
                        <div className="w-0 h-0 border-l-[4px] border-l-transparent border-r-[4px] border-r-transparent border-t-[6px] border-t-purple-500"></div>
                     </motion.div>
                  )}
               </div>
             );
          })}
        </AnimatePresence>
      </div>

      {/* Array Row */}
      <div className="flex justify-center items-center gap-4 w-full p-4 relative">
        {array.map((value, index) => {
          const inSearchSpace = index >= low && index <= high;
          const isMidHighlight = index === mid && mid !== -1;
          
          let bgColor = inSearchSpace ? 'bg-slate-700 border-slate-600 shadow-lg text-white' : 'bg-slate-800/20 border-slate-700/20 text-slate-500 opacity-20 grayscale';
          
          if (isMidHighlight) {
              if (opType === 'COMPARE') {
                  bgColor = 'bg-amber-500 border-amber-400 shadow-[0_0_20px_rgba(251,191,36,0.5)] text-slate-950';
              } else if (opType === 'FOUND') {
                  bgColor = 'bg-emerald-500 border-emerald-400 shadow-[0_0_20px_rgba(16,185,129,0.5)] text-slate-950';
              } else if (opType === 'MID_CALCULATION') {
                  bgColor = 'bg-cyan-500 border-cyan-400 shadow-[0_0_20px_rgba(34,211,238,0.5)] text-slate-950';
              } else {
                  bgColor = 'bg-cyan-600 border-cyan-500 shadow-[0_0_15px_rgba(34,211,238,0.4)] text-white';
              }
          }

          return (
            <motion.div
              key={`bs-item-${index}`}
              layout
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              className={`relative flex flex-col items-center justify-center w-16 h-16 rounded-xl border-2 transition-all duration-300 ${bgColor}`}
            >
              <span className="font-mono font-bold text-xl">
                {value}
              </span>
              <div className="absolute -bottom-6 text-xs text-slate-500 font-mono font-bold">
                [{index}]
              </div>
            </motion.div>
          );
        })}
      </div>
      
      {/* Target Info */}
      <div className="mt-12 text-sm text-slate-400 font-mono flex items-center gap-3">
        <span className="px-3 py-1 bg-slate-800 rounded-full border border-slate-700">Target: <strong className="text-cyan-400">{target}</strong></span>
        <span className="px-3 py-1 bg-slate-800 rounded-full border border-slate-700">{currentState ? currentState.stepTitle : 'Awaiting Execution'}</span>
      </div>

      {/* Dynamic Educational Explanation Overlay */}
      <AnimatePresence>
        {currentState?.message && (
          <motion.div
            key={currentState.step}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className="absolute top-8 p-4 bg-slate-900/90 backdrop-blur border border-cyan-500/50 shadow-[0_0_30px_rgba(34,211,238,0.15)] rounded-xl max-w-md text-center z-20"
          >
            <p className="text-cyan-50 font-medium text-sm md:text-base leading-relaxed">
              {currentState.message}
            </p>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default BinarySearchVisualizer;
