import React, { useMemo, useEffect, useState } from 'react';
import { useExecution } from '../../context/ExecutionContext';
import { ReactFlow, Background, Controls, MarkerType } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { motion, AnimatePresence } from 'framer-motion';

// Custom tree node for Fibonacci
const FibNode = ({ data }) => {
  const { isActive, isCompleted, type, explanation, n, val, dpMode } = data;
  
  const isCacheHit = type === 'CACHE_HIT';
  const isBaseCase = type === 'BASE_CASE';
  
  let borderColor = 'border-slate-700';
  let glow = '';
  let bgColor = 'bg-slate-900';
  
  if (isActive) {
    if (isCacheHit) {
      borderColor = 'border-amber-400';
      glow = 'shadow-[0_0_20px_rgba(251,191,36,0.6)]';
      bgColor = 'bg-amber-900/30';
    } else {
      borderColor = 'border-cyan-400';
      glow = 'shadow-[0_0_15px_rgba(34,211,238,0.5)]';
    }
  } else if (isCompleted) {
    borderColor = 'border-emerald-500/50';
    bgColor = 'bg-slate-800';
  }

  return (
    <div className={`p-3 rounded-xl border-2 transition-all duration-300 ${glow} ${bgColor} ${borderColor} min-w-[100px] text-center ${isActive ? 'scale-110 z-10' : 'opacity-90'}`}>
      <div className="font-mono text-sm font-semibold text-slate-200 mb-1">
        fib({n})
      </div>
      {(val !== undefined && val !== -1) && (
        <div className={`text-xs font-mono font-bold px-2 py-1 rounded ${isCacheHit ? 'bg-amber-500/20 text-amber-300' : 'bg-emerald-500/20 text-emerald-300'}`}>
          = {val}
        </div>
      )}
      {isCacheHit && isActive && (
        <div className="absolute -top-3 -right-3 bg-amber-500 text-amber-950 text-[10px] font-bold px-2 py-0.5 rounded-full animate-bounce">
          CACHE HIT
        </div>
      )}
    </div>
  );
};

const nodeTypes = { custom: FibNode };

