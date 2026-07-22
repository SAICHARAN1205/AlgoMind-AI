import React from 'react';
import { useExecution } from '../../context/ExecutionContext';

const VariablesPanel = () => {
  const { currentState } = useExecution();
  const variables = currentState?.variables || {};
  return (
    <div className="glass-panel p-4 flex flex-col gap-3 h-full">
      <div className="flex justify-between items-center border-b border-slate-700/50 pb-2">
        <h2 className="text-sm font-semibold text-slate-300">Variables</h2>
      </div>
      <div className="flex-1 flex flex-col gap-2 bg-slate-900/50 rounded-lg border border-slate-700/50 p-3 overflow-y-auto">
        {!variables || Object.keys(variables).length === 0 ? (
          <span className="text-slate-500 font-mono text-sm text-center italic mt-4">No active variables</span>
        ) : (
          Object.entries(variables).map(([key, value]) => (
            <div key={key} className="flex justify-between items-center bg-slate-800/80 px-3 py-2 rounded border border-slate-700/50">
              <span className="text-indigo-400 font-mono text-sm">{key}</span>
              <span className="text-emerald-400 font-mono text-sm font-semibold">{value}</span>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default VariablesPanel;
