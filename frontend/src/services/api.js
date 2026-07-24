import axios from 'axios';

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'https://algomind-ai-axmv.onrender.com').replace(/\/$/, '');
const API_V1_BASE_URL = `${API_BASE_URL}/api/v1`;
const WS_URL = import.meta.env.VITE_WS_URL || `${API_BASE_URL.replace(/^http:\/\//, 'ws://').replace(/^https:\/\//, 'wss://')}/ws`;

const api = axios.create({
  baseURL: API_V1_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export { API_BASE_URL, API_V1_BASE_URL, WS_URL, api };
