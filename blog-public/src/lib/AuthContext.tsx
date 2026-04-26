




import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import request from './request';

export const COMMUNITY_TOKEN_KEY = 'community_token';

const readToken = () => localStorage.getItem(COMMUNITY_TOKEN_KEY);

const persistToken = (token: string | null) => {
    if (token) {
        localStorage.setItem(COMMUNITY_TOKEN_KEY, token);
    } else {
        localStorage.removeItem(COMMUNITY_TOKEN_KEY);
    }
};

export interface UserProfile {
    userId: number;
    username: string;
    displayName: string;
    avatar: string;
    bio: string;
    website: string;
    role?: string;
    status?: string;
    followerCount: number;
    followingCount: number;
    followedByMe: boolean;
}

interface AuthContextType {
    user: UserProfile | null;
    initializing: boolean;
    login: (payload: any) => Promise<UserProfile>;
    register: (payload: any) => Promise<UserProfile>;
    logout: () => Promise<void>;
    refreshUser: () => Promise<UserProfile>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<UserProfile | null>(null);
    const [initializing, setInitializing] = useState(true);

    useEffect(() => {
        const bootstrap = async () => {
            if (!readToken()) {
                setInitializing(false);
                return;
            }
            try {
                const res: any = await request.get('/api/community/me');
                setUser(res?.data ?? res);
            } catch {
                persistToken(null);
                setUser(null);
            } finally {
                setInitializing(false);
            }
        };

        bootstrap();
    }, []);

    const value = useMemo(() => ({
        user,
        initializing,
        async login(payload: any) {
            const res: any = await request.post('/api/community/auth/login', {
                identifier: payload.identifier ?? payload.username,
                password: payload.password,
            });
            const token = res?.data?.token ?? res?.token;
            persistToken(token);
            const meRes: any = await request.get('/api/community/me');
            const me = meRes?.data ?? meRes;
            setUser(me);
            return me;
        },
        async register(payload: any) {
            const res: any = await request.post('/api/community/auth/register', {
                ...payload,
                displayName: payload.displayName ?? payload.username,
            });
            const token = res?.data?.token ?? res?.token;
            persistToken(token);
            const meRes: any = await request.get('/api/community/me');
            const me = meRes?.data ?? meRes;
            setUser(me);
            return me;
        },
        async logout() {
            try {
                await request.post('/api/community/logout');
            } catch (e) {
                
            } finally {
                persistToken(null);
                setUser(null);
            }
        },
        async refreshUser() {
            const meRes: any = await request.get('/api/community/me');
            const me = meRes?.data ?? meRes;
            setUser(me);
            return me;
        }
    }), [initializing, user]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    const value = useContext(AuthContext);
    if (!value) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return value;
};
