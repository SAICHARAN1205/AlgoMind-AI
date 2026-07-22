import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Editor } from '@monaco-editor/react';
import { toast } from 'react-hot-toast';
import { useExecution } from '../context/ExecutionContext';

// Import visualizers
import ArrayVisualizer from '../components/visualizer/ArrayVisualizer.jsx';
import BinarySearchVisualizer from '../components/visualizer/BinarySearchVisualizer.jsx';
import MergeSortVisualizer from '../components/visualizer/MergeSortVisualizer.jsx';
import FibonacciDPVisualizer from '../components/visualizer/FibonacciDPVisualizer.jsx';
import BFSVisualizer from '../components/visualizer/BFSVisualizer.jsx';
import DFSVisualizer from '../components/visualizer/DFSVisualizer.jsx';
import DijkstraVisualizer from '../components/visualizer/DijkstraVisualizer.jsx';
import DPGridVisualizer from '../components/visualizer/DPGridVisualizer.jsx';
import AIMentorPanel from '../components/visualizer/AIMentorPanel.jsx';

import PlaybackControls from '../components/controls/PlaybackControls.jsx';
import VariablesPanel from '../components/visualizer/VariablesPanel.jsx';

const BOILERPLATES = {
  java: `class Solution {
    public void execute() {
        // Paste your algorithm code here
        // Example: Bubble Sort
        int[] arr = {8, 3, 5, 1, 9, 6};
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // swap arr[j] and arr[j+1]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
}`,
  python: `class Solution:
    def execute(self):
        # Paste your algorithm code here
        # Example: Binary Search
        arr = [1, 3, 5, 6, 8, 9]
        target = 5
        low = 0
        high = len(arr) - 1
        
        while low <= high:
            mid = (low + high) // 2
            if arr[mid] == target:
                return mid
            elif arr[mid] < target:
                low = mid + 1
            else:
                high = mid - 1
        return -1
`,
  cpp: `class Solution {
public:
    void execute() {
        // Paste your algorithm code here
        
    }
};`,
  javascript: `class Solution {
    execute() {
        // Paste your algorithm code here
        
    }
}`
};

