import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ExecutionProvider } from './context/ExecutionContext.jsx';
import { Toaster } from 'react-hot-toast';

import LandingPage from './pages/LandingPage.jsx';
import LearnMode from './pages/LearnMode.jsx';
import CodeVisualizerMode from './pages/CodeVisualizerMode.jsx';

function App() {
  return (
    <BrowserRouter>
      <ExecutionProvider>
        <Toaster position="top-right" />
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/learn" element={<LearnMode />} />
          <Route path="/visualize" element={<CodeVisualizerMode />} />
        </Routes>
      </ExecutionProvider>
    </BrowserRouter>
  );
}

export default App;
