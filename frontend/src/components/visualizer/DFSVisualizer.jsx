import React, { useMemo, useEffect, useState } from 'react';
import { useExecution } from '../../context/ExecutionContext';
import { ReactFlow, Background, Controls, MarkerType, useNodesState, useEdgesState } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { motion, AnimatePresence } from 'framer-motion';

// Custom Graph Node
const GraphNodeUI = ({ data }) => {
  const { id, label, visited, active, level, isBacktracking } = data;
  
  let bgColor = 'bg-slate-800';
  let borderColor = 'border-slate-600';
  let textColor = 'text-slate-300';
  let glow = '';
  
  if (isBacktracking) {
    bgColor = 'bg-rose-500/20';
    borderColor = 'border-rose-400';
    textColor = 'text-rose-300';
    glow = 'shadow-[0_0_15px_rgba(244,63,94,0.5)] scale-105 z-10';
  } else if (active) {
    bgColor = 'bg-cyan-500/20';
    borderColor = 'border-cyan-400';
    textColor = 'text-cyan-300';
    glow = 'shadow-[0_0_15px_rgba(34,211,238,0.5)] scale-110 z-10';
  } else if (visited) {
    bgColor = 'bg-emerald-500/20';
    borderColor = 'border-emerald-500/50';
    textColor = 'text-emerald-400';
  }

  return (
    <div className={`w-14 h-14 rounded-full border-2 flex items-center justify-center transition-all duration-300 ${bgColor} ${borderColor} ${textColor} ${glow} relative`}>
      <span className="font-mono text-lg font-bold">{label}</span>
      {level !== -1 && (
        <span className="absolute -bottom-5 text-[9px] font-mono bg-slate-900 px-1 rounded border border-slate-700 whitespace-nowrap">
          Depth {level}
        </span>
      )}
    </div>
  );
};

const nodeTypes = { custom: GraphNodeUI };

