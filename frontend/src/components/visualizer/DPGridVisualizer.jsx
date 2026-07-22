import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const DPGridVisualizer = ({ dpTable }) => {
  if (!dpTable) return <div className="text-slate-500">No DP Table Data</div>;

  const { rows, cols, matrix, activeCell, currentTransition, completedCells } = dpTable;

  const isActive = (r, c) => activeCell && activeCell[0] === r && activeCell[1] === c;
  const isTransition = (r, c) => currentTransition && currentTransition.some(t => t[0] === r && t[1] === c);
  const isCompleted = (r, c) => completedCells && completedCells.includes(`${r},${c}`);

  return (
    <div className="w-full h-full flex flex-col items-center justify-center p-4">
      <div className="overflow-auto max-h-full max-w-full rounded-xl border border-slate-700/50 bg-slate-900/50 p-6">
        <table className="border-collapse">
          <tbody>
            {Array.from({ length: rows }).map((_, rowIndex) => (
              <tr key={`row-${rowIndex}`}>
                {Array.from({ length: cols }).map((_, colIndex) => {
                  const val = matrix[rowIndex][colIndex];
                  const active = isActive(rowIndex, colIndex);
                  const transition = isTransition(rowIndex, colIndex);
                  const completed = isCompleted(rowIndex, colIndex);

                  let bgColor = "bg-slate-800";
                  let borderColor = "border-slate-700/50";
                  let textColor = "text-slate-400";
                  let scale = 1;

                  if (active) {
                    bgColor = "bg-indigo-500/20";
                    borderColor = "border-indigo-400";
                    textColor = "text-indigo-300 font-bold";
                    scale = 1.1;
                  } else if (transition) {
                    bgColor = "bg-amber-500/20";
                    borderColor = "border-amber-400";
                    textColor = "text-amber-300 font-bold";
                    scale = 1.05;
                  } else if (completed) {
                    bgColor = "bg-slate-800/80";
                    borderColor = "border-emerald-500/50";
                    textColor = "text-emerald-400";
                  }

                  return (
                    <td key={`cell-${rowIndex}-${colIndex}`} className="p-1">
                      <motion.div
                        layout
                        initial={{ opacity: 0, scale: 0.8 }}
                        animate={{ opacity: 1, scale }}
                        transition={{ type: "spring", stiffness: 300, damping: 20 }}
                        className={`w-14 h-14 rounded flex items-center justify-center text-sm border-2 ${bgColor} ${borderColor} ${textColor} relative`}
                      >
                        {/* Dependency highlight ring */}
                        {transition && (
                          <motion.div
                            layoutId={`transition-${rowIndex}-${colIndex}`}
                            className="absolute inset-0 rounded border-2 border-amber-400/50"
                            initial={{ opacity: 0 }}
                            animate={{ opacity: [0, 1, 0] }}
                            transition={{ duration: 1.5, repeat: Infinity }}
                          />
                        )}
                        
                        {val !== null ? val : ""}
                        
                        {/* Cell Coordinate Label */}
                        <span className="absolute bottom-0 right-1 text-[8px] text-slate-600">
                          {rowIndex},{colIndex}
                        </span>
                      </motion.div>
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default DPGridVisualizer;
