import React, { useRef, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { useExecution } from '../../context/ExecutionContext';

const CodeEditor = () => {
  const { code, setCode, currentState, executeCode, isLoading } = useExecution();
  const editorRef = useRef(null);
  const decorationsRef = useRef([]);

  const handleEditorDidMount = (editor, monaco) => {
    editorRef.current = editor;
  };

  useEffect(() => {
    if (editorRef.current && currentState?.lineNumber) {
      const line = currentState.lineNumber;
      decorationsRef.current = editorRef.current.deltaDecorations(
        decorationsRef.current,
        [
          {
            range: new window.monaco.Range(line, 1, line, 1),
            options: {
              isWholeLine: true,
              className: 'bg-indigo-500/20 border-l-4 border-indigo-400',
            },
          },
        ]
      );
    } else if (editorRef.current) {
       decorationsRef.current = editorRef.current.deltaDecorations(decorationsRef.current, []);
    }
  }, [currentState]);

  const handleRun = () => {
    executeCode(code);
  };

  return (
    <div className="flex flex-col h-full rounded-lg overflow-hidden border border-slate-700/50 bg-[#1e1e1e]">
      <div className="flex justify-between items-center p-2 bg-slate-800 border-b border-slate-700/50">
        <div className="text-xs font-mono text-slate-400 ml-2">Main.java</div>
        <button 
          onClick={handleRun}
          disabled={isLoading}
          className="px-4 py-1.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 text-sm font-semibold rounded disabled:opacity-50 transition-colors"
        >
          {isLoading ? 'Executing...' : 'Run Code'}
        </button>
      </div>
      <div className="flex-1">
        <Editor
          height="100%"
          defaultLanguage="java"
          theme="vs-dark"
          value={code}
          onChange={(value) => setCode(value)}
          onMount={handleEditorDidMount}
          options={{
            minimap: { enabled: false },
            fontSize: 14,
            padding: { top: 16 },
            scrollBeyondLastLine: false,
            smoothScrolling: true,
            cursorBlinking: "smooth",
          }}
        />
      </div>
    </div>
  );
};

export default CodeEditor;
