/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AlertTriangle, CornerDownRight, MessageSquare, Send, X } from 'lucide-react';
import { useAuth } from '../auth/AuthContext';
import { communityApi } from '../api/community';

const avatarFallback = (seed) => `https://api.dicebear.com/7.x/identicon/svg?seed=${encodeURIComponent(seed)}&backgroundColor=1e1e2e`;

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

const buildCommentTree = (comments) => {
    const map = new Map();
    comments.forEach((item) => {
        map.set(item.id, { ...item, children: [] });
    });
    const roots = [];
    comments.forEach((item) => {
        const node = map.get(item.id);
        if (item.parentId && map.has(item.parentId)) {
            map.get(item.parentId).children.push(node);
        } else {
            roots.push(node);
        }
    });
    return roots;
};

const CommunityPostCommentSection = ({ postId, postSlug }) => {
    const navigate = useNavigate();
    const { user } = useAuth();
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [replyTarget, setReplyTarget] = useState(null);
    const [content, setContent] = useState('');
    const [message, setMessage] = useState('');

    const fetchComments = async () => {
        if (!postSlug) return;
        setLoading(true);
        try {
            const data = await communityApi.listPostComments(postSlug);
            setComments(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error('鍔犺浇绀惧尯甯栧瓙璇勮澶辫触', error);
            setComments([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchComments();
    }, [postSlug]);

    const tree = useMemo(() => buildCommentTree(comments), [comments]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!content.trim()) return;
        if (!user) {
            navigate('/login');
            return;
        }
        setSubmitting(true);
        setMessage('');
        try {
            const created = await communityApi.createPostComment(postId, {
                parentId: replyTarget?.id || null,
                content: content.trim(),
            });
            setContent('');
            setReplyTarget(null);
            if (created?.status === 'PENDING') {
                setMessage('璇勮宸叉彁浜わ紝绛夊緟瀹℃牳閫氳繃鍚庡睍绀恒€?);
                return;
            }
            if (created?.status === 'REJECTED') {
                setMessage('璇勮鏈€氳繃瀹℃牳瑙勫垯锛岃璋冩暣鍚庡啀璇曘€?);
                return;
            }
            setMessage('璇勮鍙戝竷鎴愬姛銆?);
            fetchComments();
        } catch (error) {
            setMessage(error.message || '鍙戣〃璇勮澶辫触銆?);
        } finally {
            setSubmitting(false);
        }
    };

    const handleReportComment = async (commentId) => {
        if (!commentId) return;
        if (!user) {
            navigate('/login');
            return;
        }
        if (!window.confirm('纭畾瑕佷妇鎶ヨ繖鏉¤瘎璁哄悧锛熸彁浜ゅ悗浼氳繘鍏ュ鏍搞€?)) {
            return;
        }
        try {
            await communityApi.createReport({
                contentType: 'POST_COMMENT',
                contentId: commentId,
                reason: 'OTHER',
                description: `绀惧尯甯栧瓙璇勮涓炬姤锛?{postSlug || '-'}`,
            });
            setMessage('涓炬姤宸叉彁浜わ紝鎰熻阿浣犵殑鍙嶉銆?);
        } catch (error) {
            setMessage(error.message || '鎻愪氦涓炬姤澶辫触銆?);
        }
    };

    const renderComment = (item, depth = 0) => (
        <div key={item.id} className="space-y-3">
            <div className="group flex gap-4 sm:gap-5">
                <div className="flex-shrink-0">
                    <img
                        src={item.avatar || avatarFallback(item.profileUsername || item.nickname)}
                        alt={item.nickname}
                        className="h-10 w-10 rounded-full border border-white/10 bg-slate-800 object-cover sm:h-12 sm:w-12"
                    />
                </div>
                <div className="flex-1 rounded-2xl border border-white/5 bg-white/[0.02] p-5 transition-colors group-hover:bg-white/[0.04]">
                    <div className="mb-3 flex flex-wrap items-center justify-between gap-y-1">
                        <Link to={`/u/${item.profileUsername || ''}`} className="font-semibold text-cyan-300 hover:text-cyan-200">
                            {item.nickname}
                        </Link>
                        <span className="text-xs text-slate-500">{formatDate(item.createdAt)}</span>
                    </div>
                    {item.replyToNickname && (
                        <p className="mb-2 flex items-center gap-2 text-xs text-slate-400">
                            <CornerDownRight className="h-3.5 w-3.5" />
                            姝ｅ湪鍥炲 <span className="text-slate-300">{item.replyToNickname}</span>
                        </p>
                    )}
                    <p className="whitespace-pre-wrap text-sm leading-relaxed text-slate-300">{item.content}</p>
                    <div className="mt-3 flex items-center gap-4">
                        <button
                            type="button"
                            onClick={() => setReplyTarget({ id: item.id, nickname: item.nickname })}
                            className="text-xs text-slate-400 hover:text-slate-200"
                        >
                            鍥炲
                        </button>
                        <button
                            type="button"
                            onClick={() => handleReportComment(item.id)}
                            className="inline-flex items-center gap-1 text-xs text-rose-300 hover:text-rose-200"
                        >
                            <AlertTriangle className="h-3.5 w-3.5" />
                            涓炬姤
                        </button>
                    </div>
                </div>
            </div>
            {item.children?.length > 0 && (
                <div className="space-y-3 border-l border-white/10 pl-4 sm:pl-8" style={{ marginLeft: depth > 0 ? 8 : 56 }}>
                    {item.children.map((child) => renderComment(child, depth + 1))}
                </div>
            )}
        </div>
    );

    return (
        <section className="rounded-3xl border border-white/5 bg-white/[0.02] p-6 shadow-xl sm:p-10">
            <h3 className="mb-8 flex items-center gap-3 text-2xl font-bold text-slate-100">
                <MessageSquare className="h-6 w-6 text-indigo-400" />
                绀惧尯璇勮锛坽comments.length}锛?            </h3>

            <form onSubmit={handleSubmit} className="mb-10 rounded-2xl border border-white/5 bg-[#09090b]/50 p-6">
                {replyTarget && (
                    <div className="mb-4 flex items-center justify-between rounded-2xl border border-amber-300/30 bg-amber-300/10 p-3 text-sm text-amber-100">
                        <span>姝ｅ湪鍥炲 {replyTarget.nickname}</span>
                        <button type="button" onClick={() => setReplyTarget(null)} className="inline-flex items-center gap-1 text-xs text-amber-200 hover:text-amber-100">
                            <X className="h-3.5 w-3.5" /> 鍙栨秷
                        </button>
                    </div>
                )}

                {user ? (
                    <p className="mb-4 text-sm text-slate-300">
                        褰撳墠韬唤锛?Link to={`/u/${user.username}`} className="text-cyan-300 hover:text-cyan-200">{user.displayName || user.username}</Link>
                    </p>
                ) : (
                    <p className="mb-4 text-sm text-slate-400">鐧诲綍鍚庡嵆鍙弬涓庤璁恒€?/p>
                )}

                <textarea
                    rows={4}
                    value={content}
                    onChange={(event) => setContent(event.target.value)}
                    placeholder="鍐欎笅浣犵殑璇勮..."
                    className="w-full resize-none rounded-xl border border-white/10 bg-white/[0.03] p-4 text-sm text-slate-200 transition-all placeholder:text-slate-600 focus:border-indigo-500/50 focus:bg-white/[0.05] focus:outline-none"
                />

                <div className="mt-4 flex justify-end">
                    <button
                        type="submit"
                        disabled={submitting}
                        className="inline-flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2.5 text-sm font-medium text-white transition-colors hover:bg-indigo-600 disabled:opacity-50"
                    >
                        {submitting ? '鎻愪氦涓?..' : <><Send className="h-4 w-4" /> 鍙戣〃璇勮</>}
                    </button>
                </div>
                {message && <p className="mt-3 text-sm text-slate-400">{message}</p>}
            </form>

            <div className="space-y-8">
                {loading ? (
                    <div className="py-10 text-center text-slate-500">璇勮鍔犺浇涓?..</div>
                ) : tree.length > 0 ? (
                    tree.map((item) => renderComment(item))
                ) : (
                    <div className="rounded-2xl border border-dashed border-white/5 bg-white/[0.01] py-12 text-center text-slate-500">
                        杩樻病鏈夎瘎璁猴紝鏉ュ紑鍚涓€鏉¤璁哄惂銆?                    </div>
                )}
            </div>
        </section>
    );
};

export default CommunityPostCommentSection;

