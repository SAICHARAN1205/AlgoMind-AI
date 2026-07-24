import axios from 'axios';

const API_BASE_URL = import.meta.env.PROD 
  ? 'https://algomind-ai-axmv.onrender.com'
  : (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '');
const API_V1_BASE_URL = `${API_BASE_URL}/api/v1`;

// SockJS requires http:// or https:// URLs — it handles the WebSocket upgrade internally.
// Do NOT pass ws:// or wss:// to SockJS, or the browser will throw:
// "The URL's scheme must be either http: or https:. 'ws:' is not allowed."
const SOCKJS_URL = `${API_BASE_URL}/ws`;

const api = axios.create({
  baseURL: API_V1_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export { API_BASE_URL, API_V1_BASE_URL, SOCKJS_URL, api };
