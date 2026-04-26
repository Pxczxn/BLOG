




import axios from 'axios';
import { message } from 'antd';
import { clearAdminToken, getAdminToken } from '../auth/storage';
import { adminAssetPath } from './path';

const apiBaseURL = import.meta.env.VITE_API_BASE_URL || '';

const request = axios.create({
  baseURL: apiBaseURL,
  timeout: 10000,
});





request.interceptors.request.use(
  (config) => {
    const token = getAdminToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);







request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status;
    const errorData = error.response?.data;

    
    if (status === 401 || status === 403) {
      clearAdminToken();
      message.error('登录已过期，请重新登录');
      window.location.href = adminAssetPath('login');
      return Promise.reject(error);
    }

    
    const errorMessage = errorData?.message || error.message || '请求失败';
    message.error(errorMessage);

    return Promise.reject(error);
  }
);

export default request;

