import React, { useRef, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { useExecution } from '../../context/ExecutionContext';

const PseudocodeEditor = ({ code }) => {
  const { currentState } = useExecution();
  const editorRef = useRef(null);
  const decorationsRef = useRef([]);

  const handleEditorDidMount = (editor) => {
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
              className: 'bg-emerald-500/20 border-l-4 border-emerald-400',
            },
          },
        ]
      );
    } else if (editorRef.current) {
       decorationsRef.current = editorRef.current.deltaDecorations(decorationsRef.current, []);
    }
  }, [currentState]);

  return (
    <div className="flex flex-col h-full rounded-lg overflow-hidden border border-slate-700/50 bg-[#1e1e1e] shadow-xl shadow-black/20">
      <div className="flex justify-between items-center p-2 bg-slate-800 border-b border-slate-700/50">
        <span className="text-xs font-mono text-slate-400 ml-2">Pseudocode</span>
      </div>
      <div className="flex-1">
        <Editor
          height="100%"
          defaultLanguage="java"
          theme="vs-dark"
          value={code}
          onMount={handleEditorDidMount}
          options={{
            readOnly: true,
            minimap: { enabled: false },
            fontSize: 13,
            padding: { top: 16 },
            scrollBeyondLastLine: false,
            smoothScrolling: true,
            cursorBlinking: "solid",
            lineNumbersMinChars: 3,
            renderLineHighlight: "none",
            hideCursorInOverviewRuler: true
          }}
        />
      </div>
    </div>
  );
};

export default PseudocodeEditor;
