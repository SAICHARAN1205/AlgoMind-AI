import React from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';

const LandingPage = () => {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans selection:bg-cyan-500/30">
      {/* Navigation */}
      <nav className="flex justify-between items-center px-8 py-6 max-w-7xl mx-auto border-b border-slate-800/50">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-cyan-500 to-indigo-500 shadow-lg shadow-cyan-500/20" />
          <h1 className="text-xl font-bold tracking-tight">AlgoMind<span className="text-cyan-400">AI</span></h1>
        </div>
        <div className="flex gap-4">
          <a href="https://github.com/your-github" target="_blank" rel="noreferrer" className="px-4 py-2 text-sm font-medium text-slate-300 hover:text-white transition-colors">GitHub</a>
        </div>
      </nav>

      {/* Hero Section */}
      <main className="max-w-7xl mx-auto px-8 pt-24 pb-32 text-center">
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-slate-900 border border-slate-800 mb-8"
        >
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
          <span className="text-xs font-medium text-slate-400">Universal DSA Visualization Engine</span>
        </motion.div>

        <motion.h1 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.1 }}
          className="text-5xl md:text-7xl font-extrabold tracking-tight mb-8 leading-tight"
        >
          Master Algorithms with <br/>
          <span className="bg-gradient-to-r from-cyan-400 via-indigo-400 to-purple-400 bg-clip-text text-transparent">
            Visual Intelligence
          </span>
        </motion.h1>

        <motion.p 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.2 }}
          className="text-lg text-slate-400 mb-12 max-w-2xl mx-auto"
        >
          AlgoMind AI is an interactive DSA platform that dynamically visualizes your code, explains complex concepts, and mentors you through dynamic programming and recursion.
        </motion.p>

        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.3 }}
          className="flex justify-center gap-4"
        >
          <Link to="/learn" className="px-8 py-4 text-sm font-semibold bg-cyan-500 hover:bg-cyan-400 text-slate-950 rounded-full transition-all shadow-[0_0_30px_rgba(6,182,212,0.3)]">
            Learn Mode (Beginners)
          </Link>
          <Link to="/visualize" className="px-8 py-4 text-sm font-semibold bg-slate-900 hover:bg-slate-800 border border-slate-800 rounded-full transition-colors flex items-center justify-center">
            Code Visualizer (Advanced)
          </Link>
        </motion.div>

        {/* Feature Grid */}
        <motion.div 
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.5 }}
          className="grid md:grid-cols-3 gap-6 mt-32 text-left"
        >
          <div className="p-6 rounded-2xl bg-slate-900/50 border border-slate-800/50 backdrop-blur-sm">
            <div className="w-10 h-10 rounded-lg bg-cyan-500/20 flex items-center justify-center mb-4 border border-cyan-500/30">
               <span className="text-cyan-400 font-bold">DP</span>
            </div>
            <h3 className="text-lg font-semibold mb-2">Dynamic Programming Grid</h3>
            <p className="text-sm text-slate-400">Visually trace through overlapping subproblems, memoization caches, and bottom-up tabulation tables step-by-step.</p>
          </div>
          
          <div className="p-6 rounded-2xl bg-slate-900/50 border border-slate-800/50 backdrop-blur-sm relative overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-indigo-500/10 rounded-full blur-2xl transform translate-x-1/2 -translate-y-1/2" />
            <div className="w-10 h-10 rounded-lg bg-indigo-500/20 flex items-center justify-center mb-4 border border-indigo-500/30">
               <span className="text-indigo-400 font-bold">AI</span>
            </div>
            <h3 className="text-lg font-semibold mb-2">Intelligent AI Mentor</h3>
            <p className="text-sm text-slate-400">Get context-aware hints, automated complexity analysis (Big-O), and friendly translations of complex runtime errors.</p>
          </div>

          <div className="p-6 rounded-2xl bg-slate-900/50 border border-slate-800/50 backdrop-blur-sm">
            <div className="w-10 h-10 rounded-lg bg-emerald-500/20 flex items-center justify-center mb-4 border border-emerald-500/30">
               <span className="text-emerald-400 font-bold">T</span>
            </div>
            <h3 className="text-lg font-semibold mb-2">Execution Timelines</h3>
            <p className="text-sm text-slate-400">Scrub forward and backward through your algorithm's exact execution path, watching variables and stack frames update live.</p>
          </div>
        </motion.div>
      </main>
    </div>
  );
};

export default LandingPage;
