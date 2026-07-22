import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import ArrayVisualizer from '../components/visualizer/ArrayVisualizer.jsx';
import BinarySearchVisualizer from '../components/visualizer/BinarySearchVisualizer.jsx';
import MergeSortVisualizer from '../components/visualizer/MergeSortVisualizer.jsx';
import FibonacciDPVisualizer from '../components/visualizer/FibonacciDPVisualizer.jsx';
import BFSVisualizer from '../components/visualizer/BFSVisualizer.jsx';
import DFSVisualizer from '../components/visualizer/DFSVisualizer.jsx';
import DijkstraVisualizer from '../components/visualizer/DijkstraVisualizer.jsx';
import StackVisualizer from '../components/visualizer/StackVisualizer.jsx';
import QueueVisualizer from '../components/visualizer/QueueVisualizer.jsx';
import TreeVisualizer from '../components/visualizer/TreeVisualizer.jsx';
import BubbleSortVisualizer from '../components/visualizer/BubbleSortVisualizer.jsx';
import PlaybackControls from '../components/controls/PlaybackControls.jsx';
import DPGridVisualizer from '../components/visualizer/DPGridVisualizer.jsx';
import VariablesPanel from '../components/visualizer/VariablesPanel.jsx';
import PseudocodeEditor from '../components/editor/PseudocodeEditor.jsx';
import { useExecution } from '../context/ExecutionContext';
import AIMentorSidebar from '../components/ai/AIMentorSidebar.jsx';

const algorithms = {
  Sorting: ['Bubble Sort', 'Selection Sort', 'Insertion Sort', 'Merge Sort'],
  Searching: ['Binary Search', 'Linear Search'],
  'Stack & Queue': ['Stack Operations', 'Queue Operations'],
  'Trees & BST': ['Inorder Traversal', 'Preorder Traversal', 'Postorder Traversal', 'Binary Search Tree (BST)'],
  Graphs: ['Breadth First Search (BFS)', 'Depth First Search (DFS)', 'Dijkstra’s Algorithm'],
  'Dynamic Programming': ['Fibonacci DP', 'Knapsack', 'LCS'],
};

import { algorithmCodeMap, FIB_BOTTOM_UP_CODE, FIB_MEMOIZED_CODE, FIB_RECURSIVE_CODE } from '../utils/algorithmCodeMap.js';

