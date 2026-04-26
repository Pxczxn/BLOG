/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Helmet } from 'react-helmet-async';
import ReactMarkdown from 'react-markdown';
import { ArrowLeft, Clock, Eye, Folder, Hash, ArrowRight } from 'lucide-react';
import request from '../../api/request';
import CommentSection from '../../components/CommentSection';
import { normalizeArticle } from '../../api/article';

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${months[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`;
};

const DetailPage = () => {
    const { slug } = useParams();
    const [data, setData] = useState(null);
    const [navigation, setNavigation] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchDetail = async () => {
            setLoading(true);
            setError(null);
            try {
                const resp = await request.get(`/api/public/articles/${slug}`);
                setData(normalizeArticle(resp.data || resp));
                request.post(`/api/public/articles/${slug}/view`).catch(() => { });
            } catch (err) {
                setError(err.message || '获取文章详情失败');
            } finally {
                setLoading(false);
            }
        };

        const fetchNavigation = async () => {
            try {
                const resp = await request.get(`/api/public/articles/${slug}/navigation`);
                setNavigation(resp.data || resp);
            } catch (err) {
                console.error('Failed to fetch navigation:', err);
            }
        };

        if (slug) {
            fetchDetail();
            fetchNavigation();
        }
    }, [slug]);

    if (loading) {
        return (
            <div className="flex justify-center items-center py-32">
                <div className="w-6 h-6 border-2 border-white/10 border-t-white rounded-full animate-spin"></div>
            </div>
        );
    }

    if (error || !data) {
        return (
            <div className="text-center py-32 max-w-2xl mx-auto font-sans text-slate-500">
                <p className="text-lg mb-6">{error || '404 - 文章不存在或已被删除'}</p>
                <Link to="/" className="inline-flex items-center gap-2 text-sm font-medium text-slate-300 hover:text-white transition-colors">
                    <ArrowLeft className="w-4 h-4" /> 返回首页
                </Link>
            </div>
        );
    }

    return (
        <article className="animate-in fade-in slide-in-from-bottom-8 duration-1000 pb-16 font-sans max-w-[760px] mx-auto">
            <Helmet>
                <title>{data.title} - BLOG · 破星辰只寻你</title>
                <meta name="description" content={data.summary || data.title} />
                <meta property="og:title" content={`${data.title} - BLOG · 破星辰只寻你`} />
                <meta property="og:description" content={data.summary || data.title} />
                {data.coverImage && <meta property="og:image" content={data.coverImage} />}
            </Helmet>

            <Link to="/" className="inline-flex items-center gap-2 text-sm font-medium text-slate-500 hover:text-slate-300 mb-12 group transition-colors">
                <ArrowLeft className="w-4 h-4 group-hover:-translate-x-1 transition-transform" />
                全部文章
            </Link>

            <header className="mb-10 text-center">
                <h1 className="text-3xl sm:text-4xl md:text-5xl font-bold text-slate-100 tracking-tight leading-snug mb-8 font-serif">
                    {data.title}
                </h1>

                <div className="flex flex-wrap items-center justify-center gap-4 text-sm text-slate-500 font-medium tracking-wide">
                    <time className="flex items-center gap-1.5"><Clock className="w-4 h-4" /> {formatDate(data.createdAt)}</time>
                    <span className="w-1 h-1 rounded-full bg-slate-700"></span>
                    <span className="flex items-center gap-1.5 text-indigo-400">
                        <Folder className="w-4 h-4" /> {data.category?.name || '默认分类'}
                    </span>
                    <span className="w-1 h-1 rounded-full bg-slate-700"></span>
                    <span className="flex items-center gap-1.5 hover:text-slate-300 transition-colors cursor-help" title="阅读量">
                        <Eye className="w-4 h-4" /> {data.viewCount || 0}
                    </span>
                </div>
            </header>

            {data.coverImage && (
                <div className="w-full aspect-[21/9] bg-white/5 rounded-3xl overflow-hidden mb-14 drop-shadow-2xl">
                    <img src={data.coverImage} alt={data.title} className="w-full h-full object-cover" />
                </div>
            )}

            <div className="prose prose-invert prose-lg max-w-none
                prose-headings:font-bold prose-headings:tracking-tight
                prose-h2:mt-16 prose-h2:mb-8 prose-h2:pb-2 prose-h2:border-b prose-h2:border-white/5
                prose-h3:mt-12 prose-h3:mb-6
                prose-p:leading-[1.8] prose-p:font-light prose-p:mb-8
                prose-a:text-indigo-400 prose-a:no-underline hover:prose-a:underline
                prose-pre:bg-[#09090b] prose-pre:border prose-pre:border-white/5 prose-pre:rounded-xl prose-pre:my-8
                prose-code:text-indigo-300 prose-code:bg-indigo-500/10 prose-code:px-1.5 prose-code:py-0.5 prose-code:rounded-md prose-code:font-normal
                prose-img:rounded-2xl prose-img:border prose-img:border-white/5 prose-img:my-10
                prose-blockquote:border-l-indigo-500 prose-blockquote:bg-indigo-500/5 prose-blockquote:py-2 prose-blockquote:px-5 prose-blockquote:rounded-r-xl prose-blockquote:not-italic prose-blockquote:my-8
                prose-li:leading-[1.8] prose-li:marker:text-slate-600
            ">
                <ReactMarkdown>{data.content || ''}</ReactMarkdown>
            </div>

            {data.tags && data.tags.length > 0 && (
                <div className="mt-16 pt-8 border-t border-white/5">
                    <div className="flex flex-wrap gap-2">
                        {data.tags.map((tag) => (
                            <Link key={tag.id} to={`/tag/${tag.slug || tag.id}`} className="flex items-center gap-1 px-3 py-1.5 bg-white/5 text-slate-400 hover:bg-white/10 hover:text-slate-200 rounded-lg text-sm font-medium transition-colors">
                                <Hash className="w-3.5 h-3.5" /> {tag.name}
                            </Link>
                        ))}
                    </div>
                </div>
            )}

            <div className="mt-12 grid grid-cols-1 sm:grid-cols-2 gap-4">
                {navigation?.previous ? (
                    <Link to={`/post/${navigation.previous.slug}`} className="group flex flex-col p-6 rounded-2xl bg-white/[0.02] border border-white/5 hover:bg-white/5 transition-colors">
                        <span className="flex items-center gap-2 text-xs text-slate-500 uppercase tracking-wider mb-2 font-medium">
                            <ArrowLeft className="w-3.5 h-3.5 group-hover:-translate-x-1 transition-transform" /> 上一篇
                        </span>
                        <span className="font-medium text-slate-300 group-hover:text-white line-clamp-2">
                            {navigation.previous.title}
                        </span>
                    </Link>
                ) : (
                    <div className="flex flex-col p-6 rounded-2xl bg-white/[0.02] border border-white/5 opacity-50">
                        <span className="flex items-center gap-2 text-xs text-slate-500 uppercase tracking-wider mb-2 font-medium">
                            <ArrowLeft className="w-3.5 h-3.5" /> 上一篇
                        </span>
                        <span className="font-medium text-slate-500">已经是第一篇</span>
                    </div>
                )}

                {navigation?.next ? (
                    <Link to={`/post/${navigation.next.slug}`} className="group flex flex-col items-end p-6 rounded-2xl bg-white/[0.02] border border-white/5 hover:bg-white/5 text-right transition-colors">
                        <span className="flex items-center gap-2 text-xs text-slate-500 uppercase tracking-wider mb-2 font-medium">
                            下一篇 <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />
                        </span>
                        <span className="font-medium text-slate-300 group-hover:text-white line-clamp-2">
                            {navigation.next.title}
                        </span>
                    </Link>
                ) : (
                    <div className="flex flex-col items-end p-6 rounded-2xl bg-white/[0.02] border border-white/5 opacity-50 text-right">
                        <span className="flex items-center gap-2 text-xs text-slate-500 uppercase tracking-wider mb-2 font-medium">
                            下一篇 <ArrowRight className="w-3.5 h-3.5" />
                        </span>
                        <span className="font-medium text-slate-500">已经是最后一篇</span>
                    </div>
                )}
            </div>

            <div className="mt-20 pt-10 border-t border-white/5 flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <div className="w-12 h-12 rounded-full bg-slate-800 border border-white/10 flex items-center justify-center overflow-hidden">
                        <img src="/assets/avatar.png" alt="Avatar" className="w-full h-full object-cover" />
                    </div>
                    <div>
                        <p className="text-base font-semibold text-slate-200 tracking-wide">破星辰只寻你</p>
                        <p className="text-sm text-slate-500 mt-0.5">全栈开发者与设计爱好者</p>
                    </div>
                </div>
            </div>

            <div className="mt-20">
                <CommentSection articleId={data.id} />
            </div>
        </article>
    );
};

export default DetailPage;

