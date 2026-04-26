




import axios from "axios";
import toast from "react-hot-toast";
import { getAdminToken, clearAdminToken } from "./auth";

const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || "";

const request = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    withCredentials: true,
});


export const getStaticUrl = (path: string) => {
    if (!path) return path;
    if (path.startsWith("http://") || path.startsWith("https://")) return path;
    if (path.startsWith("/uploads/")) return API_BASE_URL + path;
    return path;
};


request.interceptors.request.use(
    (config) => {
        
        if (config.url?.startsWith('/api/admin')) {
            const adminToken = getAdminToken();
            if (adminToken) {
                config.headers['Authorization'] = `Bearer ${adminToken}`;
            }
        } else {
            
            const token = localStorage.getItem("community_token");
            if (token) {
                config.headers["X-Community-Authorization"] = `Bearer ${token}`;
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    },
);


request.interceptors.response.use(
    (response) => {
        const res = response.data;
        if (res && res.code && res.code !== 200) {
            
            if (res.code === 401 || res.code === 403) {
                 if (response.config.url?.startsWith('/api/admin') && response.config.url !== '/api/admin/login') {
                      clearAdminToken();
                      toast.error('登录已过期，请重新登录');
                      window.location.href = '/admin-pxczxn/login';
                 }
            } else {
                
                toast.error(res.message || "操作失败");
            }
            return Promise.reject(new Error(res.message || "Error occurred"));
        }
        return res;
    },
    (error) => {
        const errorData = error?.response?.data;
        const status = error?.response?.status;

        
        if (status === 401 || status === 403) {
            if (error.config.url?.startsWith('/api/admin') && error.config.url !== '/api/admin/login') {
                clearAdminToken();
                toast.error('登录已过期，请重新登录');
                window.location.href = '/admin-pxczxn/login';
            }
        } else {
            
            let message = errorData?.message || error.message || "网络错误";
            if (errorData?.data && typeof errorData.data === "object") {
                const fieldErrors = Object.values(errorData.data).join(", ");
                message = fieldErrors || message;
            }
            toast.error(message);
        }

        let message = errorData?.message || error.message || "Network error";
        if (errorData?.data && typeof errorData.data === "object") {
            const fieldErrors = Object.values(errorData.data).join(", ");
            message = fieldErrors || message;
        }
        return Promise.reject(new Error(message));
    },
);

export default request;
