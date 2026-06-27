import axios, { type AxiosError, type InternalAxiosRequestConfig } from "axios";
import toast from "react-hot-toast";
import { clearAdminToken, getAdminToken, setAdminToken } from "./auth";

const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || "";

type RetriableConfig = InternalAxiosRequestConfig & { _retry?: boolean };

const request = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    withCredentials: true,
});

const authClient = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    withCredentials: true,
});

let refreshPromise: Promise<string | null> | null = null;

export const getStaticUrl = (path: string) => {
    if (!path) return path;
    if (path.startsWith("http://") || path.startsWith("https://")) return path;
    if (path.startsWith("/uploads/")) return API_BASE_URL + path;
    return path;
};

const isAdminRequest = (url?: string) => Boolean(url?.startsWith("/api/admin"));
const isAdminAuthEndpoint = (url?: string) => url === "/api/admin/login" || url === "/api/admin/refresh";

export const refreshAdminSession = async () => {
    if (!refreshPromise) {
        refreshPromise = authClient.post("/api/admin/refresh")
            .then((response) => {
                const payload = response.data?.data || response.data;
                const token = payload?.token || null;
                setAdminToken(token);
                return token;
            })
            .catch(() => {
                clearAdminToken();
                return null;
            })
            .finally(() => {
                refreshPromise = null;
            });
    }
    return refreshPromise;
};

const redirectToAdminLogin = () => {
    clearAdminToken();
    if (window.location.pathname !== "/admin-pxczxn/login") {
        toast.error("登录已过期，请重新登录");
        window.location.href = "/admin-pxczxn/login";
    }
};

const replayAdminRequest = async (config: RetriableConfig) => {
    if (!isAdminRequest(config.url) || isAdminAuthEndpoint(config.url) || config._retry) {
        return null;
    }

    config._retry = true;
    const token = await refreshAdminSession();
    if (!token) {
        redirectToAdminLogin();
        return null;
    }

    config.headers.set("Authorization", `Bearer ${token}`);
    return request(config);
};

request.interceptors.request.use(
    (config) => {
        if (isAdminRequest(config.url) && !isAdminAuthEndpoint(config.url)) {
            const adminToken = getAdminToken();
            if (adminToken) {
                config.headers.set("Authorization", `Bearer ${adminToken}`);
            }
        } else if (!isAdminRequest(config.url)) {
            const token = localStorage.getItem("community_token");
            if (token) {
                config.headers.set("X-Community-Authorization", `Bearer ${token}`);
            }
        }
        return config;
    },
    (error) => Promise.reject(error),
);

request.interceptors.response.use(
    async (response) => {
        const res = response.data;
        if (res && res.code && res.code !== 200) {
            if ((res.code === 401 || res.code === 403) && isAdminRequest(response.config.url)) {
                const replayed = await replayAdminRequest(response.config as RetriableConfig);
                if (replayed) return replayed;
                redirectToAdminLogin();
            } else {
                toast.error(res.message || "操作失败");
            }
            return Promise.reject(new Error(res.message || "Error occurred"));
        }
        return res?.data ?? res;
    },
    async (error: AxiosError<any>) => {
        const errorData = error?.response?.data;
        const status = error?.response?.status;
        const config = error.config as RetriableConfig | undefined;

        if ((status === 401 || status === 403) && config && isAdminRequest(config.url)) {
            const replayed = await replayAdminRequest(config);
            if (replayed) return replayed;
            redirectToAdminLogin();
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
