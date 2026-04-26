/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useState } from 'react';
import { Link, NavLink, Outlet } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import { Github, Mail, ArrowUpRight, BookOpen, Hash, Folder, LogOut, UserCircle2, MessageCircleMore, PenSquare } from 'lucide-react';
import request, { getStaticUrl } from '../api/request';
import { useAuth } from '../auth/AuthContext';

const Layout = () => {
    const [stats, setStats] = useState({ articles: 0, categories: 0, tags: 0 });
    const { user, logout } = useAuth();

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const [artRes, catRes, tagRes] = await Promise.all([
                    request.get('/api/public/articles?page=1&size=1'),
                    request.get('/api/public/categories'),
                    request.get('/api/public/tags'),
                ]);
                const articlesData = artRes.data || artRes;
                const catData = catRes.data || catRes;
                const tagData = tagRes.data || tagRes;
                setStats({
                    articles: articlesData.total || 0,
                    categories: Array.isArray(catData) ? catData.length : (catData.items?.length || 0),
                    tags: Array.isArray(tagData) ? tagData.length : (tagData.items?.length || 0),
                });
            } catch (error) {
                console.error('Failed to fetch stats', error);
            }
        };

        fetchStats();
    }, []);

    const navLinkClass = ({ isActive }) =>
        `px-4 py-2 text-sm font-medium transition-colors hover:text-white relative ${
            isActive ? 'text-white font-semibold after:absolute after:bottom-[-20px] after:left-0 after:w-full after:border-b-2 after:border-white' : 'text-slate-400'
        }`;

    return (
        <div className="relative z-10 flex min-h-screen flex-col font-sans text-slate-300">
            <Helmet>
                <title>BLOG - 鐮存槦杈板彧瀵讳綘</title>
                <meta name="description" content="鍏ㄦ爤寮€鍙戣€呬笌璁捐鐖卞ソ鑰咃紝鐢ㄥ績鏋勫缓鍏呮弧璐ㄦ劅鐨勬暟瀛椾綋楠屻€? />
            </Helmet>

            <div className="fixed inset-0 z-0 pointer-events-none overflow-hidden">
                <div className="star-layer-1"></div>
                <div className="star-layer-2"></div>
                <div className="star-layer-3"></div>
            </div>

            {/* Top Navigation */}
            <header className="sticky top-0 z-50 w-full border-b border-white/5 bg-[#020617]/70 backdrop-blur-xl">
                <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-6">
                    {/* Brand */}
                    <Link to="/" className="flex items-center gap-3 group">
                        <div className="h-8 w-8 overflow-hidden rounded-lg border border-white/10">
                            <img src="/assets/avatar.png" alt="Avatar" className="h-full w-full object-cover transition-transform group-hover:scale-110" />
                        </div>
                        <span className="font-serif text-lg font-bold tracking-tight text-slate-100">鐮存槦杈板彧瀵讳綘</span>
                    </Link>

                    {/* Central Nav */}
                    <nav className="hidden md:flex items-center gap-2">
                        <NavLink to="/" className={navLinkClass} end>鍏ㄩ儴鏂囩珷</NavLink>
                        <NavLink to="/community" className={navLinkClass}>绀惧尯骞垮満</NavLink>
                        <NavLink to="/about" className={navLinkClass}>鍏充簬浣滆€?/NavLink>
                    </nav>

                    {/* Right Actions */}
                    <div className="flex items-center gap-4">
                        <div className="hidden items-center gap-4 border-r border-white/10 pr-4 md:flex">
                            <a href="https://github.com/Pxczxn" target="_blank" rel="noreferrer" className="text-slate-400 transition-colors hover:text-white">
                                <Github className="h-4 w-4" />
                            </a>
                            <a href="mailto:Pxczxn@163.com" className="text-slate-400 transition-colors hover:text-white">
                                <Mail className="h-4 w-4" />
                            </a>
                        </div>

                        {user ? (
                            <div className="flex items-center gap-4">
                                <Link to="/community/new" className="hidden lg:flex items-center gap-2 rounded-full bg-cyan-500/10 border border-cyan-500/20 px-3 py-1.5 text-xs font-semibold text-cyan-400 hover:bg-cyan-500/20">
                                    <PenSquare className="h-3 w-3" /> 鍙戝笘
                                </Link>
                                <div className="group relative">
                                    <Link to={`/u/${user.username}`}>
                                        <div className="relative flex h-8 w-8 items-center justify-center overflow-hidden rounded-full border border-white/20 bg-slate-800 text-xs font-bold text-white transition-all group-hover:border-white/50">
                                            <span>{(user.displayName || user.username || '?').charAt(0).toUpperCase()}</span>
                                            <img
                                                src={getStaticUrl(user.avatar) || `https://api.dicebear.com/7.x/identicon/svg?seed=${user.username}&backgroundColor=1e1e2e`}
                                                alt={user.displayName || user.username}
                                                className="absolute inset-0 h-full w-full object-cover transition-opacity"
                                                onError={(e) => { e.target.style.opacity = '0'; }}
                                            />
                                        </div>
                                    </Link>
                                    {/* Dropdown Menu - Added pt-2 and top-full to bridge the gap */}
                                    <div className="absolute right-0 top-full hidden pt-2 group-hover:flex">
                                        <div className="flex w-32 flex-col overflow-hidden rounded-xl border border-white/10 bg-[#0f172a] shadow-2xl">
                                            <Link to="/me" className="px-4 py-3 text-sm text-slate-300 hover:bg-white/5">鎴戠殑璧勬枡</Link>
                                            <button onClick={logout} className="border-t border-white/5 px-4 py-3 text-left text-sm text-rose-400 hover:bg-white/5">閫€鍑虹櫥褰?/button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="flex items-center gap-3">
                                <Link to="/login" className="text-sm font-semibold text-slate-300 hover:text-white">鐧诲綍</Link>
                                <Link to="/register" className="rounded-full bg-white px-4 py-1.5 text-sm font-semibold text-slate-900 transition-all hover:bg-slate-200">娉ㄥ唽</Link>
                            </div>
                        )}
                    </div>
                </div>
            </header>

            {/* Stats / Secondary Banner */}
            <div className="w-full border-b border-light border-white/5 bg-white/[0.01]">
                <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-2">
                    <div className="flex items-center gap-4 sm:gap-6">
                        <span className="flex items-center gap-1.5 text-xs text-slate-400"><BookOpen className="h-3 w-3" /> {stats.articles} 绡囨枃绔?/span>
                        <span className="flex items-center gap-1.5 text-xs text-slate-400"><Folder className="h-3 w-3" /> {stats.categories} 涓垎绫?/span>
                        <span className="flex items-center gap-1.5 text-xs text-slate-400"><Hash className="h-3 w-3" /> {stats.tags} 涓爣绛?/span>
                    </div>
                    <div className="hidden lg:flex items-center gap-2 px-3 py-1">
                        <span className="text-[10px] uppercase tracking-[0.2em] text-blue-400/80">姝ｅ湪鏋勫缓鑷繁鐨勬暟瀛椾笘鐣?/span>
                        <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-blue-500"></span>
                    </div>
                </div>
            </div>

            {/* Main Content Area */}
            <main className="mx-auto w-full max-w-5xl flex-1 px-6 py-12 md:py-16">
                {/* Fallback mobile nav inside main, since top-nav hides items on mobile */}
                <div className="mb-8 flex flex-wrap gap-3 md:hidden">
                    <NavLink to="/" className="rounded-full border border-white/10 bg-white/5 px-4 py-1.5 text-xs text-slate-300">鍏ㄩ儴鏂囩珷</NavLink>
                    <NavLink to="/community" className="rounded-full border border-white/10 bg-white/5 px-4 py-1.5 text-xs text-slate-300">绀惧尯骞垮満</NavLink>
                    <NavLink to="/about" className="rounded-full border border-white/10 bg-white/5 px-4 py-1.5 text-xs text-slate-300">鍏充簬浣滆€?/NavLink>
                </div>
                
                <Outlet />
            </main>

            {/* Footer */}
            <footer className="mt-auto border-t border-white/5 bg-[#020617]/50 backdrop-blur-xl">
                <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-8 text-xs text-slate-500">
                    <div className="flex flex-col gap-1 sm:flex-row sm:items-center sm:gap-4">
                        <p>&copy; {new Date().getFullYear()} 鐮存槦杈板彧瀵讳綘.</p>
                        <span className="hidden w-2 border-t border-slate-700 sm:inline-block"></span>
                        <p>鐢ㄥ績鏋勫缓鍏呮弧璐ㄦ劅鐨勬暟瀛椾綋楠屻€?/p>
                    </div>
                    <div className="flex gap-4">
                        <a href="https://github.com/Pxczxn" target="_blank" rel="noreferrer" className="flex items-center gap-1 transition-colors hover:text-slate-300">
                            婧愮爜 <ArrowUpRight className="h-3 w-3" />
                        </a>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Layout;