const LearnMode = () => {
  const { currentState, executionTimeline, isLoading, loadPrebuiltExecution } = useExecution();
  const [activeCategory, setActiveCategory] = useState('Sorting');
  const [activeAlgo, setActiveAlgo] = useState('Bubble Sort');

  const handleSelectAlgorithm = (algo) => {
    setActiveAlgo(algo);
  };

  const handleStart = () => {
    if (activeAlgo === 'Bubble Sort') {
      loadPrebuiltExecution('bubble-sort');
    } else if (activeAlgo === 'Binary Search') {
      loadPrebuiltExecution('binary-search');
    } else if (activeAlgo === 'Merge Sort') {
      loadPrebuiltExecution('merge-sort');
    } else if (activeAlgo === 'Fibonacci DP') {
      loadPrebuiltExecution('fibonacci-dp');
    } else if (activeAlgo === 'Breadth First Search (BFS)') {
      loadPrebuiltExecution('bfs');
    } else if (activeAlgo === 'Depth First Search (DFS)') {
      loadPrebuiltExecution('dfs');
    } else if (activeAlgo === 'Dijkstra’s Algorithm') {
      loadPrebuiltExecution('dijkstra');
    } else if (activeAlgo === 'Stack Operations') {
      loadPrebuiltExecution('stack');
    } else if (activeAlgo === 'Queue Operations') {
      loadPrebuiltExecution('queue');
    } else if (activeAlgo === 'Inorder Traversal') {
      loadPrebuiltExecution('tree-traversal-inorder');
    } else if (activeAlgo === 'Preorder Traversal') {
      loadPrebuiltExecution('tree-traversal-preorder');
    } else if (activeAlgo === 'Postorder Traversal') {
      loadPrebuiltExecution('tree-traversal-postorder');
    } else if (activeAlgo === 'Binary Search Tree (BST)') {
      loadPrebuiltExecution('bst');
    } else {
      toast('Algorithm coming soon! Try Bubble Sort or Binary Search.', { icon: '🚧' });
    }
  };

  const getCode = () => {
      // Use active algo from current execution state if playing, otherwise the UI selection
      const runningAlgo = (executionTimeline.length > 0 && currentState?.algorithmName) 
        ? currentState.algorithmName 
        : activeAlgo;
        
      if (runningAlgo === 'Fibonacci DP' || activeAlgo === 'Fibonacci DP' && executionTimeline.length === 0) {
          if (currentState?.dpMode === 'BOTTOM_UP') return FIB_BOTTOM_UP_CODE;
          if (currentState?.dpMode === 'MEMOIZED') return FIB_MEMOIZED_CODE;
          return FIB_RECURSIVE_CODE;
      }
      
      return algorithmCodeMap[runningAlgo] || algorithmCodeMap['Bubble Sort'];
  };

  const visType = currentState?.visualizationType || 'ARRAY';

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 flex flex-col gap-6 font-sans selection:bg-cyan-500/30">
      {/* Header */}
      <header className="flex justify-between items-center pb-4 border-b border-slate-800">
        <Link to="/" className="text-2xl font-bold tracking-tight">
          AlgoMind<span className="text-cyan-400">AI</span> <span className="text-slate-500 text-lg font-normal ml-2">| Learn Mode</span>
        </Link>
        <div className="flex items-center gap-4">
          <Link to="/visualize" className="text-sm font-medium text-slate-400 hover:text-cyan-400 transition-colors">
            Switch to Code Editor
          </Link>
          <div className="flex items-center gap-2 bg-slate-900 px-3 py-1.5 rounded-full border border-slate-800">
            <span className={`w-2.5 h-2.5 rounded-full ${executionTimeline.length > 0 ? 'bg-emerald-500 animate-pulse shadow-[0_0_8px_rgba(16,185,129,0.5)]' : 'bg-slate-500'}`} />
            <span className="text-xs font-mono text-slate-400">
              {executionTimeline.length > 0 ? 'Playing' : 'Standby'}
            </span>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 grid grid-cols-1 lg:grid-cols-4 gap-6 min-h-[600px]">
        
        {/* Left Panel: Algorithm Catalog */}
        <div className="flex flex-col gap-4 h-full lg:col-span-1 bg-slate-900/40 p-4 rounded-2xl border border-slate-800 shadow-xl shadow-black/20 backdrop-blur-sm">
          <h2 className="text-lg font-semibold text-slate-200 mb-2">Algorithm Catalog</h2>
          <div className="flex-1 overflow-y-auto custom-scrollbar flex flex-col gap-6 pr-2">
            {Object.entries(algorithms).map(([category, algos]) => (
              <div key={category}>
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">{category}</h3>
                <div className="flex flex-col gap-2">
                  {algos.map(algo => (
                    <button
                      key={algo}
                      onClick={() => handleSelectAlgorithm(algo)}
                      className={`text-left px-4 py-2.5 rounded-xl text-sm transition-all ${
                        activeAlgo === algo 
                          ? 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 shadow-[0_0_15px_rgba(34,211,238,0.05)]' 
                          : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200 border border-transparent'
                      }`}
                    >
                      {algo}
                    </button>
                  ))}
                </div>
              </div>
            ))}
          </div>
          <button 
            onClick={handleStart}
            className="w-full mt-4 px-4 py-3 bg-gradient-to-r from-cyan-500 to-indigo-500 hover:from-cyan-400 hover:to-indigo-400 text-white text-sm font-bold rounded-xl transition-all shadow-[0_0_20px_rgba(34,211,238,0.2)]"
          >
            Start Visualization
          </button>
        </div>

        {/* Center Panel: Visualization & Variables */}
        <div className="flex flex-col gap-4 h-full lg:col-span-2">
          
          <div className="flex-1 bg-slate-900/40 p-6 flex flex-col justify-center items-center relative overflow-hidden rounded-2xl border border-slate-800 shadow-xl shadow-black/20 backdrop-blur-sm">
             
             {/* Loading overlay */}
             {isLoading && (
                <div className="absolute inset-0 z-50 flex items-center justify-center bg-slate-950/80 backdrop-blur-md">
                   <div className="flex flex-col items-center gap-4">
                     <div className="w-10 h-10 border-4 border-cyan-500/30 border-t-cyan-400 rounded-full animate-spin shadow-[0_0_15px_rgba(34,211,238,0.2)]"></div>
                     <span className="text-cyan-400 font-mono text-sm font-medium tracking-wider">Loading Animation...</span>
                   </div>
                </div>
             )}

             <div className="absolute top-4 left-4 bg-slate-950/80 px-4 py-1.5 rounded-full border border-slate-800/80 text-xs font-mono text-slate-400 z-10 shadow-lg backdrop-blur-sm">
               {currentState ? currentState.stepTitle : `Select an algorithm to begin`}
             </div>
             
             {activeAlgo === 'Fibonacci DP' ? (
                <FibonacciDPVisualizer />
             ) : visType === 'GRID' ? (
                <DPGridVisualizer dpTable={currentState?.dpTable} />
             ) : visType === 'STACK' ? (
                <StackVisualizer />
             ) : visType === 'QUEUE' ? (
                <QueueVisualizer />
             ) : visType === 'TREE' ? (
                <TreeVisualizer />
             ) : activeAlgo === 'Dijkstra’s Algorithm' ? (
                <DijkstraVisualizer />
             ) : activeAlgo === 'Depth First Search (DFS)' ? (
                <DFSVisualizer />
             ) : activeAlgo === 'Breadth First Search (BFS)' ? (
                <BFSVisualizer />
             ) : activeAlgo === 'Merge Sort' ? (
                <MergeSortVisualizer />
             ) : activeAlgo === 'Binary Search' ? (
                <BinarySearchVisualizer />
             ) : activeAlgo === 'Bubble Sort' ? (
                <BubbleSortVisualizer />
             ) : (
                <ArrayVisualizer />
             )}
          </div>

          <div className="flex justify-center">
            <PlaybackControls />
          </div>
          
          {/* Variables Panel */}
          <div className="h-32 shadow-xl shadow-black/20 rounded-xl overflow-hidden">
             <VariablesPanel />
          </div>

        </div>

        {/* Right Panel: Pseudocode & AI Mentor */}
        <div className="flex flex-col gap-4 h-full lg:col-span-1">
          <div className="h-1/2">
            <PseudocodeEditor code={getCode()} />
          </div>
          <div className="h-1/2 shadow-xl shadow-black/20 rounded-lg overflow-hidden">
            <AIMentorSidebar />
          </div>
        </div>
      </main>
    </div>
  );
};

export default LearnMode;
