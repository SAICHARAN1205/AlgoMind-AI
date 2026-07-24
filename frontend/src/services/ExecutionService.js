import { api } from './api.js';

export const ExecutionService = {
  executeCode: async (code, sessionId) => {
    try {
      const response = await api.post('/execute', {
        code,
        sessionId
      });
      return response.data;
    } catch (error) {
      console.error('Execution API error:', error);
      throw error;
    }
  },
  
  executeAlgorithm: async (algorithmName, sessionId) => {
    try {
      const response = await api.post('/execute', {
        code: `${algorithmName}()`,
        sessionId
      });
      return response.data;
    } catch (error) {
      console.error('Execution API error:', error);
      throw error;
    }
  },

  getLearnAlgorithm: async (algorithmId) => {
    try {
      const response = await api.get(`/learn/${algorithmId}`);
      return response.data;
    } catch (error) {
      console.error('Learn API error:', error);
      throw error;
    }
  }
};
