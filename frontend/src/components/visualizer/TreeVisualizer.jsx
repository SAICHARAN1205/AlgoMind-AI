import React, { useMemo, useEffect } from 'react';
import { ReactFlow, Background, MarkerType, useNodesState, useEdgesState } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { useExecution } from '../../context/ExecutionContext';
import { motion, AnimatePresence } from 'framer-motion';

// Custom Node for Tree
const TreeNodeVisualizer = ({ data }) => {
  const { label, isVisited, isActive, activeOp } = data;
  
  let bgClass = 'bg-slate-800 border-slate-600 text-slate-300';
  if (isVisited) bgClass = 'bg-slate-700 border-purple-500/50 text-purple-300';
  if (isActive) {
      if (activeOp === 'COMPARE') bgClass = 'bg-amber-900/50 border-amber-400 text-amber-300 shadow-[0_0_15px_rgba(251,191,36,0.4)]';
      else if (activeOp === 'INSERT' || activeOp === 'FOUND') bgClass = 'bg-emerald-900/50 border-emerald-400 text-emerald-300 shadow-[0_0_15px_rgba(16,185,129,0.4)]';
      else bgClass = 'bg-cyan-900/50 border-cyan-400 text-cyan-300 shadow-[0_0_15px_rgba(34,211,238,0.4)]';
  }

  return (
    <div className={`w-14 h-14 rounded-full border-2 flex items-center justify-center transition-all duration-300 ${bgClass}`}>
      <span className="font-mono font-bold text-lg">{label}</span>
    </div>
  );
};

const nodeTypes = {
  treeNode: TreeNodeVisualizer,
};

const TreeVisualizer = () => {
  const { currentState } = useExecution();
  
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  useEffect(() => {
    if (!currentState || !currentState.treeState) return;

    const { nodes: treeNodesMap, activeNodeId, activeOperation } = currentState.treeState;
    
    if (!treeNodesMap) return;

    const newNodes = [];
    const newEdges = [];

    // Base position centering offset
    const xOffset = 300;
    const yOffset = 50;

    Object.values(treeNodesMap).forEach((node) => {
      newNodes.push({
        id: node.id,
        type: 'treeNode',
        position: { x: node.x + xOffset, y: node.y + yOffset },
        data: { 
            label: node.value,
            isVisited: node.visited,
            isActive: node.active,
            activeOp: activeOperation
        },
      });

      if (node.leftId) {
        newEdges.push({
          id: `e-${node.id}-${node.leftId}`,
          source: node.id,
          target: node.leftId,
          animated: node.active || (treeNodesMap[node.leftId] && treeNodesMap[node.leftId].active),
          style: { stroke: (node.active && activeOperation === 'TRAVERSE_LEFT') ? '#22d3ee' : '#475569', strokeWidth: 2 },
          markerEnd: { type: MarkerType.ArrowClosed, color: (node.active && activeOperation === 'TRAVERSE_LEFT') ? '#22d3ee' : '#475569' }
        });
      }

      if (node.rightId) {
        newEdges.push({
          id: `e-${node.id}-${node.rightId}`,
          source: node.id,
          target: node.rightId,
          animated: node.active || (treeNodesMap[node.rightId] && treeNodesMap[node.rightId].active),
          style: { stroke: (node.active && activeOperation === 'TRAVERSE_RIGHT') ? '#22d3ee' : '#475569', strokeWidth: 2 },
          markerEnd: { type: MarkerType.ArrowClosed, color: (node.active && activeOperation === 'TRAVERSE_RIGHT') ? '#22d3ee' : '#475569' }
        });
      }
    });

    setNodes(newNodes);
    setEdges(newEdges);

  }, [currentState, setNodes, setEdges]);

  if (!currentState || !currentState.treeState) return null;
  
  const { traversalOrder } = currentState.treeState;

  return (
    <div className="w-full h-full relative bg-slate-950 rounded-xl overflow-hidden border border-slate-800">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        nodeTypes={nodeTypes}
        fitView
        fitViewOptions={{ padding: 0.2 }}
        proOptions={{ hideAttribution: true }}
      >
        <Background color="#334155" gap={20} size={1} />
      </ReactFlow>

      {/* Traversal Order Panel */}
      {traversalOrder && traversalOrder.length > 0 && (
          <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 bg-slate-900/90 backdrop-blur-sm border border-purple-500/30 rounded-xl px-6 py-3 shadow-lg flex flex-col items-center max-w-[90%]">
              <span className="text-[10px] uppercase font-bold text-purple-400 mb-1 tracking-widest">Traversal Order</span>
              <div className="flex flex-wrap items-center justify-center gap-2">
                  <AnimatePresence>
                      {traversalOrder.map((val, idx) => (
                          <React.Fragment key={`trav-${idx}`}>
                              <motion.div
                                  initial={{ opacity: 0, scale: 0.5 }}
                                  animate={{ opacity: 1, scale: 1 }}
                                  className="w-8 h-8 rounded-full bg-purple-900/50 border border-purple-400 flex items-center justify-center"
                              >
                                  <span className="text-xs font-mono font-bold text-purple-200">{val}</span>
                              </motion.div>
                              {idx < traversalOrder.length - 1 && (
                                  <span className="text-slate-500 text-xs">→</span>
                              )}
                          </React.Fragment>
                      ))}
                  </AnimatePresence>
              </div>
          </div>
      )}
    </div>
  );
};

export default TreeVisualizer;
