import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'https://algomind-ai-axmv.onrender.com').replace(/\/$/, '');
const WS_URL = import.meta.env.VITE_WS_URL || `${API_BASE_URL.replace(/^http:\/\//, 'ws://').replace(/^https:\/\//, 'wss://')}/ws`;

class WebSocketService {
  constructor() {
    this.client = null;
    this.subscriptions = new Map();
  }

  connect(onConnect, onError) {
    if (this.client && this.client.connected) {
      if (onConnect) onConnect();
      return;
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('STOMP Client connected');
        if (onConnect) onConnect();
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
        if (onError) onError(frame);
      },
      onWebSocketError: (error) => {
        console.error('WebSocket Error:', error);
        if (onError) onError(error);
      }
    });

    this.client.activate();
  }

  subscribeToExecution(sessionId, callback) {
    if (!this.client || !this.client.connected) {
      console.error('Cannot subscribe: STOMP client is not connected.');
      return;
    }

    const topic = `/topic/execution/${sessionId}`;
    
    // Unsubscribe if already subscribed to this session
    this.unsubscribe(topic);

    const subscription = this.client.subscribe(topic, (message) => {
      if (message.body) {
        callback(JSON.parse(message.body));
      }
    });

    this.subscriptions.set(topic, subscription);
  }

  unsubscribe(topic) {
    if (this.subscriptions.has(topic)) {
      this.subscriptions.get(topic).unsubscribe();
      this.subscriptions.delete(topic);
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.subscriptions.clear();
      console.log('STOMP Client disconnected');
    }
  }
}

export default new WebSocketService();
