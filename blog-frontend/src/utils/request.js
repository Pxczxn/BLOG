import axios from 'axios';
import { adminAssetPath } from './path';

const apiBaseURL = import.meta.env.VITE_API_BASE_URL || '';

const request = axios.create({
  baseURL: apiBaseURL,
  timeout: 10000,
});

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // console.log('[REQ]', config.method?.toUpperCase(), config.url, 'Authorization=', config.headers?.Authorization);
    return config;
  },
  (error) => Promise.reject(error)
);

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response && [401, 403].includes(error.response.status)) {
      localStorage.removeItem('token');
      window.location.href = adminAssetPath('login');
    }
    return Promise.reject(error);
  }
);

export default request;
