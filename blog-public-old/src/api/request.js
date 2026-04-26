/*
 * 功能：前端模块逻辑。
 */
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

const request = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    withCredentials: true,
});

export const getStaticUrl = (path) => {
    if (!path) return path;
    if (path.startsWith('http://') || path.startsWith('https://')) return path;
    if (path.startsWith('/uploads/')) return API_BASE_URL + path;
    return path;
};

const MOJIBAKE_PATTERN = /[脙脗脜脝脨脩脴忙氓茅猫莽冒镁]/;

const tryDecodeLatin1Utf8 = (value) => {
    if (typeof value !== 'string' || value.length === 0) return value;
    if (!MOJIBAKE_PATTERN.test(value)) return value;

    try {
        const bytes = Uint8Array.from(value, (char) => char.charCodeAt(0) & 0xff);
        const decoded = new TextDecoder('utf-8', { fatal: true }).decode(bytes);
        return decoded;
    } catch {
        return value;
    }
};

const normalizePayloadText = (value) => {
    if (typeof value === 'string') {
        return tryDecodeLatin1Utf8(value);
    }
    if (Array.isArray(value)) {
        return value.map(normalizePayloadText);
    }
    if (value && typeof value === 'object') {
        const normalized = {};
        Object.keys(value).forEach((key) => {
            normalized[key] = normalizePayloadText(value[key]);
        });
        return normalized;
    }
    return value;
};

// Request interceptor - add Authorization header
request.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('community_token');
        if (token) {
            config.headers['X-Community-Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor
request.interceptors.response.use(
    (response) => {
        const res = normalizePayloadText(response.data);
        // We expect the backend format {code, message, data}
        // But sometimes it might be slightly different. Handle according to spec.
        if (res && res.code && res.code !== 200) {
            console.error('API Error:', res.message || 'Error occurred');
            // For public blog, we throw the error so that pages can catch it and show UI
            return Promise.reject(new Error(res.message || 'Error occurred'));
        }
        // Spec says to return only response.data
        // which in this case means returning `res` which is the whole {code, data, message} block 
        // or directly its data? The prompt says "鍙彇 response.data", which gives the wrapper object.
        return res;
    },
    (error) => {
        const errorData = error?.response?.data;
        let message = errorData?.message || error.message || '缃戠粶閿欒';
        // 濡傛灉鏄獙璇侀敊璇紝data 涓寘鍚叿浣撳瓧娈甸敊璇?        if (errorData?.data && typeof errorData.data === 'object') {
            const fieldErrors = Object.values(errorData.data).join('锛?);
            message = fieldErrors || message;
        }
        message = normalizePayloadText(message);
        console.error('缃戠粶閿欒:', message);
        return Promise.reject(new Error(message));
    }
);

export default request;

