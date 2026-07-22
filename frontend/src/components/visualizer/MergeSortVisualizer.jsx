import React, { useMemo, useEffect, useState } from 'react';
import { useExecution } from '../../context/ExecutionContext';
import { ReactFlow, Background, Controls, MarkerType } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { motion, AnimatePresence } from 'framer-motion';

const CustomNode = ({ data }) => {
  const { subArray, isActive, isCompleted, type, explanation, startIndex, endIndex } = data;
  
  return (
    <div className={`p-3 rounded-xl border-2 transition-all duration-300 shadow-lg min-w-[120px] max-w-[200px] bg-slate-900 ${isActive ? 'border-cyan-400 shadow-[0_0_15px_rgba(34,211,238,0.5)] scale-110 z-10' : isCompleted ? 'border-emerald-500/50 opacity-80' : 'border-slate-700'}`}>
      <div className="flex justify-between items-center mb-2 border-b border-slate-700/50 pb-1">
        <span className="text-[10px] font-mono text-slate-400 uppercase tracking-wider">[{startIndex}...{endIndex}]</span>
        {isActive && <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>}
      </div>
      <div className="flex gap-1 justify-center flex-wrap">
        {subArray?.map((val, idx) => (
          <div key={idx} className={`text-xs font-mono w-6 h-6 flex items-center justify-center rounded ${isCompleted ? 'bg-emerald-500/20 text-emerald-300' : 'bg-slate-800 text-slate-300'}`}>
            {val}
          </div>
        ))}
      </div>
    </div>
  );
};

const nodeTypes = { custom: CustomNode };

const MergeSortVisualizer = () => {
  const { currentState } = useExecution();
  const [nodes, setNodes] = useState([]);
  const [edges, setEdges] = useState([]);
  
  const mergeTree = currentState?.mergeTree || {};
  const activeNodeId = currentState?.activeNodeId;
  const currentArray = currentState?.array || [];
  const highlightedIndices = currentState?.highlightedIndices || [];
  
  // Build tree layout
  useEffect(() => {
    if (!mergeTree || Object.keys(mergeTree).length === 0) return;
    
    const newNodes = [];
    const newEdges = [];
    
    // Simple tree layout calculation
    const levelCounts = {};
    const processedNodes = Object.values(mergeTree);
    
    // Count nodes per level to center them
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
      
      const width = 160;
      const xSpacing = 200;
      const ySpacing = 120;
      
      const totalWidth = (countAtLevel - 1) * xSpacing;
      const startX = -totalWidth / 2;
      
      const x = startX + indexAtLevel * xSpacing;
      const y = (depth - 1) * ySpacing;
      
      const isActive = node.nodeId === activeNodeId;
      const isCompleted = node.executionStatus === 'COMPLETED' || node.executionStatus === 'RETURNED';
      
      newNodes.push({
        id: node.nodeId,
        type: 'custom',
        position: { x, y },
        data: {
          subArray: node.subArray,
          isActive,
          isCompleted,
          type: node.operationType,
          explanation: node.explanation,
          startIndex: node.startIndex,
          endIndex: node.endIndex
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
            stroke: isActive ? '#22d3ee' : isCompleted ? '#10b981' : '#475569',
            strokeWidth: isActive ? 2 : 1
          },
          markerEnd: {
            type: MarkerType.ArrowClosed,
            color: isActive ? '#22d3ee' : isCompleted ? '#10b981' : '#475569',
          },
        });
      }
    });
    
    setNodes(newNodes);
    setEdges(newEdges);
  }, [mergeTree, activeNodeId]);

  return (
    <div className="w-full h-full flex gap-4 overflow-hidden">
      {/* Left: Recursion Tree */}
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

      {/* Right: Array & Merge Visualization */}
      <div className="w-2/5 h-full flex flex-col gap-4">
        
        <div className="flex-1 bg-slate-950 border border-slate-800 rounded-xl p-4 flex flex-col items-center justify-center relative shadow-inner">
          <div className="absolute top-4 left-4 text-xs font-semibold text-slate-300 bg-slate-900 px-3 py-1 rounded-full border border-slate-700">
            Array State
          </div>
          
          <div className="flex gap-2 flex-wrap justify-center mt-6">
            <AnimatePresence mode="popLayout">
              {currentArray.map((val, idx) => {
                const isHighlighted = highlightedIndices.includes(idx);
                const isCompareI = currentState?.variables?.compareI === idx;
                const isCompareJ = currentState?.variables?.compareJ === idx;
                const isMergeIdx = currentState?.variables?.mergeIndex === idx;
                
                let bgColor = 'bg-slate-800';
                let borderColor = 'border-slate-700';
                let textColor = 'text-slate-300';
                
                if (isCompareI) {
                  bgColor = 'bg-indigo-500/20';
                  borderColor = 'border-indigo-400';
                  textColor = 'text-indigo-300';
                } else if (isCompareJ) {
                  bgColor = 'bg-purple-500/20';
                  borderColor = 'border-purple-400';
                  textColor = 'text-purple-300';
                } else if (isMergeIdx) {
                  bgColor = 'bg-emerald-500/20';
                  borderColor = 'border-emerald-400';
                  textColor = 'text-emerald-300';
                } else if (isHighlighted) {
                  bgColor = 'bg-cyan-500/20';
                  borderColor = 'border-cyan-500';
                  textColor = 'text-cyan-300';
                }

                return (
                  <motion.div
                    key={`${idx}-${val}`}
                    layout
                    initial={{ opacity: 0, scale: 0.8, y: -20 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    transition={{ type: "spring", stiffness: 300, damping: 20 }}
                    className={`relative w-12 h-14 flex flex-col items-center justify-center rounded-lg border-2 ${bgColor} ${borderColor} ${textColor} shadow-lg`}
                  >
                    <span className="text-lg font-mono font-bold">{val}</span>
                    <span className="absolute -bottom-5 text-[10px] font-mono text-slate-500">[{idx}]</span>
                  </motion.div>
                );
              })}
            </AnimatePresence>
          </div>
          
          {/* Operation specific indicators */}
          {currentState?.variables?.compareI !== undefined && currentState?.variables?.compareJ !== undefined && (
             <div className="mt-12 text-sm font-mono text-slate-400 flex items-center gap-4 bg-slate-900 px-4 py-2 rounded-xl border border-slate-700">
               <span className="text-indigo-400 flex items-center gap-2"><span className="w-3 h-3 rounded-full bg-indigo-500"></span> Left Elem</span>
               <span className="text-slate-500">vs</span>
               <span className="text-purple-400 flex items-center gap-2"><span className="w-3 h-3 rounded-full bg-purple-500"></span> Right Elem</span>
             </div>
          )}
        </div>
        
        {/* Explanation Card */}
        <div className="h-1/3 bg-slate-900 border border-slate-700/50 rounded-xl p-5 shadow-lg relative overflow-hidden group">
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
  );
};

export default MergeSortVisualizer;
