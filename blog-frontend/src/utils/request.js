/**
 * Axios 请求封装
 * 基于 axios 创建统一的 HTTP 请求实例，自动附加 JWT Token，
 * 处理 401/403 响应时清除 Token 并跳转到登录页。
 */
import axios from 'axios';
import { message } from 'antd';
import { clearAdminToken, getAdminToken } from '../auth/storage';
import { adminAssetPath } from './path';

const apiBaseURL = import.meta.env.VITE_API_BASE_URL || '';

const request = axios.create({
  baseURL: apiBaseURL,
  timeout: 10000,
});

/**
 * 请求拦截器
 * - 自动在请求头添加 Authorization: Bearer ${token}
 */
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

/**
 * 响应拦截器
 * - 自动解包响应数据（response.data）
 * - 遇到 401/403 时清除 Token 并跳转到登录页
 * - 显示错误提示
 */
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status;
    const errorData = error.response?.data;

    // 处理 401/403 认证错误
    if (status === 401 || status === 403) {
      clearAdminToken();
      message.error('登录已过期，请重新登录');
      window.location.href = adminAssetPath('login');
      return Promise.reject(error);
    }

    // 显示错误提示
    const errorMessage = errorData?.message || error.message || '请求失败';
    message.error(errorMessage);

    return Promise.reject(error);
  }
);

export default request;

