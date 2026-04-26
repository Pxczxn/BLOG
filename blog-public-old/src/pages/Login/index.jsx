/*
 * 功能：前端模块逻辑。
 */
import React, { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import { LogIn } from 'lucide-react';
import { useAuth } from '../../auth/AuthContext';

const LoginPage = () => {
    const { user, login, initializing } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [form, setForm] = useState({ identifier: '', password: '' });
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    if (!initializing && user) {
        return <Navigate to={location.state?.from || '/me'} replace />;
    }

    const handleSubmit = async (event) => {
        event.preventDefault();
        setSubmitting(true);
        setError('');
        try {
            await login(form);
            navigate(location.state?.from || '/me', { replace: true });
        } catch (err) {
            setError(err.message || '鐧诲綍澶辫触锛岃绋嶅悗閲嶈瘯銆?);
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <section className="max-w-xl mx-auto">
            <Helmet>
                <title>绀惧尯鐧诲綍 - BLOG 路 鐮存槦杈板彧瀵讳綘</title>
            </Helmet>
            <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-8 sm:p-10 shadow-2xl">
                <div className="mb-8">
                    <p className="text-xs uppercase tracking-[0.3em] text-cyan-400/80">Community</p>
                    <h1 className="mt-3 text-3xl font-bold text-slate-100">鐧诲綍浣犵殑绀惧尯韬唤</h1>
                    <p className="mt-3 text-sm leading-7 text-slate-400">鐧诲綍鍚庝綘鍙互绠＄悊璧勬枡銆佹嫢鏈夊叕寮€涓婚〉锛屽苟鍦ㄨ瘎璁哄尯鐣欎笅甯﹁韩浠界殑鐣欒█銆?/p>
                </div>
                <form onSubmit={handleSubmit} className="space-y-5">
                    <input
                        type="text"
                        placeholder="鐢ㄦ埛鍚嶆垨閭"
                        value={form.identifier}
                        onChange={(event) => setForm({ ...form, identifier: event.target.value })}
                        className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-cyan-400/50"
                        required
                    />
                    <input
                        type="password"
                        placeholder="瀵嗙爜"
                        value={form.password}
                        onChange={(event) => setForm({ ...form, password: event.target.value })}
                        className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-cyan-400/50"
                        required
                    />
                    <button
                        type="submit"
                        disabled={submitting}
                        className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-cyan-500 px-5 py-3.5 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:opacity-60"
                    >
                        <LogIn className="h-4 w-4" />
                        {submitting ? '鐧诲綍涓?..' : '鐧诲綍'}
                    </button>
                </form>
                {error && <p className="mt-4 text-sm text-rose-300">{error}</p>}
                <p className="mt-8 text-sm text-slate-400">
                    杩樻病鏈夎处鍙凤紵 <Link to="/register" className="text-cyan-300 hover:text-cyan-200">鍘绘敞鍐?/Link>
                </p>
            </div>
        </section>
    );
};

export default LoginPage;

