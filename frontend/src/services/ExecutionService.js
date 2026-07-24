import axios from 'axios';

const API_BASE_URL = `${import.meta.env.VITE_API_BASE_URL}/api/v1`;

export const ExecutionService = {
  executeCode: async (code, sessionId) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/execute`, {
        code,
        sessionId
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Execution API error:', error);
      throw error;
    }
  },
  
  executeAlgorithm: async (algorithmName, sessionId) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/execute`, {
        code: `${algorithmName}()`,
        sessionId
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Execution API error:', error);
      throw error;
    }
  },

  getLearnAlgorithm: async (algorithmId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/learn/${algorithmId}`);
      return response.data;
    } catch (error) {
      console.error('Learn API error:', error);
      throw error;
    }
  }
};
