/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { Link } from 'react-router-dom';
import { ArrowRight, Layers3, PenSquare } from 'lucide-react';
import { communityApi } from '../../api/community';
import { useAuth } from '../../auth/AuthContext';

const CommunityHomePage = () => {
    const { user } = useAuth();
    const [nodes, setNodes] = useState([]);
    const [posts, setPosts] = useState({ items: [], total: 0 });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const [nodeData, postData] = await Promise.all([
                    communityApi.listNodes(),
                    communityApi.listPosts({ page: 1, size: 12 }),
                ]);
                setNodes(Array.isArray(nodeData) ? nodeData : []);
                setPosts(postData || { items: [], total: 0 });
            } catch (error) {
                console.error('鍔犺浇绀惧尯鏁版嵁澶辫触', error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    return (
        <section className="space-y-12">
            <Helmet>
                <title>绀惧尯骞垮満 - BLOG</title>
            </Helmet>

            <div className="rounded-[2rem] border border-white/5 bg-gradient-to-br from-cyan-500/10 via-transparent to-emerald-500/10 p-8 shadow-2xl sm:p-10">
                <p className="text-xs uppercase tracking-[0.35em] text-cyan-300/80">绀惧尯</p>
                <h1 className="mt-4 text-4xl font-bold tracking-tight text-slate-100 sm:text-5xl">鎶娾€滃洿瑙傚崥瀹⑩€濆彉鎴愨€滃弬涓庣ぞ鍖衡€濄€?/h1>
                <p className="mt-5 max-w-2xl text-sm leading-7 text-slate-400 sm:text-base">
                    杩欓噷宸茬粡鏈夎妭鐐广€佸笘瀛愬拰鐢ㄦ埛涓婚〉銆備綘鍙互鍦ㄤ笉鍚屼富棰樹笅鍙戝笘浜ゆ祦锛屼篃鍙互椤虹潃浣滆€呬富椤电户缁彂鐜版洿澶氱ぞ鍖哄唴瀹广€?                </p>
                <div className="mt-7 flex flex-wrap gap-3">
                    <Link to={user ? '/community/new' : '/login'} className="inline-flex items-center gap-2 rounded-2xl bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 hover:bg-cyan-400">
                        <PenSquare className="h-4 w-4" /> {user ? '鍐欎竴绡囧笘瀛? : '鐧诲綍鍚庡彂甯?}
                    </Link>
                    {user ? (
                        <Link to="/me" className="inline-flex items-center gap-2 rounded-2xl border border-white/10 px-5 py-3 text-sm text-slate-300 hover:bg-white/5">
                            鍘绘垜鐨勪富椤?ArrowRight className="h-4 w-4" />
                        </Link>
                    ) : (
                        <Link to="/register" className="inline-flex items-center gap-2 rounded-2xl border border-white/10 px-5 py-3 text-sm text-slate-300 hover:bg-white/5">
                            娉ㄥ唽璐﹀彿<ArrowRight className="h-4 w-4" />
                        </Link>
                    )}
                </div>
            </div>

            <div className="space-y-5">
                <div className="flex items-center justify-between">
                    <h2 className="text-2xl font-semibold text-slate-100">绀惧尯鑺傜偣</h2>
                    <span className="text-sm text-slate-500">{nodes.length} 涓妭鐐?/span>
                </div>
                <div className="grid gap-4 md:grid-cols-3">
                    {nodes.map((node) => (
                        <Link key={node.id} to={`/community/node/${node.slug}`} className="rounded-3xl border border-white/5 bg-white/[0.03] p-6 transition hover:-translate-y-1 hover:bg-white/[0.05]">
                            <div className="inline-flex rounded-2xl border border-cyan-500/20 bg-cyan-500/10 p-3 text-cyan-300">
                                <Layers3 className="h-5 w-5" />
                            </div>
                            <h3 className="mt-5 text-xl font-semibold text-slate-100">{node.name}</h3>
                            <p className="mt-3 text-sm leading-6 text-slate-400">{node.description}</p>
                            <p className="mt-5 text-xs uppercase tracking-[0.2em] text-slate-500">{node.postCount} 绡囧笘瀛?/p>
                        </Link>
                    ))}
                </div>
            </div>

            <div className="space-y-5">
                <div className="flex items-center justify-between">
                    <h2 className="text-2xl font-semibold text-slate-100">鏈€鏂板笘瀛?/h2>
                    <span className="text-sm text-slate-500">{posts.total || 0} 绡囧叕寮€鍐呭</span>
                </div>
                {loading ? (
                    <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-10 text-center text-slate-500">鍔犺浇涓?..</div>
                ) : posts.items?.length ? (
                    <div className="space-y-4">
                        {posts.items.map((post) => (
                            <article key={post.id} className="rounded-3xl border border-white/5 bg-white/[0.03] p-6 transition hover:bg-white/[0.05]">
                                <div className="flex flex-wrap items-center gap-3 text-xs uppercase tracking-[0.18em] text-slate-500">
                                    <Link to={`/community/node/${post.node.slug}`} className="text-cyan-300 hover:text-cyan-200">{post.node.name}</Link>
                                    <span>|</span>
                                    <Link to={`/u/${post.author.username}`} className="hover:text-slate-300">@{post.author.username}</Link>
                                </div>
                                <Link to={`/community/post/${post.slug}`} className="mt-3 block text-2xl font-semibold text-slate-100 hover:text-cyan-300">
                                    {post.title}
                                </Link>
                                <p className="mt-3 text-sm leading-7 text-slate-400">{post.summary || '杩欑瘒甯栧瓙杩樻病鏈夊啓鎽樿銆?}</p>
                            </article>
                        ))}
                    </div>
                ) : (
                    <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-10 text-center text-slate-500">绀惧尯閲岃繕娌℃湁鍏紑甯栧瓙锛屾潵鍙戠涓€绡囧惂銆?/div>
                )}
            </div>
        </section>
    );
};

export default CommunityHomePage;

