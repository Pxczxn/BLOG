/*
 * 功能：前端模块逻辑。
 */
import React, { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import { UserPlus } from 'lucide-react';
import { useAuth } from '../../auth/AuthContext';

const RegisterPage = () => {
    const { user, register, initializing } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [form, setForm] = useState({
        username: '',
        displayName: '',
        email: '',
        password: '',
    });
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
            await register(form);
            navigate(location.state?.from || '/me', { replace: true });
        } catch (err) {
            setError(err.message || '娉ㄥ唽澶辫触锛岃绋嶅悗閲嶈瘯銆?);
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <section className="max-w-2xl mx-auto">
            <Helmet>
                <title>绀惧尯娉ㄥ唽 - BLOG 路 鐮存槦杈板彧瀵讳綘</title>
            </Helmet>
            <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-8 sm:p-10 shadow-2xl">
                <div className="mb-8">
                    <p className="text-xs uppercase tracking-[0.3em] text-emerald-400/80">Community</p>
                    <h1 className="mt-3 text-3xl font-bold text-slate-100">鍒涘缓涓€涓叕寮€韬唤</h1>
                    <p className="mt-3 text-sm leading-7 text-slate-400">娉ㄥ唽鍚庝細鑷姩鐧诲綍锛屼綘鍙互绔嬪嵆瀹屽杽璧勬枡骞舵嫢鏈夎嚜宸辩殑鍏紑涓婚〉銆?/p>
                </div>
                <form onSubmit={handleSubmit} className="grid gap-5 sm:grid-cols-2">
                    <input
                        type="text"
                        placeholder="鐢ㄦ埛鍚?
                        value={form.username}
                        onChange={(event) => setForm({ ...form, username: event.target.value })}
                        className="rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-emerald-400/50"
                        required
                    />
                    <input
                        type="text"
                        placeholder="鏄剧ず鍚嶇О"
                        value={form.displayName}
                        onChange={(event) => setForm({ ...form, displayName: event.target.value })}
                        className="rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-emerald-400/50"
                        required
                    />
                    <input
                        type="email"
                        placeholder="閭"
                        value={form.email}
                        onChange={(event) => setForm({ ...form, email: event.target.value })}
                        className="rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-emerald-400/50 sm:col-span-2"
                        required
                    />
                    <input
                        type="password"
                        placeholder="瀵嗙爜锛堣嚦灏?8 浣嶏級"
                        value={form.password}
                        onChange={(event) => setForm({ ...form, password: event.target.value })}
                        className="rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-emerald-400/50 sm:col-span-2"
                        required
                    />
                    <button
                        type="submit"
                        disabled={submitting}
                        className="inline-flex items-center justify-center gap-2 rounded-2xl bg-emerald-500 px-5 py-3.5 text-sm font-semibold text-slate-950 transition hover:bg-emerald-400 disabled:opacity-60 sm:col-span-2"
                    >
                        <UserPlus className="h-4 w-4" />
                        {submitting ? '娉ㄥ唽涓?..' : '娉ㄥ唽骞惰繘鍏ョぞ鍖?}
                    </button>
                </form>
                {error && <p className="mt-4 text-sm text-rose-300">{error}</p>}
                <p className="mt-8 text-sm text-slate-400">
                    宸茬粡鏈夎处鍙凤紵 <Link to="/login" className="text-emerald-300 hover:text-emerald-200">鍘荤櫥褰?/Link>
                </p>
            </div>
        </section>
    );
};

export default RegisterPage;

