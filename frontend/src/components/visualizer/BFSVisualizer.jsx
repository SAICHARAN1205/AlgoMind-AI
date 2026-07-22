import React, { useMemo, useEffect, useState } from 'react';
import { useExecution } from '../../context/ExecutionContext';
import { ReactFlow, Background, Controls, MarkerType, useNodesState, useEdgesState } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { motion, AnimatePresence } from 'framer-motion';

// Custom Graph Node
const GraphNodeUI = ({ data }) => {
  const { id, label, visited, active, level } = data;
  
  let bgColor = 'bg-slate-800';
  let borderColor = 'border-slate-600';
  let textColor = 'text-slate-300';
  let glow = '';
  
  if (active) {
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
          L{level}
        </span>
      )}
    </div>
  );
};

const nodeTypes = { custom: GraphNodeUI };

const BFSVisualizer = () => {
  const { currentState } = useExecution();
  
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  
  const graphState = currentState?.graphState;
  
  // Fixed positions for the sample graph
  const nodePositions = {
    '0': { x: 250, y: 50 },
    '1': { x: 150, y: 150 },
    '2': { x: 350, y: 150 },
    '3': { x: 50,  y: 250 },
    '4': { x: 200, y: 250 },
    '5': { x: 350, y: 250 }
  };
  
  useEffect(() => {
    if (!graphState) return;
    
    const newNodes = graphState.nodes.map(n => ({
      id: n.id,
      type: 'custom',
      position: nodePositions[n.id] || { x: 0, y: 0 },
      data: {
        id: n.id,
        label: n.label,
        visited: n.visited,
        active: n.active,
        level: n.level
      }
    }));
    
    const newEdges = graphState.edges.map(e => {
      const isTraversed = e.traversed;
      const isActive = e.active;
      
      return {
        id: `e-${e.source}-${e.target}`,
        source: e.source,
        target: e.target,
        type: 'straight',
        animated: isActive,
        style: {
          stroke: isActive ? '#22d3ee' : isTraversed ? '#10b981' : '#475569',
          strokeWidth: isActive || isTraversed ? 3 : 1
        },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          color: isActive ? '#22d3ee' : isTraversed ? '#10b981' : '#475569',
        }
      };
    });
    
    setNodes(newNodes);
    setEdges(newEdges);
  }, [graphState, setNodes, setEdges]);
  
  const queue = graphState?.queue || [];
  const traversalOrder = graphState?.traversalOrder || [];

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

      {/* Right: Queue & Order Visualizer */}
      <div className="w-2/5 h-full flex flex-col gap-4">
        
        <div className="flex-1 bg-slate-950 border border-slate-800 rounded-xl p-4 flex flex-col relative shadow-inner overflow-hidden">
          
          {/* Queue Visualization */}
          <div className="flex-1 flex flex-col justify-center">
              <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-4 px-2">Queue State</div>
              <div className="flex items-center gap-3 bg-slate-900 border border-slate-700/50 rounded-xl p-4 min-h-[100px] overflow-x-auto relative">
                 {queue.length === 0 && (
                     <div className="absolute inset-0 flex items-center justify-center text-sm font-mono text-slate-600 italic">
                         Queue is empty
                     </div>
                 )}
                 <AnimatePresence mode="popLayout">
                    {queue.map((val, idx) => (
                       <motion.div
                         key={`q-${val}-${idx}`}
                         layout
                         initial={{ opacity: 0, x: 20, scale: 0.8 }}
                         animate={{ opacity: 1, x: 0, scale: 1 }}
                         exit={{ opacity: 0, scale: 0.5, y: -20 }}
                         className={`w-12 h-12 flex-shrink-0 flex items-center justify-center rounded-lg border-2 bg-indigo-500/20 border-indigo-400 text-indigo-300 font-mono font-bold shadow-lg`}
                       >
                         {val}
                         {idx === 0 && (
                            <span className="absolute -bottom-6 text-[10px] text-indigo-400 uppercase tracking-widest whitespace-nowrap font-sans">Front</span>
                         )}
                         {idx === queue.length - 1 && queue.length > 1 && (
                            <span className="absolute -bottom-6 text-[10px] text-indigo-400/50 uppercase tracking-widest whitespace-nowrap font-sans">Back</span>
                         )}
                       </motion.div>
                    ))}
                 </AnimatePresence>
              </div>
          </div>
          
          {/* Traversal Order Visualization */}
          <div className="flex-1 flex flex-col justify-center border-t border-slate-800 pt-4">
              <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-4 px-2">Traversal Order</div>
              <div className="flex items-center gap-2 flex-wrap min-h-[60px] p-2 bg-slate-900/50 rounded-xl">
                 <AnimatePresence>
                     {traversalOrder.map((val, idx) => (
                         <div key={`t-${val}-${idx}`} className="flex items-center gap-2">
                             <motion.div
                               initial={{ opacity: 0, scale: 0 }}
                               animate={{ opacity: 1, scale: 1 }}
                               className="w-8 h-8 rounded bg-emerald-500/20 border border-emerald-500/50 text-emerald-400 flex items-center justify-center font-mono font-bold text-sm"
                             >
                                 {val}
                             </motion.div>
                             {idx < traversalOrder.length - 1 && (
                                 <span className="text-slate-600 text-sm">→</span>
                             )}
                         </div>
                     ))}
                 </AnimatePresence>
              </div>
          </div>
        </div>
        
        {/* Explanation Card */}
        <div className="h-1/3 min-h-[140px] bg-slate-900 border border-slate-700/50 rounded-xl p-5 shadow-lg relative overflow-hidden group">
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

export default BFSVisualizer;
