import React, { useMemo, useEffect, useState } from 'react';
import { useExecution } from '../../context/ExecutionContext';
import { ReactFlow, Background, Controls, MarkerType, useNodesState, useEdgesState, EdgeLabelRenderer, BaseEdge, getStraightPath } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { motion, AnimatePresence } from 'framer-motion';

// Custom Graph Node
const GraphNodeUI = ({ data }) => {
  const { id, label, visited, active } = data;
  
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
    </div>
  );
};

// Custom Edge with Label
const CustomWeightedEdge = ({ id, sourceX, sourceY, targetX, targetY, sourcePosition, targetPosition, style, markerEnd, data }) => {
  const [edgePath, labelX, labelY] = getStraightPath({
    sourceX,
    sourceY,
    targetX,
    targetY,
  });

  return (
    <>
      <BaseEdge id={id} path={edgePath} style={style} markerEnd={markerEnd} />
      <EdgeLabelRenderer>
        <div
          style={{
            position: 'absolute',
            transform: `translate(-50%, -50%) translate(${labelX}px,${labelY}px)`,
            pointerEvents: 'all',
          }}
          className={`px-2 py-0.5 rounded text-xs font-mono font-bold border transition-colors ${
              data?.active ? 'bg-cyan-900 border-cyan-400 text-cyan-300' :
              data?.traversed ? 'bg-emerald-900 border-emerald-500 text-emerald-400' :
              'bg-slate-800 border-slate-600 text-slate-300'
          }`}
        >
          {data?.weight}
        </div>
      </EdgeLabelRenderer>
    </>
  );
};

const nodeTypes = { custom: GraphNodeUI };
const edgeTypes = { weighted: CustomWeightedEdge };