const DFSVisualizer = () => {
  const { currentState } = useExecution();
  
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  
  const dfsState = currentState?.dfsState;
  const isBacktrack = currentState?.operationType === 'BACKTRACK';
  
  // Fixed positions for the sample graph (Same as BFS for visual comparison)
  const nodePositions = {
    '0': { x: 250, y: 50 },
    '1': { x: 150, y: 150 },
    '2': { x: 350, y: 150 },
    '3': { x: 50,  y: 250 },
    '4': { x: 200, y: 250 },
    '5': { x: 350, y: 250 }
  };
  
  useEffect(() => {
    if (!dfsState) return;
    
    const newNodes = dfsState.nodes.map(n => ({
      id: n.id,
      type: 'custom',
      position: nodePositions[n.id] || { x: 0, y: 0 },
      data: {
        id: n.id,
        label: n.label,
        visited: n.visited,
        active: n.active,
        level: n.level,
        isBacktracking: isBacktrack && n.id === String(dfsState.currentNode)
      }
    }));
    
    const newEdges = dfsState.edges.map(e => {
      const isTraversed = e.traversed;
      const isActive = e.active;
      
      let color = isActive && isBacktrack ? '#f43f5e' : isActive ? '#22d3ee' : isTraversed ? '#10b981' : '#475569';
      
      return {
        id: `e-${e.source}-${e.target}`,
        source: e.source,
        target: e.target,
        type: 'straight',
        animated: isActive,
        style: {
          stroke: color,
          strokeWidth: isActive || isTraversed ? 3 : 1
        },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          color: color,
        }
      };
    });
    
    setNodes(newNodes);
    setEdges(newEdges);
  }, [dfsState, isBacktrack, setNodes, setEdges]);
  
  const stack = dfsState?.recursionStack || [];
  const traversalOrder = dfsState?.traversalOrder || [];

  return (
    <div className="w-full h-full flex gap-4 overflow-hidden">
      {/* Left: Graph Tree */}
      <div className="w-3/5 h-full relative rounded-xl overflow-hidden border border-slate-800 bg-slate-950 shadow-inner flex flex-col">
        <div className="absolute top-4 left-4 z-10 px-3 py-1 bg-slate-900/80 rounded-full border border-slate-700 backdrop-blur-sm shadow-lg text-xs font-semibold text-slate-300">
          Graph Structure
        </div>
        <div className="flex-1">
            <ReactFlow
              nodes={nodes}
              edges={edges}
              onNodesChange={onNodesChange}
              onEdgesChange={onEdgesChange}
              nodeTypes={nodeTypes}
              fitView
              fitViewOptions={{ padding: 0.2 }}
              minZoom={0.5}
              maxZoom={1.5}
              proOptions={{ hideAttribution: true }}
              nodesDraggable={false}
              nodesConnectable={false}
            >
              <Background color="#1e293b" gap={20} />
              <Controls className="bg-slate-900 border-slate-700 fill-slate-300" />
            </ReactFlow>
        </div>
      </div>

      {/* Right: Stack & Order Visualizer */}
      <div className="w-2/5 h-full flex flex-col gap-4">
        
        <div className="flex-1 bg-slate-950 border border-slate-800 rounded-xl p-4 flex flex-col relative shadow-inner overflow-hidden">
          
          <div className="flex-1 flex gap-4">
              {/* Stack Visualization */}
              <div className="w-1/3 flex flex-col items-center">
                  <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">Call Stack</div>
                  <div className="flex-1 w-full bg-slate-900 border-x-2 border-b-2 border-slate-700 rounded-b-xl flex flex-col-reverse justify-start items-center p-2 gap-2 overflow-y-auto relative">
                     {stack.length === 0 && (
                         <div className="absolute inset-0 flex flex-col items-center justify-center opacity-50">
                             <div className="text-xs font-mono text-slate-500">Empty</div>
                         </div>
                     )}
                     <AnimatePresence>
                        {stack.map((val, idx) => (
                           <motion.div
                             key={`s-${val}-${idx}`}
                             initial={{ opacity: 0, y: -20, scale: 0.9 }}
                             animate={{ opacity: 1, y: 0, scale: 1 }}
                             exit={{ opacity: 0, y: -20, scale: 0.9 }}
                             transition={{ duration: 0.2 }}
                             className={`w-full py-2 flex flex-col items-center justify-center rounded border-2 shadow-md ${
                                 idx === stack.length - 1 
                                     ? 'bg-purple-500/20 border-purple-400 text-purple-300' 
                                     : 'bg-slate-800 border-slate-600 text-slate-400'
                             }`}
                           >
                             <span className="font-mono text-sm font-bold">Node {val}</span>
                             {idx === stack.length - 1 && (
                                <span className="text-[8px] uppercase tracking-widest text-purple-400 font-sans mt-0.5">Top</span>
                             )}
                           </motion.div>
                        ))}
                     </AnimatePresence>
                  </div>
              </div>

              {/* Traversal Order & Comparison Panel */}
              <div className="w-2/3 flex flex-col gap-4">
                  
                  <div className="flex-1 flex flex-col bg-slate-900/50 rounded-xl p-3 border border-slate-800">
                      <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">Traversal Order</div>
                      <div className="flex items-center gap-1.5 flex-wrap">
                         <AnimatePresence>
                             {traversalOrder.map((val, idx) => (
                                 <div key={`t-${val}-${idx}`} className="flex items-center gap-1.5">
                                     <motion.div
                                       initial={{ opacity: 0, scale: 0 }}
                                       animate={{ opacity: 1, scale: 1 }}
                                       className="w-6 h-6 rounded bg-emerald-500/20 border border-emerald-500/50 text-emerald-400 flex items-center justify-center font-mono font-bold text-xs"
                                     >
                                         {val}
                                     </motion.div>
                                     {idx < traversalOrder.length - 1 && (
                                         <span className="text-slate-600 text-xs">→</span>
                                     )}
                                 </div>
                             ))}
                         </AnimatePresence>
                      </div>
                  </div>

                  {/* Comparison Panel */}
                  <div className="bg-slate-900 rounded-xl p-3 border border-slate-800">
                      <div className="text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-2">Quick Compare</div>
                      <div className="grid grid-cols-2 gap-2 text-xs">
                          <div className="bg-slate-950 p-2 rounded border border-slate-800">
                              <div className="font-bold text-cyan-400 mb-1">BFS</div>
                              <ul className="text-slate-400 space-y-0.5 ml-3 list-disc text-[10px]">
                                  <li>Uses a Queue</li>
                                  <li>Explores Level-by-Level</li>
                                  <li>Shortest Path finding</li>
                              </ul>
                          </div>
                          <div className="bg-slate-950 p-2 rounded border border-purple-500/30">
                              <div className="font-bold text-purple-400 mb-1">DFS</div>
                              <ul className="text-slate-400 space-y-0.5 ml-3 list-disc text-[10px]">
                                  <li>Uses a Stack</li>
                                  <li>Plunges Deeply</li>
                                  <li>Path/Maze solving</li>
                              </ul>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
          
        </div>
        
        {/* Explanation Card */}
        <div className="h-[120px] bg-slate-900 border border-slate-700/50 rounded-xl p-4 shadow-lg relative overflow-hidden group">
          <div className="absolute inset-0 bg-gradient-to-br from-purple-500/5 to-rose-500/5 opacity-50"></div>
          <h3 className="text-xs font-bold text-purple-400 mb-2 flex items-center gap-2">
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            Educational Explanation
          </h3>
          <p className="text-slate-300 text-sm leading-snug relative z-10 font-medium">
            {currentState?.educationalNote || "Select an algorithm to view the explanation step by step."}
          </p>
        </div>
      </div>
    </div>
  );
};

export default DFSVisualizer;