const CodeVisualizerMode = () => {
  const [language, setLanguage] = useState('java');
  const [manualAlgorithm, setManualAlgorithm] = useState('AUTO');
  const [code, setCode] = useState(BOILERPLATES['java']);
  const [isVisualizing, setIsVisualizing] = useState(false);
  const [detectedAlgo, setDetectedAlgo] = useState(null);
  const [explanation, setExplanation] = useState(null);
  const [visType, setVisType] = useState(null);
  
  const [aiInsights, setAiInsights] = useState(null);
  const [isAiLoading, setIsAiLoading] = useState(false);
  const [errorShown, setErrorShown] = useState(false);

  const { currentState, setExecutionTimeline, setIsLoading, isLoading, actions } = useExecution();

  useEffect(() => {
      setCode(BOILERPLATES[language]);
  }, [language]);

  const handleVisualize = async () => {
      setIsVisualizing(true);
      setIsLoading(true);
      setDetectedAlgo(null);
      setExplanation(null);
      setErrorShown(false);
      
      try {
          const response = await fetch('http://localhost:8080/api/v1/visualize', {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify({ language, code, manualAlgorithm })
          });
          
          const data = await response.json();
          
          if (!response.ok) {
              throw new Error(data.message || "Failed to analyze code");
          }
          
          if (!data || !data.states || data.states.length === 0) {
              throw new Error("No visualization states returned from the engine.");
          }
          
          // Fire and forget AI insights using the requested override or detected algorithm
          const resolvedAlgorithm = manualAlgorithm !== 'AUTO' ? manualAlgorithm : (data.detectedAlgorithm || "UNKNOWN");
          fetchAIInsights(resolvedAlgorithm).catch(console.error);
          
          setDetectedAlgo(data.detectedAlgorithm);
          setVisType(data.visualizationType);
          setExplanation(data.explanation);
          
          if (data.lowConfidence) {
              toast('Could not confidently detect algorithm. Please select it manually if visualization looks incorrect.', { icon: '⚠️' });
          } else {
              toast.success("Algorithm successfully loaded!");
          }
          
          // Ensure playback resets before setting timeline
          actions.reset();
          setExecutionTimeline(data.states);
          
          // Play immediately after setting
          setTimeout(() => actions.play(), 50);
          
      } catch (err) {
          console.error("Visualization error:", err);
          if (!errorShown) {
              toast.error(err.message || "An unexpected error occurred. Please try again.");
              setErrorShown(true);
          }
      } finally {
          setIsLoading(false);
          // turn off the overlay after a short delay so the animation finishes
          setTimeout(() => setIsVisualizing(false), 500);
      }
  };

  const fetchAIInsights = async (algoType) => {
      setIsAiLoading(true);
      try {
          const response = await fetch('http://localhost:8080/api/v1/mentor/analyze', {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify({ 
                  algorithmType: algoType,
                  userCode: code,
                  errorMessage: "" 
              })
          });
          
          if (response.ok) {
              const insights = await response.json();
              setAiInsights(insights);
          } else {
              setAiInsights({ summary: "AI mentor temporarily unavailable. Proceeding with visualization." });
          }
      } catch (err) {
          console.error("AI Mentor fetch failed:", err);
          setAiInsights({ summary: "AI mentor temporarily unavailable. Proceeding with visualization." });
      } finally {
          setIsAiLoading(false);
      }
  };

  const renderVisualizer = () => {
      if (!currentState) {
          return (
              <div className="flex-1 h-full flex flex-col items-center justify-center text-slate-500 opacity-50 border-2 border-dashed border-slate-700 rounded-xl m-4 bg-slate-900/20">
                  <svg className="w-16 h-16 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
                  </svg>
                  <p className="font-mono">Write code and click Visualize</p>
              </div>
          );
      }
      
      // Determine what to render based on algo and vis type
      if (detectedAlgo === 'bubble-sort') return <ArrayVisualizer />;
      if (detectedAlgo === 'selection-sort') return <ArrayVisualizer />;
      if (detectedAlgo === 'insertion-sort') return <ArrayVisualizer />;
      if (detectedAlgo === 'binary-search') return <BinarySearchVisualizer />;
      if (detectedAlgo === 'merge-sort') return <MergeSortVisualizer />;
      if (detectedAlgo === 'bfs') return <BFSVisualizer />;
      if (detectedAlgo === 'dfs') return <DFSVisualizer />;
      if (detectedAlgo === 'dijkstra') return <DijkstraVisualizer />;
      if (detectedAlgo === 'fibonacci-dp') {
           if (visType === 'GRID') {
               return currentState.dpMode ? <FibonacciDPVisualizer /> : <DPGridVisualizer dpTable={currentState.dpTable} />;
           }
           return <FibonacciDPVisualizer />; // fallback
      }
      
      return <ArrayVisualizer />;
  };

  return (
    <div className="h-screen bg-slate-950 flex flex-col overflow-hidden relative">
      
      {/* Top Banner */}
      <div className="bg-slate-900 border-b border-slate-800 p-4 shadow-md flex items-center justify-between z-10">
        <div>
            <h1 className="text-xl font-bold text-white flex items-center gap-2">
                <span className="text-purple-500 font-mono text-2xl">{'</>'}</span> 
                Code Visualizer
            </h1>
            <p className="text-slate-400 text-sm mt-1">Paste your algorithm and see it execute instantly.</p>
        </div>
        
        <div className="flex items-center gap-6">
            {detectedAlgo && (
                <div className="flex items-center gap-3 bg-slate-950 px-4 py-2 rounded-full border border-slate-700 shadow-inner">
                    <span className="text-xs text-slate-400 uppercase tracking-widest font-bold">Detected:</span>
                    <span className="text-sm text-cyan-400 font-mono font-bold capitalize px-2 py-0.5 bg-cyan-900/30 rounded border border-cyan-800">{detectedAlgo.replace('-', ' ')}</span>
                </div>
            )}
            <Link to="/" className="text-sm font-medium text-slate-400 hover:text-cyan-400 transition-colors">
                Switch to Learn Mode
            </Link>
        </div>
      </div>
      
      {/* Main Split Content */}
      <div className="flex-1 flex overflow-hidden min-h-0">
          
          {/* LEFT: Editor Pane (30%) */}
          <div className="w-[30%] min-w-0 flex flex-col border-r border-slate-800 bg-slate-900 z-10 shadow-[10px_0_15px_-3px_rgba(0,0,0,0.3)]">
              
              <div className="flex items-center justify-between p-3 bg-slate-800/50 border-b border-slate-700">
                  <div className="flex gap-2">
                      <select 
                          value={language}
                          onChange={(e) => setLanguage(e.target.value)}
                          className="bg-slate-950 text-slate-300 border border-slate-700 rounded-lg px-3 py-1.5 text-sm font-medium focus:outline-none focus:border-purple-500"
                      >
                          <option value="java">Java</option>
                          <option value="python">Python</option>
                          <option value="cpp">C++</option>
                          <option value="javascript">JavaScript</option>
                      </select>
                      
                      <select 
                          value={manualAlgorithm}
                          onChange={(e) => setManualAlgorithm(e.target.value)}
                          className="bg-slate-950 text-slate-300 border border-slate-700 rounded-lg px-3 py-1.5 text-sm font-medium focus:outline-none focus:border-purple-500 max-w-[150px]"
                      >
                          <option value="AUTO">Auto Detect</option>
                          <option value="bubble-sort">Bubble Sort</option>
                          <option value="selection-sort">Selection Sort</option>
                          <option value="insertion-sort">Insertion Sort</option>
                          <option value="merge-sort">Merge Sort</option>
                          <option value="binary-search">Binary Search</option>
                          <option value="bfs">BFS</option>
                          <option value="dfs">DFS</option>
                          <option value="dijkstra">Dijkstra</option>
                      </select>
                  </div>
                  
                  <button 
                      onClick={handleVisualize}
                      disabled={isLoading}
                      className="bg-purple-600 hover:bg-purple-500 text-white px-5 py-1.5 rounded-lg text-sm font-bold shadow-[0_0_15px_rgba(147,51,234,0.3)] hover:shadow-[0_0_20px_rgba(147,51,234,0.5)] transition-all flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                      {isLoading ? (
                          <>
                              <svg className="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                              Analyzing...
                          </>
                      ) : (
                          <>
                              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" /><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                              Visualize Code
                          </>
                      )}
                  </button>
              </div>
              
              <div className="flex-1 p-2">
                  <div className="h-full rounded-lg overflow-hidden border border-slate-700 shadow-inner">
                      <Editor
                        height="100%"
                        language={language}
                        theme="vs-dark"
                        value={code}
                        onChange={(value) => setCode(value)}
                        options={{
                          minimap: { enabled: false },
                          fontSize: 14,
                          fontFamily: "'Fira Code', 'JetBrains Mono', monospace",
                          fontLigatures: true,
                          padding: { top: 16 },
                          scrollBeyondLastLine: false,
                          smoothScrolling: true,
                          cursorBlinking: "smooth",
                        }}
                      />
                  </div>
              </div>
          </div>
          
          {/* MIDDLE: Visualizer Pane (45%) */}
          <div className="w-[45%] min-w-0 flex flex-col relative bg-[#0b1120] border-r border-slate-800 overflow-hidden">
             
             {isVisualizing && (
                 <div className="absolute inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex flex-col items-center justify-center p-8 text-center transition-all animate-[fadeIn_0.3s_ease-out_forwards] pointer-events-none">
                      <div className="w-16 h-16 rounded-full border-4 border-purple-500/30 border-t-purple-500 animate-spin mb-6"></div>
                      <h2 className="text-xl font-bold text-white mb-2">Analyzing Patterns...</h2>
                      <p className="text-slate-400 font-mono text-sm max-w-sm">
                          Engine is evaluating code syntax, mapping nested loops, calculating recursion trees, and building execution graphs...
                      </p>
                 </div>
             )}
              
             {/* Visualizer Area */}
             <div className="flex-1 p-4 pb-2 relative overflow-hidden flex flex-col min-h-0">
                 <div className="w-full flex-1 min-h-0 mb-4 rounded-xl relative shadow-2xl overflow-hidden">
                     {renderVisualizer()}
                 </div>
                 
                 <div className="h-[35%] min-h-[220px] flex gap-4 shrink-0">
                     {/* Playback Controls */}
                     <div className="w-3/5 flex flex-col">
                         <PlaybackControls />
                     </div>
                     
                     {/* Variables Panel */}
                     <div className="w-2/5 flex flex-col gap-4">
                         <div className="flex-1 bg-slate-900 border border-slate-700/50 rounded-xl overflow-hidden shadow-lg">
                             <VariablesPanel />
                         </div>
                     </div>
                 </div>
             </div>
          </div>
          
          {/* RIGHT: AI Mentor Sidebar (25%) */}
          <div className="w-[25%] bg-[#0b1120] p-4 flex flex-col relative overflow-hidden">
              <AIMentorPanel insights={aiInsights} isLoading={isAiLoading} />
          </div>
      </div>
    </div>
  );
};

export default CodeVisualizerMode;
