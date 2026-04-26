/*
 * 功能：前端模块逻辑。
 */
import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { communityApi, COMMUNITY_TOKEN_KEY } from '../api/community';

const AuthContext = createContext(null);

const readToken = () => localStorage.getItem(COMMUNITY_TOKEN_KEY);

const persistToken = (token) => {
    if (token) {
        localStorage.setItem(COMMUNITY_TOKEN_KEY, token);
    } else {
        localStorage.removeItem(COMMUNITY_TOKEN_KEY);
    }
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [initializing, setInitializing] = useState(true);

    useEffect(() => {
        const bootstrap = async () => {
            if (!readToken()) {
                setInitializing(false);
                return;
            }
            try {
                const profile = await communityApi.me();
                setUser(profile);
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
        async login(payload) {
            const response = await communityApi.login(payload);
            persistToken(response.token);
            const me = await communityApi.me();
            setUser(me);
            return me;
        },
        async register(payload) {
            const response = await communityApi.register(payload);
            persistToken(response.token);
            const me = await communityApi.me();
            setUser(me);
            return me;
        },
        async logout() {
            try {
                await communityApi.logout();
            } finally {
                persistToken(null);
                setUser(null);
            }
        },
        async refreshUser() {
            const me = await communityApi.me();
            setUser(me);
            return me;
        },
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