const DijkstraVisualizer = () => {
  const { currentState } = useExecution();
  
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  
  const dijkstraState = currentState?.dijkstraState;
  
  // Fixed positions for the sample graph
  // 0 -> (1,4), (2,1)
  // 1 -> (3,1)
  // 2 -> (1,2), (3,5)
  // 3 -> (4,3)
  // 4 -> []
  const nodePositions = {
    '0': { x: 50,  y: 150 },
    '1': { x: 200, y: 50 },
    '2': { x: 200, y: 250 },
    '3': { x: 350, y: 150 },
    '4': { x: 500, y: 150 }
  };
  
  useEffect(() => {
    if (!dijkstraState) return;
    
    const newNodes = dijkstraState.nodes.map(n => ({
      id: n.id,
      type: 'custom',
      position: nodePositions[n.id] || { x: 0, y: 0 },
      data: {
        id: n.id,
        label: n.label,
        visited: n.visited,
        active: n.active
      }
    }));
    
    const newEdges = dijkstraState.edges.map(e => {
      const isTraversed = e.traversed;
      const isActive = e.active;
      
      let color = isActive ? '#22d3ee' : isTraversed ? '#10b981' : '#475569';
      
      return {
        id: `e-${e.source}-${e.target}`,
        source: e.source,
        target: e.target,
        type: 'weighted',
        animated: isActive,
        data: { weight: e.weight, active: isActive, traversed: isTraversed },
        style: {
          stroke: color,
          strokeWidth: isActive || isTraversed ? 3 : 2
        },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          color: color,
        }
      };
    });
    
    setNodes(newNodes);
    setEdges(newEdges);
  }, [dijkstraState, setNodes, setEdges]);
  
  const pq = dijkstraState?.priorityQueue || [];
  const distanceMap = dijkstraState?.distanceMap || {};

  return (
    <div className="w-full h-full flex gap-4 overflow-hidden">
      {/* Left: Graph Tree */}
      <div className="w-3/5 h-full relative rounded-xl overflow-hidden border border-slate-800 bg-slate-950 shadow-inner flex flex-col">
        <div className="absolute top-4 left-4 z-10 px-3 py-1 bg-slate-900/80 rounded-full border border-slate-700 backdrop-blur-sm shadow-lg text-xs font-semibold text-slate-300 flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>
          Weighted Graph
        </div>
        <div className="flex-1">
            <ReactFlow
              nodes={nodes}
              edges={edges}
              onNodesChange={onNodesChange}
              onEdgesChange={onEdgesChange}
              nodeTypes={nodeTypes}
              edgeTypes={edgeTypes}
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

      {/* Right: Distance Table & PQ Visualizer */}
      <div className="w-2/5 h-full flex flex-col gap-4">
        
        <div className="flex-1 bg-slate-950 border border-slate-800 rounded-xl p-4 flex flex-col gap-4 relative shadow-inner overflow-hidden">
          
          <div className="flex-1 flex gap-4 h-[60%]">
              {/* Distance Table */}
              <div className="w-1/2 flex flex-col">
                  <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">Distance Table</div>
                  <div className="flex-1 bg-slate-900 rounded-xl border border-slate-700 overflow-hidden flex flex-col">
                      <div className="grid grid-cols-2 bg-slate-800 border-b border-slate-700 text-xs font-bold text-slate-400 p-2">
                          <div className="text-center">Node</div>
                          <div className="text-center">Dist</div>
                      </div>
                      <div className="flex-1 overflow-y-auto">
                          {Object.keys(distanceMap).map((nodeId) => {
                              const dist = distanceMap[nodeId];
                              const isInfinity = dist === 2147483647; // Integer.MAX_VALUE
                              const displayDist = isInfinity ? "∞" : dist;
                              const isActive = String(dijkstraState?.currentNode) === nodeId;
                              const isTargetOfActiveEdge = dijkstraState?.activeEdge?.target === nodeId;
                              
                              let rowClass = "border-b border-slate-800/50 ";
                              if (isTargetOfActiveEdge) rowClass += "bg-cyan-500/10 text-cyan-300 font-bold";
                              else if (isActive) rowClass += "bg-emerald-500/10 text-emerald-400 font-bold";
                              else rowClass += "text-slate-300";
                              
                              return (
                                  <div key={`dist-${nodeId}`} className={`grid grid-cols-2 p-2 text-sm font-mono text-center transition-colors ${rowClass}`}>
                                      <div>{nodeId}</div>
                                      <div>{displayDist}</div>
                                  </div>
                              );
                          })}
                      </div>
                  </div>
              </div>

              {/* Priority Queue (Vertical) */}
              <div className="w-1/2 flex flex-col items-center">
                  <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2">Priority Queue</div>
                  <div className="flex-1 w-full bg-slate-900 border-x-2 border-b-2 border-amber-900/50 rounded-b-xl flex flex-col-reverse justify-start items-center p-2 gap-2 overflow-y-auto relative">
                     {pq.length === 0 && (
                         <div className="absolute inset-0 flex flex-col items-center justify-center opacity-50">
                             <div className="text-xs font-mono text-slate-500">Empty</div>
                         </div>
                     )}
                     <AnimatePresence>
                        {pq.map((val, idx) => (
                           <motion.div
                             key={`pq-${val}-${idx}`}
                             initial={{ opacity: 0, x: 20, scale: 0.9 }}
                             animate={{ opacity: 1, x: 0, scale: 1 }}
                             exit={{ opacity: 0, scale: 0.8 }}
                             transition={{ duration: 0.2 }}
                             className={`w-full py-2 flex flex-col items-center justify-center rounded border shadow-md ${
                                 idx === pq.length - 1 
                                     ? 'bg-amber-500/20 border-amber-400 text-amber-300 scale-105' 
                                     : 'bg-slate-800 border-slate-600 text-slate-400'
                             }`}
                           >
                             <span className="font-mono text-xs font-bold">{val}</span>
                             {idx === pq.length - 1 && (
                                <span className="text-[8px] uppercase tracking-widest text-amber-400 font-sans mt-0.5">Min</span>
                             )}
                           </motion.div>
                        ))}
                     </AnimatePresence>
                  </div>
              </div>
          </div>
          
          {/* Greedy Choice Highlight */}
          <div className="bg-emerald-950 border border-emerald-900/50 rounded-xl p-3 shadow-inner flex items-center justify-center gap-3">
              <svg className="w-5 h-5 text-emerald-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
              <div className="text-xs text-emerald-400 font-medium text-center">
                  Dijkstra always extracts the node with the <span className="font-bold text-emerald-300 uppercase">minimum distance</span> from the Priority Queue!
              </div>
          </div>
        </div>
        
        {/* Explanation Card */}
        <div className="h-[130px] bg-slate-900 border border-slate-700/50 rounded-xl p-4 shadow-lg relative overflow-hidden group">
          <div className="absolute inset-0 bg-gradient-to-br from-cyan-500/5 to-amber-500/5 opacity-50"></div>
          <h3 className="text-xs font-bold text-cyan-400 mb-2 flex items-center gap-2">
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

export default DijkstraVisualizer;
