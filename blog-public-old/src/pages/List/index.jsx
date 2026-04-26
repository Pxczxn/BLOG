/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import { ArrowRight, NotebookPen, Eye, Folder } from 'lucide-react';
import request from '../../api/request';
import { normalizeArticlePage } from '../../api/article';

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${months[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`;
};

const SkeletonItem = () => (
    <div className="flex flex-col sm:flex-row gap-6 p-6 md:p-8 border border-white/5 rounded-2xl bg-white/[0.02] animate-pulse">
        <div className="w-full sm:w-[280px] aspect-[16/9] bg-white/5 rounded-xl"></div>
        <div className="flex-1 flex flex-col justify-center gap-4">
            <div className="w-24 h-5 bg-white/5 rounded"></div>
            <div className="w-3/4 h-8 bg-white/10 rounded"></div>
            <div className="w-full h-4 bg-white/5 rounded mt-2"></div>
            <div className="w-2/3 h-4 bg-white/5 rounded"></div>
        </div>
    </div>
);

const ListPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const categoryId = searchParams.get('categoryId');
    const [data, setData] = useState({ items: [], total: 0 });
    const [categories, setCategories] = useState([]);
    const [stats, setStats] = useState({ categories: 0 });
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);

    useEffect(() => {
        fetchOptions();
    }, []);

    useEffect(() => {
        fetchArticles(page, categoryId);
    }, [page, categoryId]);

    const fetchOptions = async () => {
        try {
            const catRes = await request.get('/api/public/categories');
            const catItems = Array.isArray(catRes.data) ? catRes.data : (catRes.data?.items || []);
            setCategories(catItems);
            setStats({ categories: catItems.length });
        } catch (error) {
            console.error('Failed to fetch options', error);
        }
    };

    const fetchArticles = async (currentPage, currentCategory) => {
        setLoading(true);
        try {
            const params = { page: currentPage, size: 10 };
            if (currentCategory) params.categoryId = currentCategory;
            const resp = await request.get('/api/public/articles', { params });
            setData(normalizeArticlePage(resp));
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleCategoryClick = (id) => {
        setPage(1);
        setSearchParams(id ? { categoryId: id } : {});
    };

    return (
        <div className="animate-in fade-in slide-in-from-bottom-8 duration-1000 font-sans">
            <Helmet>
                <title>BLOG - 全部文章</title>
                <meta name="description" content="包含所有技术文章、学习笔记和生活感悟的归档列表。" />
            </Helmet>

            <div className="mb-10">
                <h1 className="text-4xl md:text-5xl font-bold tracking-tight text-slate-100 mb-6 font-serif">全部文章</h1>

                <div className="flex items-center gap-6 text-sm text-slate-500 mb-6 pb-6 border-b border-white/5">
                    <span className="flex items-center gap-2"><NotebookPen className="w-4 h-4" /> {data.total || 0} 篇文章</span>
                    <span className="flex items-center gap-2"><Folder className="w-4 h-4" /> {stats.categories} 个分类</span>
                    <span className="flex items-center gap-2">路 最近更新于 {formatDate(new Date())}</span>
                </div>

                <div className="flex flex-wrap gap-3 mb-8">
                    <button onClick={() => handleCategoryClick(null)} className={`px-4 py-2 rounded-full text-sm font-medium transition-all ${!categoryId ? 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/30' : 'bg-white/5 text-slate-400 hover:bg-white/10 border border-transparent'}`}>
                        全部
                    </button>
                    {categories.map((cat) => (
                        <button key={cat.id} onClick={() => handleCategoryClick(cat.id)} className={`px-4 py-2 rounded-full text-sm font-medium transition-all ${categoryId === String(cat.id) ? 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/30' : 'bg-white/5 text-slate-400 hover:bg-white/10 border border-transparent'}`}>
                            {cat.name}
                        </button>
                    ))}
                </div>
            </div>

            <div className="space-y-6">
                {loading ? (
                    <><SkeletonItem /><SkeletonItem /><SkeletonItem /></>
                ) : data.items?.length > 0 ? (
                    data.items.map((article) => {
                        const hasCover = !!article.coverImage;
                        return (
                            <article key={article.id} className="group relative flex flex-col sm:flex-row gap-6 p-5 md:p-6 rounded-2xl bg-white/[0.02] border border-white/5 hover:bg-white/[0.04] hover:-translate-y-1 transition-all duration-500 overflow-hidden">
                                {hasCover ? (
                                    <div className="w-full sm:w-[280px] aspect-[16/9] flex-shrink-0 overflow-hidden rounded-xl bg-slate-800 relative">
                                        <div className="absolute inset-0 bg-cover bg-center transition-transform duration-700 group-hover:scale-105" style={{ backgroundImage: `url(${article.coverImage})` }}></div>
                                        <div className="absolute inset-0 bg-black/20 group-hover:bg-transparent transition-colors"></div>
                                    </div>
                                ) : (
                                    <div className="w-full sm:w-[280px] aspect-[16/9] flex-shrink-0 overflow-hidden rounded-xl bg-gradient-to-br from-slate-800 to-slate-900 border border-white/5 flex items-center justify-center">
                                        <NotebookPen className="w-12 h-12 text-slate-600 opacity-50" />
                                    </div>
                                )}
                                <div className="flex-1 flex flex-col justify-center">
                                    <Link to={`/post/${article.slug}`} className="absolute inset-0 z-10"><span className="sr-only">View Article</span></Link>
                                    <div className="flex flex-wrap items-center gap-3 text-xs text-slate-500 mb-3 font-medium tracking-wide">
                                        <span className="px-2.5 py-1 rounded bg-indigo-500/10 text-indigo-300 border border-indigo-500/20">{article.category?.name || '默认分类'}</span>
                                        <time className="flex items-center gap-1.5"><Folder className="w-3.5 h-3.5" />{formatDate(article.createdAt)}</time>
                                        <span className="flex items-center gap-1.5"><Eye className="w-3.5 h-3.5" />{article.viewCount || 0} 阅读</span>
                                    </div>
                                    <h2 className="text-xl md:text-2xl font-semibold text-slate-200 group-hover:text-indigo-300 mb-4 leading-snug">
                                        {article.title}
                                    </h2>
                                    <p className="text-slate-400 leading-relaxed font-light line-clamp-2 md:line-clamp-3 mb-6">
                                        {article.summary || '暂无摘要，点击阅读全文了解更多内容。'}
                                    </p>
                                    <div className="mt-auto flex items-center text-sm font-medium text-indigo-400/80 group-hover:text-indigo-400 relative z-20">
                                        阅读全文 <ArrowRight className="w-4 h-4 ml-1.5 transform group-hover:translate-x-1 transition-transform" />
                                    </div>
                                </div>
                            </article>
                        );
                    })
                ) : (
                    <div className="py-24 text-center text-slate-500 flex flex-col items-center bg-white/[0.02] border border-white/5 rounded-2xl">
                        <NotebookPen className="w-12 h-12 mb-4 opacity-20" />
                        <p className="text-lg">该分类下暂无文章发布</p>
                    </div>
                )}
            </div>

            {data.total > 0 && Math.ceil(data.total / 10) > 1 && (
                <div className="flex justify-between items-center py-12 mt-8">
                    <button onClick={() => setPage((p) => Math.max(1, p - 1))} disabled={page === 1} className="px-6 py-2 rounded-full border border-white/10 text-sm font-medium text-slate-400 hover:text-white hover:bg-white/5 disabled:opacity-30 transition-all">
                        &larr; 上一页
                    </button>
                    <span className="text-sm font-medium text-slate-500">{page} / {Math.ceil(data.total / 10)}</span>
                    <button onClick={() => setPage((p) => p + 1)} disabled={page >= Math.ceil(data.total / 10)} className="px-6 py-2 rounded-full border border-white/10 text-sm font-medium text-slate-400 hover:text-white hover:bg-white/5 disabled:opacity-30 transition-all">
                        下一页 &rarr;
                    </button>
                </div>
            )}
        </div>
    );
};

export default ListPage;

