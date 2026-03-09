import axios from 'axios';

const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '',
    timeout: 10000,
});

const MOJIBAKE_PATTERN = /[ÃÂÅÆÐÑØæåéèçðþ]/;

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
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        // console.log('[REQ]', config.method?.toUpperCase(), config.url, 'Authorization=', config.headers.Authorization);
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
        // or directly its data? The prompt says "只取 response.data", which gives the wrapper object.
        return res;
    },
    (error) => {
        console.error('Network Error:', error.message);
        return Promise.reject(error);
    }
);

export default request;
