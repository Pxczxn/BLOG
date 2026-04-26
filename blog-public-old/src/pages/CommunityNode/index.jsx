/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { Link, useParams } from 'react-router-dom';
import { communityApi } from '../../api/community';

const CommunityNodePage = () => {
    const { slug } = useParams();
    const [node, setNode] = useState(null);
    const [posts, setPosts] = useState({ items: [], total: 0, page: 1, size: 10 });
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(true);
    const pageSize = 10;

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const [nodeData, postData] = await Promise.all([
                    communityApi.getNode(slug),
                    communityApi.listPosts({ page, size: pageSize, node: slug }),
                ]);
                setNode(nodeData);
                setPosts(postData || { items: [], total: 0, page, size: pageSize });
            } catch (error) {
                console.error('鍔犺浇鑺傜偣椤甸潰澶辫触', error);
                setNode(null);
                setPosts({ items: [], total: 0, page, size: pageSize });
            } finally {
                setLoading(false);
            }
        };

        if (slug) {
            fetchData();
        }
    }, [page, slug]);

    return (
        <section className="space-y-8">
            <Helmet>
                <title>{node?.name ? `${node.name} - 绀惧尯鑺傜偣` : '绀惧尯鑺傜偣'}</title>
            </Helmet>

            <div className="rounded-[2rem] border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <p className="text-xs uppercase tracking-[0.3em] text-cyan-300/80">鑺傜偣</p>
                <h1 className="mt-4 text-4xl font-bold text-slate-100">{node?.name || '绀惧尯鑺傜偣'}</h1>
                <p className="mt-4 max-w-2xl text-sm leading-7 text-slate-400">{node?.description || '杩欓噷鏄鑺傜偣涓嬬殑鍏紑甯栧瓙鍒楄〃銆?}</p>
                <p className="mt-5 text-xs uppercase tracking-[0.2em] text-slate-500">{node?.postCount || 0} 绡囧笘瀛?/p>
            </div>

            {loading ? (
                <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-10 text-center text-slate-500">鍔犺浇涓?..</div>
            ) : posts.items?.length ? (
                <>
                    <div className="space-y-4">
                        {posts.items.map((post) => (
                            <article key={post.id} className="rounded-3xl border border-white/5 bg-white/[0.03] p-6 transition hover:bg-white/[0.05]">
                                <div className="flex flex-wrap items-center gap-3 text-xs uppercase tracking-[0.18em] text-slate-500">
                                    <Link to={`/u/${post.author.username}`} className="hover:text-slate-300">@{post.author.username}</Link>
                                    <span>|</span>
                                    <span>{post.viewCount || 0} 娆℃祻瑙?/span>
                                </div>
                                <Link to={`/community/post/${post.slug}`} className="mt-3 block text-2xl font-semibold text-slate-100 hover:text-cyan-300">
                                    {post.title}
                                </Link>
                                <p className="mt-3 text-sm leading-7 text-slate-400">{post.summary || '杩欑瘒甯栧瓙杩樻病鏈夊啓鎽樿銆?}</p>
                            </article>
                        ))}
                    </div>
                    {posts.total > pageSize && (
                        <div className="flex items-center justify-between pt-2">
                            <button type="button" disabled={page === 1} onClick={() => setPage((current) => Math.max(1, current - 1))} className="rounded-2xl border border-white/10 px-4 py-2 text-sm text-slate-400 hover:bg-white/5 disabled:opacity-40">
                                涓婁竴椤?                            </button>
                            <span className="text-sm text-slate-500">{page} / {Math.ceil(posts.total / pageSize)}</span>
                            <button type="button" disabled={page >= Math.ceil(posts.total / pageSize)} onClick={() => setPage((current) => current + 1)} className="rounded-2xl border border-white/10 px-4 py-2 text-sm text-slate-400 hover:bg-white/5 disabled:opacity-40">
                                涓嬩竴椤?                            </button>
                        </div>
                    )}
                </>
            ) : (
                <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-10 text-center text-slate-500">杩欎釜鑺傜偣涓嬭繕娌℃湁鍏紑甯栧瓙銆?/div>
            )}
        </section>
    );
};

export default CommunityNodePage;

