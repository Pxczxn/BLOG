import React, { useState, useEffect } from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import { Github, Mail, ArrowUpRight, BookOpen, Hash, Folder } from 'lucide-react';
import request from '../api/request';

const Layout = () => {
    const [stats, setStats] = useState({ articles: 0, categories: 0, tags: 0 });

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

    return (
        <div className="min-h-screen flex flex-col md:flex-row font-sans relative z-10 mx-auto max-w-7xl text-slate-300">
            <Helmet>
                <title>BLOG - 关于我</title>
                <meta name="description" content="全栈开发者与设计爱好者，用心构建充满质感的数字体验。" />
            </Helmet>

            <aside className="w-full md:w-[320px] lg:w-[380px] md:h-screen md:sticky top-0 flex flex-col p-8 md:p-12 lg:p-16 border-b md:border-b-0 md:border-r border-white/5 bg-[#09090b] overflow-y-auto">
                <div className="flex-1">
                    <NavLink to="/" className="inline-block group mb-8">
                        <div className="w-16 h-16 rounded-2xl bg-white/5 border border-white/10 flex items-center justify-center font-bold text-xl mb-6 group-hover:scale-105 transition-transform overflow-hidden shadow-lg">
                            <img src="/assets/avatar.png" alt="Avatar" className="w-full h-full object-cover" />
                        </div>
                        <h1 className="text-2xl font-bold tracking-tight text-slate-100 font-serif">破星辰只寻你</h1>
                        <p className="text-slate-500 mt-2 text-sm leading-relaxed font-medium">
                            全栈开发者与设计爱好者。<br />
                            用心构建充满质感的数字体验。
                        </p>
                        <div className="mt-4 px-3 py-1.5 bg-blue-500/10 border border-blue-500/20 rounded-full inline-flex items-center gap-2">
                            <span className="w-2 h-2 rounded-full bg-blue-500 animate-pulse"></span>
                            <span className="text-xs text-blue-400 font-medium">正在构建自己的数字世界</span>
                        </div>
                    </NavLink>

                    <div className="flex gap-6 mb-12 pb-8 border-b border-white/5 justify-start">
                        <div className="flex flex-col">
                            <span className="text-2xl font-bold text-slate-200">{stats.articles}</span>
                            <span className="text-xs text-slate-500 flex items-center gap-1 mt-1"><BookOpen className="w-3 h-3" /> 文章</span>
                        </div>
                        <div className="flex flex-col">
                            <span className="text-2xl font-bold text-slate-200">{stats.categories}</span>
                            <span className="text-xs text-slate-500 flex items-center gap-1 mt-1"><Folder className="w-3 h-3" /> 分类</span>
                        </div>
                        <div className="flex flex-col">
                            <span className="text-2xl font-bold text-slate-200">{stats.tags}</span>
                            <span className="text-xs text-slate-500 flex items-center gap-1 mt-1"><Hash className="w-3 h-3" /> 标签</span>
                        </div>
                    </div>

                    <nav className="flex flex-col gap-4 mt-12">
                        <NavLink to="/" className="flex items-center gap-3 text-sm font-medium text-slate-500 hover:text-white hover:translate-x-1 transition-all">
                            <span className="w-4 border-t border-current opacity-50"></span>
                            全部文章
                        </NavLink>
                        <NavLink to="/about" className="flex items-center gap-3 text-sm font-medium text-slate-500 hover:text-white hover:translate-x-1 transition-all">
                            <span className="w-4 border-t border-current opacity-50"></span>
                            关于作者
                        </NavLink>
                    </nav>
                </div>

                <div className="mt-16 md:mt-0 flex items-center gap-5">
                    <a href="https://github.com/Pxczxn" target="_blank" rel="noreferrer" className="text-slate-400 hover:text-white transition-colors" aria-label="GitHub">
                        <Github className="w-5 h-5" />
                    </a>
                    <a href="mailto:Pxczxn@163.com" className="text-slate-400 hover:text-white transition-colors" aria-label="Email">
                        <Mail className="w-5 h-5" />
                    </a>
                </div>
            </aside>

            <main className="flex-1 min-w-0 bg-[#09090b]/40 rounded-l-3xl border-l border-t md:ml-4 md:mt-4 border-white/5 shadow-2xl backdrop-blur-md" style={{ minHeight: 'calc(100vh - 16px)' }}>
                <div className="max-w-3xl mx-auto px-6 py-12 md:py-20 lg:px-12">
                    <Outlet />
                </div>

                <footer className="max-w-3xl mx-auto px-6 lg:px-12 py-8 border-t border-white/5 text-xs text-slate-500 flex justify-between items-center">
                    <p>&copy; {new Date().getFullYear()} 破星辰只寻你. 保留所有权利。</p>
                    <a href="https://github.com/YourRepo" className="flex items-center gap-1 hover:text-slate-300 transition-colors">
                        查看源码 <ArrowUpRight className="w-3 h-3" />
                    </a>
                </footer>
            </main>
        </div>
    );
};

export default Layout;
