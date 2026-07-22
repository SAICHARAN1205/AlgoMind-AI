import React, { useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useExecution } from '../../context/ExecutionContext';

const BubbleSortVisualizer = () => {
  const { currentState } = useExecution();
  
  const array = currentState?.array || [34, 12, 45, 9, 88, 23, 56];
  const highlightedIndices = currentState?.highlightedIndices || [];
  const opType = currentState?.operationType;

  // Use values as ID since bubble sort array elements are distinct in our defaults.
  // This ensures smooth layout animations when elements swap.
  const mappedArray = useMemo(() => {
    return array.map(val => ({ id: `bs-item-${val}`, value: val }));
  }, [array]);

  return (
    <div className="w-full h-full flex flex-col justify-center items-center">
      <div className="flex justify-center items-end gap-3 h-64 w-full p-4 relative">
        <AnimatePresence>
          {mappedArray.map((item, index) => {
            const isActive = highlightedIndices.includes(index);
            
            let bgColor = 'bg-slate-700/80 border-slate-600 shadow-lg';
            if (isActive) {
                if (opType === 'COMPARE') {
                    bgColor = 'bg-amber-500 border-amber-400 shadow-[0_0_20px_rgba(251,191,36,0.5)] z-10';
                } else if (opType === 'SWAP') {
                    bgColor = 'bg-fuchsia-500 border-fuchsia-400 shadow-[0_0_20px_rgba(217,70,239,0.5)] z-10';
                } else {
                    bgColor = 'bg-cyan-500 border-cyan-400 shadow-[0_0_20px_rgba(34,211,238,0.5)] z-10';
                }
            } else if (opType === 'COMPLETE') {
                bgColor = 'bg-emerald-500 border-emerald-400 shadow-[0_0_20px_rgba(16,185,129,0.5)]';
            }

            return (
              <motion.div
                key={item.id}
                layout
                layoutId={item.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                transition={{ type: "spring", stiffness: 300, damping: 25 }}
                className={`relative flex flex-col items-center justify-end w-12 sm:w-16 rounded-t-xl border-2 transition-colors duration-300 ${bgColor}`}
                style={{ height: `${Math.max((item.value / Math.max(...array, 10)) * 100, 15)}%` }}
              >
                <span className={`mb-3 font-mono font-bold text-sm sm:text-base ${isActive || opType === 'COMPLETE' ? 'text-slate-950' : 'text-white'}`}>
                  {item.value}
                </span>
                <div className="absolute -bottom-7 text-xs text-slate-500 font-mono font-bold">
                  [{index}]
                </div>
              </motion.div>
            );
          })}
        </AnimatePresence>
      </div>
      <div className="mt-12 text-sm text-slate-400 font-mono flex items-center gap-3">
        <span className="px-3 py-1 bg-slate-800 rounded-full border border-slate-700 shadow-inner">
           {currentState ? currentState.stepTitle : 'Awaiting Execution'}
        </span>
      </div>
    </div>
  );
};

export default BubbleSortVisualizer;
