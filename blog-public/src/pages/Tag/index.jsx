import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import { ArrowRight, NotebookPen, Eye, Hash, ArrowLeft } from 'lucide-react';
import request from '../../api/request';
import { normalizeArticlePage } from '../../api/article';

const TagPage = () => {
    const { slug } = useParams();
    const [data, setData] = useState({ items: [], total: 0 });
    const [tagName, setTagName] = useState('加载中...');
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);

    useEffect(() => {
        const fetchTagInfo = async () => {
            try {
                const resp = await request.get('/api/public/tags');
                const tagItems = Array.isArray(resp.data) ? resp.data : (resp.data?.items || []);
                const currentTag = tagItems.find((t) => String(t.id) === String(slug) || t.slug === slug);
                setTagName(currentTag?.name || `标签 ${slug}`);
            } catch (error) {
                console.error('Failed to fetch tag info', error);
            }
        };

        const fetchArticles = async (currentPage) => {
            setLoading(true);
            try {
                const resp = await request.get('/api/public/articles', { params: { page: currentPage, size: 10, tagId: slug } });
                setData(normalizeArticlePage(resp));
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchTagInfo();
        fetchArticles(page);
    }, [slug, page]);

    return (
        <div className="animate-in fade-in slide-in-from-bottom-8 duration-1000 font-sans">
            <Helmet>
                <title>{tagName} - BLOG | 标签</title>
                <meta name="description" content={`聚合标签 ${tagName} 下的所有文章。`} />
            </Helmet>

            <Link to="/" className="inline-flex items-center gap-2 text-sm font-medium text-slate-500 hover:text-slate-300 mb-10 group transition-colors">
                <ArrowLeft className="w-4 h-4 group-hover:-translate-x-1" /> 返回全部文章
            </Link>

            <div className="mb-12 md:mb-16 text-center border-b border-white/5 pb-12">
                <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-fuchsia-500/10 border border-fuchsia-500/20 mb-6 text-fuchsia-400">
                    <Hash className="w-8 h-8" />
                </div>
                <h1 className="text-4xl md:text-5xl font-bold tracking-tight text-slate-100 mb-6 font-serif"># {tagName}</h1>
                <p className="text-lg text-slate-400 leading-relaxed font-light">
                    共收录 <span className="font-semibold text-fuchsia-400 mx-1">{data.total || 0}</span> 篇文章
                </p>
            </div>

            <div className="space-y-6">
                {loading ? (
                    <><div className="p-8 rounded-2xl animate-pulse border border-white/5 bg-white/[0.02]" /></>
                ) : data.items?.length > 0 ? (
                    data.items.map((article) => {
                        const hasCover = !!article.coverImage;
                        return (
                            <article key={article.id} className="group relative flex flex-col sm:flex-row gap-6 p-5 md:p-6 rounded-2xl bg-white/[0.02] border border-white/5 hover:bg-white/[0.04] hover:-translate-y-1 transition-all">
                                {hasCover ? (
                                    <div className="w-full sm:w-[280px] aspect-[16/9] flex-shrink-0 overflow-hidden rounded-xl bg-slate-800 relative">
                                        <div className="absolute inset-0 bg-cover bg-center transition-transform duration-700 group-hover:scale-105" style={{ backgroundImage: `url(${article.coverImage})` }}></div>
                                    </div>
                                ) : (
                                    <div className="w-full sm:w-[280px] aspect-[16/9] flex-shrink-0 overflow-hidden rounded-xl bg-gradient-to-br from-slate-800 to-slate-900 border border-white/5 flex items-center justify-center">
                                        <NotebookPen className="w-12 h-12 text-slate-600 opacity-50" />
                                    </div>
                                )}
                                <div className="flex-1 flex flex-col justify-center">
                                    <Link to={`/post/${article.slug}`} className="absolute inset-0 z-10"><span className="sr-only">View Article</span></Link>
                                    <div className="flex flex-wrap items-center gap-3 text-xs text-slate-500 mb-3">
                                        <span className="px-2.5 py-1 rounded bg-indigo-500/10 text-indigo-300 border border-indigo-500/20">{article.category?.name || '默认分类'}</span>
                                        <span className="flex items-center gap-1.5"><Eye className="w-3.5 h-3.5" />{article.viewCount || 0} 阅读</span>
                                    </div>
                                    <h2 className="text-xl md:text-2xl font-semibold text-slate-200 group-hover:text-indigo-300 mb-4 leading-snug">{article.title}</h2>
                                    <p className="text-slate-400 leading-relaxed font-light line-clamp-2 md:line-clamp-3 mb-6">{article.summary || '暂无摘要'}</p>
                                    <div className="mt-auto flex items-center text-sm font-medium text-indigo-400/80 group-hover:text-indigo-400 relative z-20">
                                        阅读全文 <ArrowRight className="w-4 h-4 ml-1.5 transform group-hover:translate-x-1" />
                                    </div>
                                </div>
                            </article>
                        );
                    })
                ) : (
                    <div className="py-24 text-center text-slate-500 flex flex-col items-center bg-white/[0.02] border border-white/5 rounded-2xl">
                        <NotebookPen className="w-12 h-12 mb-4 opacity-20" />
                        <p className="text-lg">该标签下暂无文章发布</p>
                    </div>
                )}
            </div>

            {data.total > 0 && Math.ceil(data.total / 10) > 1 && (
                <div className="flex justify-between items-center py-12 mt-8">
                    <button onClick={() => setPage((p) => Math.max(1, p - 1))} disabled={page === 1} className="px-6 py-2 rounded-full border border-white/10 text-sm font-medium text-slate-400 hover:text-white hover:bg-white/5 disabled:opacity-30">
                        &larr; 上一页
                    </button>
                    <span className="text-sm font-medium text-slate-500">{page} / {Math.ceil(data.total / 10)}</span>
                    <button onClick={() => setPage((p) => p + 1)} disabled={page >= Math.ceil(data.total / 10)} className="px-6 py-2 rounded-full border border-white/10 text-sm font-medium text-slate-400 hover:text-white hover:bg-white/5 disabled:opacity-30">
                        下一页 &rarr;
                    </button>
                </div>
            )}
        </div>
    );
};

export default TagPage;