const FibonacciDPVisualizer = () => {
  const { currentState } = useExecution();
  const [nodes, setNodes] = useState([]);
  const [edges, setEdges] = useState([]);
  
  const dpMode = currentState?.dpMode || 'RECURSIVE'; // RECURSIVE, MEMOIZED, BOTTOM_UP
  const activeNodeId = currentState?.activeNodeId;
  const mergeTree = currentState?.mergeTree || {};
  const dpTableState = currentState?.dpTableState;
  
  // Recursion Tree Layout
  useEffect(() => {
    if (!mergeTree || Object.keys(mergeTree).length === 0 || dpMode === 'BOTTOM_UP') return;
    
    const newNodes = [];
    const newEdges = [];
    
    const levelCounts = {};
    const processedNodes = Object.values(mergeTree);
    
    processedNodes.forEach(node => {
      levelCounts[node.depth] = (levelCounts[node.depth] || 0) + 1;
    });
    
    const levelPositions = {};
    
    processedNodes.forEach((node) => {
      const depth = node.depth;
      if (levelPositions[depth] === undefined) {
        levelPositions[depth] = 0;
      }
      
      const countAtLevel = levelCounts[depth];
      const indexAtLevel = levelPositions[depth]++;
      
      const xSpacing = 140;
      const ySpacing = 100;
      
      const totalWidth = (countAtLevel - 1) * xSpacing;
      const startX = -totalWidth / 2;
      
      const x = startX + indexAtLevel * xSpacing;
      const y = (depth - 1) * ySpacing;
      
      const isActive = node.nodeId === activeNodeId;
      const isCompleted = node.executionStatus === 'COMPLETED' || node.executionStatus === 'RETURNED';
      
      // Extract n and val from explanation or we can pass it if we added it, but wait, we added it to variables!
      // Actually, we didn't add it to node, but we can extract from variables if it's the active node.
      // Better yet, we can pass n and val in explanation or just parse it.
      // Wait, in the simulator we didn't add n and val to RecursionTreeNode directly.
      // Let's parse explanation for "fib(x) = y" or just use variables for active node.
      // We can use regex on explanation: "fib(n)"
      let nMatch = node.explanation?.match(/fib\((\d+)\)/);
      let n = nMatch ? nMatch[1] : "?";
      
      let valMatch = node.explanation?.match(/=\s*(\d+)/);
      let val = valMatch ? parseInt(valMatch[1]) : -1;
      if (node.operationType === 'CACHE_HIT' && val === -1) {
          let cacheMatch = node.explanation?.match(/earlier\s*\((\d+)\)/);
          if (cacheMatch) val = parseInt(cacheMatch[1]);
      }

      newNodes.push({
        id: node.nodeId,
        type: 'custom',
        position: { x, y },
        data: {
          isActive,
          isCompleted,
          type: node.operationType,
          explanation: node.explanation,
          dpMode,
          n,
          val
        },
      });
      
      if (node.parentId) {
        newEdges.push({
          id: `e-${node.parentId}-${node.nodeId}`,
          source: node.parentId,
          target: node.nodeId,
          type: 'smoothstep',
          animated: isActive || mergeTree[node.parentId]?.nodeId === activeNodeId,
          style: { 
            stroke: isActive && node.operationType === 'CACHE_HIT' ? '#fbbf24' : isActive ? '#22d3ee' : isCompleted ? '#10b981' : '#475569',
            strokeWidth: isActive ? 2 : 1
          },
        });
      }
    });
    
    setNodes(newNodes);
    setEdges(newEdges);
  }, [mergeTree, activeNodeId, dpMode]);

  return (
    <div className="w-full h-full flex flex-col gap-4">
      
      {/* Mode Indicator & Complexity */}
      <div className="w-full flex justify-between items-center bg-slate-900 border border-slate-700 px-4 py-2 rounded-xl shadow-lg">
        <div className="flex gap-2 items-center">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Current Phase:</span>
          <span className={`text-sm font-bold px-3 py-1 rounded-full ${
            dpMode === 'RECURSIVE' ? 'bg-rose-500/20 text-rose-400 border border-rose-500/30' :
            dpMode === 'MEMOIZED' ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30' :
            'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
          }`}>
            {dpMode.replace('_', ' ')}
          </span>
        </div>
        
        <div className="flex gap-6 items-center">
          <div className="flex flex-col items-end">
            <span className="text-[10px] text-slate-500 font-mono uppercase">Time Complexity</span>
            <span className="text-sm font-bold text-slate-300 font-mono">{currentState?.timeComplexity || 'O(2^N)'}</span>
          </div>
          <div className="flex flex-col items-end">
            <span className="text-[10px] text-slate-500 font-mono uppercase">Space Complexity</span>
            <span className="text-sm font-bold text-slate-300 font-mono">{currentState?.spaceComplexity || 'O(N)'}</span>
          </div>
        </div>
      </div>

      <div className="w-full h-full flex gap-4 overflow-hidden">
        {/* Left: Recursion Tree (Visible in Recursive/Memoized) */}
        {dpMode !== 'BOTTOM_UP' && (
          <div className="w-3/5 h-full relative rounded-xl overflow-hidden border border-slate-800 bg-slate-950">
            <div className="absolute top-4 left-4 z-10 px-3 py-1 bg-slate-900/80 rounded-full border border-slate-700 backdrop-blur-sm shadow-lg text-xs font-semibold text-slate-300">
              Recursion Tree
            </div>
            <ReactFlow
              nodes={nodes}
              edges={edges}
              nodeTypes={nodeTypes}
              fitView
              fitViewOptions={{ padding: 0.2 }}
              minZoom={0.2}
              maxZoom={1.5}
              proOptions={{ hideAttribution: true }}
            >
              <Background color="#1e293b" gap={16} />
              <Controls className="bg-slate-900 border-slate-700 fill-slate-300" />
            </ReactFlow>
          </div>
        )}

        {/* Right: DP Table & Array Vis */}
        <div className={`${dpMode === 'BOTTOM_UP' ? 'w-full' : 'w-2/5'} h-full flex flex-col gap-4 transition-all duration-500`}>
          
          <div className="flex-1 bg-slate-950 border border-slate-800 rounded-xl p-6 flex flex-col relative shadow-inner overflow-hidden justify-center items-center">
            <div className="absolute top-4 left-4 text-xs font-semibold text-slate-300 bg-slate-900 px-3 py-1 rounded-full border border-slate-700">
              DP Table (Memoization Cache / Tabulation)
            </div>
            
            {dpMode === 'BOTTOM_UP' && dpTableState ? (
              <div className="flex flex-col items-center justify-center w-full mt-8">
                <div className="flex gap-2 flex-wrap justify-center p-8">
                  <AnimatePresence mode="popLayout">
                    {dpTableState.table.map((val, idx) => {
                      const isActive = dpTableState.activeIndex === idx;
                      const isDep = dpTableState.dependencyIndices?.includes(idx);
                      const isComputed = dpTableState.computedIndices?.includes(idx);
                      
                      let bgColor = 'bg-slate-900';
                      let borderColor = 'border-slate-800';
                      let textColor = 'text-slate-500';
                      
                      if (isActive) {
                        bgColor = 'bg-cyan-500/20';
                        borderColor = 'border-cyan-400';
                        textColor = 'text-cyan-300';
                      } else if (isDep) {
                        bgColor = 'bg-indigo-500/20';
                        borderColor = 'border-indigo-400';
                        textColor = 'text-indigo-300';
                      } else if (isComputed) {
                        bgColor = 'bg-emerald-500/10';
                        borderColor = 'border-emerald-500/50';
                        textColor = 'text-emerald-400';
                      }

                      return (
                        <motion.div
                          key={`dp-${idx}`}
                          layout
                          initial={{ opacity: 0, y: 20 }}
                          animate={{ opacity: 1, y: 0, scale: isActive ? 1.1 : 1 }}
                          className={`relative w-16 h-16 flex flex-col items-center justify-center rounded-xl border-2 transition-colors ${bgColor} ${borderColor} ${textColor} shadow-lg`}
                        >
                          <span className="text-xl font-mono font-bold">{val !== null ? val : ""}</span>
                          <span className="absolute -bottom-6 text-xs font-mono text-slate-500">dp[{idx}]</span>
                          
                          {/* Dependency link highlights */}
                          {isActive && isDep && (
                             <div className="absolute inset-0 border-2 border-cyan-400 rounded-xl animate-ping opacity-20"></div>
                          )}
                        </motion.div>
                      );
                    })}
                  </AnimatePresence>
                </div>
                
                {dpTableState.dependencyIndices?.length > 0 && (
                  <div className="mt-12 text-sm font-mono text-slate-400 flex items-center gap-4 bg-slate-900 px-6 py-3 rounded-xl border border-slate-700 shadow-lg">
                    <span className="text-indigo-400 flex items-center gap-2"><span className="w-3 h-3 rounded-full bg-indigo-500"></span> Dependency (i-1, i-2)</span>
                    <span className="text-slate-500">→</span>
                    <span className="text-cyan-400 flex items-center gap-2"><span className="w-3 h-3 rounded-full bg-cyan-500 animate-pulse"></span> Active Compute (i)</span>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex-1 flex items-center justify-center w-full">
                 <div className="text-center p-6 border border-slate-800 border-dashed rounded-xl max-w-sm">
                   <svg className="w-12 h-12 text-slate-700 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                     <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4" />
                   </svg>
                   <h3 className="text-slate-400 font-semibold mb-2">Cache Memory</h3>
                   <p className="text-xs text-slate-500">
                     {dpMode === 'RECURSIVE' 
                       ? "Cache is NOT used in naive recursion. Watch how subproblems are repeated unnecessarily on the left!" 
                       : "Memoization cache stores results of recursive calls to prevent recalculating them."}
                   </p>
                 </div>
              </div>
            )}
          </div>
          
          {/* Explanation Card */}
          <div className="h-1/3 min-h-[160px] bg-slate-900 border border-slate-700/50 rounded-xl p-5 shadow-lg relative overflow-hidden group">
            <div className="absolute inset-0 bg-gradient-to-br from-cyan-500/5 to-purple-500/5 opacity-50"></div>
            <h3 className="text-sm font-bold text-cyan-400 mb-2 flex items-center gap-2">
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Educational Explanation
            </h3>
            <p className="text-slate-300 text-sm leading-relaxed relative z-10 font-medium">
              {currentState?.educationalNote || "Select an algorithm to view the explanation step by step."}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FibonacciDPVisualizer;
