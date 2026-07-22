import React, { useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useExecution } from '../../context/ExecutionContext';

const ArrayVisualizer = () => {
  const { currentState } = useExecution();
  
  const array = currentState?.array || [5, 2, 8, 1, 9, 3];
  const activeIndices = currentState?.activeIndices || [];
  const highlightedIndex = currentState?.highlightedIndex ?? -1;
  const isSorted = currentState?.isSorted || false;

  // Assign stable IDs based on value (assuming no duplicates for simple visualizer, 
  // or you could track original positions if needed. We'll use a hack to append index for duplicates 
  // but keep the key stable per value if they move.)
  // Actually, to animate swaps properly, we need the initial array to assign unique IDs, 
  // or we can just use the value as the ID assuming distinct elements for the sample.
  const mappedArray = useMemo(() => {
    // If elements are guaranteed distinct, value can be ID.
    // If there are duplicates, we'd need a more robust tracking mechanism.
    // For MVP Bubble Sort [34, 12, 45, 9, 88, 23, 56], all values are unique.
    return array.map(val => ({ id: `item-${val}`, value: val }));
  }, [array]);

  return (
    <div className="w-full h-full flex flex-col justify-center items-center">
      <div className="flex justify-center items-end gap-2 h-48 w-full p-4">
        <AnimatePresence>
          {mappedArray.map((item, index) => (
            <motion.div
              key={item.id}
              layout
              layoutId={item.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              whileHover={{ scale: 1.05 }}
              className={`relative flex flex-col items-center justify-end w-12 sm:w-16 rounded-t-md transition-colors ${
                activeIndices.includes(index) || index === highlightedIndex
                  ? 'bg-indigo-500'
                  : isSorted ? 'bg-emerald-500' : 'bg-slate-600'
              }`}
              style={{ height: `${Math.max((item.value / Math.max(...array, 10)) * 100, 15)}%` }}
            >
              <span className="mb-2 font-mono font-bold text-white text-sm sm:text-base">
                {item.value}
              </span>
              <div className="absolute -bottom-6 text-xs text-slate-500 font-mono font-bold">
                [{index}]
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
      <div className="mt-8 text-sm text-slate-400 font-mono">
        Status: {currentState ? currentState.stepTitle : 'Awaiting Execution'}
      </div>
    </div>
  );
};

export default ArrayVisualizer;
