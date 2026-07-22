import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useExecution } from '../../context/ExecutionContext';

const AIMentorSidebar = () => {
  const [activeTab, setActiveTab] = useState('hints');
  const { currentState } = useExecution();
  
  const hint = currentState?.educationalNote || "Run your code to get AI hints.";
  const timeComplexity = currentState?.timeComplexity || "O(1)";
  const spaceComplexity = currentState?.spaceComplexity || "O(1)";

  return (
    <div className="glass-panel p-4 flex flex-col gap-4 h-full">
      <div className="flex items-center gap-2 border-b border-slate-700/50 pb-3">
        <span className="w-2 h-2 rounded-full bg-cyan-400 shadow-[0_0_8px_rgba(34,211,238,0.8)] animate-pulse" />
        <h2 className="text-sm font-semibold text-slate-200">AI Mentor</h2>
      </div>

      <div className="flex gap-2">
        <button
          onClick={() => setActiveTab('hints')}
          className={`px-3 py-1 text-xs rounded-full border transition-colors ${
            activeTab === 'hints'
              ? 'bg-cyan-500/20 border-cyan-400 text-cyan-300'
              : 'bg-slate-800/50 border-slate-700/50 text-slate-400 hover:text-slate-200'
          }`}
        >
          Hints
        </button>
        <button
          onClick={() => setActiveTab('complexity')}
          className={`px-3 py-1 text-xs rounded-full border transition-colors ${
            activeTab === 'complexity'
              ? 'bg-purple-500/20 border-purple-400 text-purple-300'
              : 'bg-slate-800/50 border-slate-700/50 text-slate-400 hover:text-slate-200'
          }`}
        >
          Big-O
        </button>
        <button
          onClick={() => setActiveTab('edgecases')}
          className={`px-3 py-1 text-xs rounded-full border transition-colors ${
            activeTab === 'edgecases'
              ? 'bg-amber-500/20 border-amber-400 text-amber-300'
              : 'bg-slate-800/50 border-slate-700/50 text-slate-400 hover:text-slate-200'
          }`}
        >
          Edge Cases
        </button>
      </div>

      <div className="flex-1 overflow-y-auto pr-2 custom-scrollbar flex flex-col gap-3">
        {activeTab === 'hints' && (
          <motion.div
            key={hint}
            initial={{ opacity: 0, y: 5 }}
            animate={{ opacity: 1, y: 0 }}
            className="p-3 rounded-lg bg-slate-800/50 border border-slate-700/50 text-sm text-slate-300 leading-relaxed"
          >
            <p className="mb-2"><span className="text-cyan-400 font-semibold">Hint:</span> {hint}</p>
            {currentState && <p className="text-xs text-emerald-500 italic mt-3">Live from AI Provider.</p>}
          </motion.div>
        )}
        
        {activeTab === 'complexity' && (
          <motion.div
            key={timeComplexity}
            initial={{ opacity: 0, y: 5 }}
            animate={{ opacity: 1, y: 0 }}
            className="p-3 rounded-lg bg-slate-800/50 border border-slate-700/50 text-sm text-slate-300"
          >
            <div className="mb-3 flex justify-between">
              <div>
                <span className="text-xs text-slate-400 block mb-1">Time Complexity:</span>
                <span className="text-lg font-mono text-purple-400 font-bold">{timeComplexity}</span>
              </div>
              <div>
                <span className="text-xs text-slate-400 block mb-1">Space Complexity:</span>
                <span className="text-lg font-mono text-cyan-400 font-bold">{spaceComplexity}</span>
              </div>
            </div>
            <p className="text-slate-400 text-xs mt-2 border-t border-slate-700 pt-2">Analyzed based on current recursive call stack and loop depths.</p>
          </motion.div>
        )}

        {activeTab === 'edgecases' && (
          <motion.div
            initial={{ opacity: 0, y: 5 }}
            animate={{ opacity: 1, y: 0 }}
            className="p-3 rounded-lg bg-slate-800/50 border border-slate-700/50 text-sm text-slate-300"
          >
            <span className="text-amber-400 font-semibold block mb-2">Try testing these inputs:</span>
            <ul className="list-disc pl-4 flex flex-col gap-1 text-slate-400">
              <li>Empty array <code>[]</code></li>
              <li>Array with 1 element <code>[5]</code></li>
              <li>Array with duplicates <code>[3, 3, 3]</code></li>
            </ul>
          </motion.div>
        )}
      </div>
      
      <div className="mt-auto pt-3 border-t border-slate-700/50">
         <button className="w-full py-2 bg-slate-800 hover:bg-slate-700 border border-slate-600 rounded-lg text-sm text-slate-300 transition-colors">
            Ask Mentor a Question...
         </button>
      </div>
    </div>
  );
};

export default AIMentorSidebar;
