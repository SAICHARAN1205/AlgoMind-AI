import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const AIMentorPanel = ({ insights, isLoading }) => {
  return (
    <div className="w-full h-full flex flex-col bg-slate-900 border border-purple-500/30 rounded-xl overflow-hidden shadow-[0_0_20px_rgba(168,85,247,0.15)] relative">
      
      {/* Header */}
      <div className="bg-slate-950 p-3 border-b border-purple-900/50 flex items-center justify-between">
        <h3 className="text-sm font-bold text-purple-400 flex items-center gap-2">
          <svg className="w-5 h-5 text-purple-500 animate-pulse" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
          </svg>
          AI Mentor
        </h3>
        {isLoading && (
            <span className="text-[10px] uppercase font-bold tracking-widest text-purple-500 animate-pulse">Thinking...</span>
        )}
      </div>

      {/* Content Area */}
      <div className="flex-1 p-3 overflow-y-auto space-y-3 relative">
        
        {isLoading && (
            <motion.div 
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                className="absolute inset-0 bg-slate-900 z-10 p-3 space-y-3"
            >
                <div className="w-full h-16 bg-slate-800 rounded-lg animate-pulse"></div>
                <div className="w-full h-24 bg-slate-800 rounded-lg animate-pulse"></div>
                <div className="w-full h-20 bg-slate-800 rounded-lg animate-pulse"></div>
                <div className="w-full h-16 bg-slate-800 rounded-lg animate-pulse"></div>
                <div className="w-3/4 h-16 bg-slate-800 rounded-lg animate-pulse"></div>
            </motion.div>
        )}

        <AnimatePresence>
            {!isLoading && insights && (
                <motion.div 
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.3 }}
                    className="space-y-3"
                >
                    {/* Summary */}
                    {insights.summary && (
                        <div className="bg-purple-900/20 border border-purple-500/20 rounded-lg p-3">
                            <p className="text-sm text-purple-200 leading-tight">
                                {insights.summary}
                            </p>
                        </div>
                    )}
                    
                    {/* Error Explanation (If any) */}
                    {insights.errorExplanation && insights.errorExplanation.length > 0 && (
                        <div className="bg-rose-900/20 border border-rose-500/30 rounded-lg p-3 shadow-inner">
                            <h4 className="text-[10px] uppercase font-bold text-rose-500 mb-1 flex items-center gap-1">
                                <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                                Issue Detected
                            </h4>
                            <p className="text-xs text-rose-300 leading-snug font-medium">
                                {insights.errorExplanation}
                            </p>
                        </div>
                    )}

                    {/* Hint */}
                    {insights.hint && (
                        <div className="bg-amber-900/20 border border-amber-500/30 rounded-lg p-3">
                            <h4 className="text-[10px] uppercase font-bold text-amber-500 mb-1">💡 Learning Hint</h4>
                            <p className="text-xs text-amber-200 leading-snug">
                                {insights.hint}
                            </p>
                        </div>
                    )}

                    {/* Improvement Suggestion */}
                    {insights.improvementSuggestion && (
                        <div className="bg-blue-900/20 border border-blue-500/30 rounded-lg p-3 shadow-inner">
                            <h4 className="text-[10px] uppercase font-bold text-blue-500 mb-1 flex items-center gap-1">
                                <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
                                Code Improvement
                            </h4>
                            <p className="text-xs text-blue-300 leading-snug">
                                {insights.improvementSuggestion}
                            </p>
                        </div>
                    )}

                    {/* Optimization Hint */}
                    {insights.optimizationHint && (
                        <div className="bg-fuchsia-900/20 border border-fuchsia-500/30 rounded-lg p-3">
                            <h4 className="text-[10px] uppercase font-bold text-fuchsia-500 mb-1">🚀 Optimization</h4>
                            <p className="text-xs text-fuchsia-200 leading-snug">
                                {insights.optimizationHint}
                            </p>
                        </div>
                    )}

                    {/* Complexity */}
                    {insights.complexityExplanation && (
                        <div className="bg-cyan-900/20 border border-cyan-500/30 rounded-lg p-3">
                            <h4 className="text-[10px] uppercase font-bold text-cyan-500 mb-1">⏱️ Complexity Breakdown</h4>
                            <p className="text-xs text-cyan-200 leading-snug">
                                {insights.complexityExplanation}
                            </p>
                        </div>
                    )}

                    {/* Edge Cases */}
                    {insights.edgeCaseExplanation && (
                        <div className="bg-emerald-900/20 border border-emerald-500/30 rounded-lg p-3">
                            <h4 className="text-[10px] uppercase font-bold text-emerald-500 mb-1">🔍 Edge Cases to Consider</h4>
                            <p className="text-xs text-emerald-200 leading-snug">
                                {insights.edgeCaseExplanation}
                            </p>
                        </div>
                    )}

                </motion.div>
            )}

            {!isLoading && !insights && (
                <div className="flex flex-col items-center justify-center h-40 text-center opacity-50">
                    <svg className="w-8 h-8 text-slate-500 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M13 10V3L4 14h7v7l9-11h-7z" />
                    </svg>
                    <p className="text-xs text-slate-400 font-mono">Run your code to get AI insights.</p>
                </div>
            )}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default AIMentorPanel;
