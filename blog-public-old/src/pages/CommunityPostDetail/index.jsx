/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import { AlertTriangle, Heart, Star, LogIn } from 'lucide-react';
import { communityApi } from '../../api/community';
import { useAuth } from '../../auth/AuthContext';
import CommunityPostCommentSection from '../../components/CommunityPostCommentSection';

const CommunityPostDetailPage = () => {
    const { slug } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [reporting, setReporting] = useState(false);
    const [liking, setLiking] = useState(false);
    const [favoriting, setFavoriting] = useState(false);

    useEffect(() => {
        const fetchDetail = async () => {
            setLoading(true);
            setError('');
            try {
                const data = await communityApi.getPost(slug);
                setPost(data);
                communityApi.incrementPostView(slug).catch(() => {});
            } catch (err) {
                setError(err.message || '鍔犺浇甯栧瓙璇︽儏澶辫触銆?);
                setPost(null);
            } finally {
                setLoading(false);
            }
        };

        if (slug) {
            fetchDetail();
        }
    }, [slug]);

    const handleLike = async () => {
        if (!post || liking) return;
        if (!user) {
            navigate('/login');
            return;
        }
        setLiking(true);
        try {
            const interaction = post.likedByMe
                ? await communityApi.unlikePost(post.id)
                : await communityApi.likePost(post.id);
            setPost((prev) => ({
                ...prev,
                likeCount: interaction.likeCount,
                favoriteCount: interaction.favoriteCount,
                likedByMe: interaction.likedByMe,
                favoritedByMe: interaction.favoritedByMe,
            }));
        } catch (err) {
            window.alert(err.message || '鏇存柊鐐硅禐鐘舵€佸け璐ャ€?);
        } finally {
            setLiking(false);
        }
    };

    const handleFavorite = async () => {
        if (!post || favoriting) return;
        if (!user) {
            navigate('/login');
            return;
        }
        setFavoriting(true);
        try {
            const interaction = post.favoritedByMe
                ? await communityApi.unfavoritePost(post.id)
                : await communityApi.favoritePost(post.id);
            setPost((prev) => ({
                ...prev,
                likeCount: interaction.likeCount,
                favoriteCount: interaction.favoriteCount,
                likedByMe: interaction.likedByMe,
                favoritedByMe: interaction.favoritedByMe,
            }));
        } catch (err) {
            window.alert(err.message || '鏇存柊鏀惰棌鐘舵€佸け璐ャ€?);
        } finally {
            setFavoriting(false);
        }
    };

    const handleReport = async () => {
        if (!post) return;
        if (!user) {
            navigate('/login');
            return;
        }
        if (!window.confirm('纭畾瑕佷妇鎶ヨ繖绡囧笘瀛愬悧锛熸彁浜ゅ悗浼氳繘鍏ュ鏍搞€?)) return;
        setReporting(true);
        try {
            await communityApi.createReport({
                contentType: 'POST',
                contentId: post.id,
                reason: 'OTHER',
                description: `甯栧瓙璇︽儏椤典妇鎶ワ細${post.slug}`,
            });
            window.alert('涓炬姤宸叉彁浜わ紝鎰熻阿浣犵殑鍙嶉銆?);
        } catch (err) {
            window.alert(err.message || '鎻愪氦涓炬姤澶辫触銆?);
        } finally {
            setReporting(false);
        }
    };

    if (loading) {
        return <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-10 text-center text-slate-500">鍔犺浇涓?..</div>;
    }

    if (!post) {
        return <div className="rounded-3xl border border-white/5 bg-white/[0.03] p-10 text-center text-slate-500">{error || '甯栧瓙涓嶅瓨鍦ㄣ€?}</div>;
    }

    return (
        <article className="space-y-8">
            <Helmet>
                <title>{post.title} - 绀惧尯甯栧瓙</title>
            </Helmet>

            <header className="rounded-[2rem] border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex flex-wrap items-center justify-between gap-3">
                    <div className="flex flex-wrap items-center gap-3 text-xs uppercase tracking-[0.18em] text-slate-500">
                        <Link to={`/community/node/${post.node.slug}`} className="text-cyan-300 hover:text-cyan-200">{post.node.name}</Link>
                        <span>|</span>
                        <Link to={`/u/${post.author.username}`} className="hover:text-slate-300">@{post.author.username}</Link>
                    </div>
                    {user ? (
                        <button
                            type="button"
                            onClick={handleReport}
                            disabled={reporting}
                            className="inline-flex items-center gap-2 rounded-2xl border border-rose-400/30 px-3 py-1.5 text-xs font-medium text-rose-300 transition hover:bg-rose-500/10 disabled:opacity-60"
                        >
                            <AlertTriangle className="h-4 w-4" />
                            {reporting ? '鎻愪氦涓?..' : '涓炬姤'}
                        </button>
                    ) : (
                        <Link
                            to="/login"
                            className="inline-flex items-center gap-2 rounded-2xl border border-white/10 px-3 py-1.5 text-xs font-medium text-slate-400 transition hover:bg-white/5"
                        >
                            <LogIn className="h-4 w-4" />
                            鐧诲綍鍚庝妇鎶?                        </Link>
                    )}
                </div>

                <h1 className="mt-4 text-4xl font-bold leading-tight text-slate-100">{post.title}</h1>
                <p className="mt-4 text-sm leading-7 text-slate-400">{post.summary || '浣滆€呰繕娌℃湁濉啓鎽樿銆?}</p>

                <div className="mt-6 flex flex-wrap items-center gap-3">
                    <button
                        type="button"
                        onClick={handleLike}
                        disabled={liking}
                        className={`inline-flex items-center gap-2 rounded-2xl border px-3 py-1.5 text-xs font-medium transition ${
                            user && post.likedByMe
                                ? 'border-rose-400/40 bg-rose-500/20 text-rose-200'
                                : 'border-white/10 text-slate-300 hover:bg-white/5'
                        }`}
                    >
                        <Heart className="h-4 w-4" />
                        {post.likeCount || 0}
                    </button>
                    <button
                        type="button"
                        onClick={handleFavorite}
                        disabled={favoriting}
                        className={`inline-flex items-center gap-2 rounded-2xl border px-3 py-1.5 text-xs font-medium transition ${
                            user && post.favoritedByMe
                                ? 'border-amber-400/40 bg-amber-500/20 text-amber-100'
                                : 'border-white/10 text-slate-300 hover:bg-white/5'
                        }`}
                    >
                        <Star className="h-4 w-4" />
                        {post.favoriteCount || 0}
                    </button>
                    <p className="text-xs uppercase tracking-[0.18em] text-slate-500">{post.viewCount || 0} 娆℃祻瑙?/p>
                    {!user && (
                        <Link to="/login" className="text-xs text-cyan-300 hover:text-cyan-200">
                            鐧诲綍鍚庡彲浜掑姩
                        </Link>
                    )}
                </div>
            </header>

            <div className="prose prose-invert max-w-none rounded-[2rem] border border-white/5 bg-white/[0.03] p-8 prose-headings:text-slate-100 prose-p:text-slate-300 prose-a:text-cyan-300 prose-strong:text-slate-100 sm:p-10">
                <ReactMarkdown>{post.content || ''}</ReactMarkdown>
            </div>

            <CommunityPostCommentSection postId={post.id} postSlug={post.slug} />
        </article>
    );
};

export default CommunityPostDetailPage;

