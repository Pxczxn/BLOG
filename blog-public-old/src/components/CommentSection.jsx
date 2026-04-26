/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { User, Mail, Send, MessageSquare, CornerDownRight, X } from 'lucide-react';
import request from '../api/request';
import { useAuth } from '../auth/AuthContext';

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

const avatarFallback = (seed) => `https://api.dicebear.com/7.x/identicon/svg?seed=${encodeURIComponent(seed)}&backgroundColor=1e1e2e`;

const buildCommentTree = (comments) => {
    const map = new Map();
    comments.forEach((item) => {
        map.set(item.id, { ...item, children: [] });
    });

    const roots = [];
    comments.forEach((item) => {
        const current = map.get(item.id);
        if (item.parentId && map.has(item.parentId)) {
            map.get(item.parentId).children.push(current);
        } else {
            roots.push(current);
        }
    });
    return roots;
};

const CommentSection = ({ articleId }) => {
    const { user } = useAuth();
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [replyTarget, setReplyTarget] = useState(null);
    const [form, setForm] = useState({ nickname: '', email: '', content: '' });
    const [submitMessage, setSubmitMessage] = useState('');

    const fetchComments = async () => {
        if (!articleId) return;
        setLoading(true);
        try {
            const resp = await request.get(`/api/public/articles/${articleId}/comments`);
            setComments(resp.data || resp || []);
        } catch (error) {
            console.error('Failed to fetch comments', error);
            setComments([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchComments();
    }, [articleId]);

    const commentTree = useMemo(() => buildCommentTree(comments), [comments]);

    const handleReply = (comment) => {
        setReplyTarget({ id: comment.id, nickname: comment.nickname });
    };

    const cancelReply = () => {
        setReplyTarget(null);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        if ((!user && (!form.nickname || !form.email)) || !form.content.trim()) return;

        setSubmitting(true);
        setSubmitMessage('');
        try {
            const payload = {
                articleId,
                content: form.content.trim(),
                parentId: replyTarget?.id || null,
            };
            if (!user) {
                payload.nickname = form.nickname.trim();
                payload.email = form.email.trim();
            }
            await request.post('/api/public/comments', payload);
            setForm({ nickname: '', email: '', content: '' });
            setReplyTarget(null);
            setSubmitMessage('Comment submitted and waiting for moderation.');
            fetchComments();
        } catch (error) {
            console.error('Failed to submit comment', error);
            setSubmitMessage(error.message || 'Failed to submit comment. Please try again later.');
        } finally {
            setSubmitting(false);
        }
    };

    const renderComment = (comment, depth = 0) => (
        <div key={comment.id} className="space-y-3">
            <div className="group flex gap-4 sm:gap-5">
                <div className="flex-shrink-0">
                    <img
                        src={comment.avatar || avatarFallback(comment.profileUsername || comment.nickname)}
                        alt={comment.nickname}
                        className="h-10 w-10 rounded-full border border-white/10 bg-slate-800 object-cover sm:h-12 sm:w-12"
                    />
                </div>
                <div className="flex-1 rounded-2xl border border-white/5 bg-white/[0.02] p-5 transition-colors group-hover:bg-white/[0.04]">
                    <div className="mb-3 flex flex-wrap items-center justify-between gap-y-1">
                        {comment.communityUser && comment.profileUsername ? (
                            <Link to={`/u/${comment.profileUsername}`} className="font-semibold text-cyan-300 hover:text-cyan-200">
                                {comment.nickname}
                            </Link>
                        ) : (
                            <span className="font-semibold text-slate-200">{comment.nickname}</span>
                        )}
                        <span className="text-xs text-slate-500">{formatDate(comment.createdAt)}</span>
                    </div>

                    {comment.replyToNickname && (
                        <p className="mb-2 flex items-center gap-2 text-xs text-slate-400">
                            <CornerDownRight className="h-3.5 w-3.5" />
                            Replying to <span className="text-slate-300">{comment.replyToNickname}</span>
                        </p>
                    )}

                    <div className="whitespace-pre-wrap text-sm font-light leading-relaxed text-slate-300">
                        {comment.content}
                    </div>

                    <div className="mt-3">
                        <button
                            type="button"
                            onClick={() => handleReply(comment)}
                            className="text-xs text-slate-400 hover:text-slate-200"
                        >
                            Reply
                        </button>
                    </div>
                </div>
            </div>

            {comment.children?.length > 0 && (
                <div className="space-y-3 border-l border-white/10 pl-4 sm:pl-8" style={{ marginLeft: depth > 0 ? 8 : 56 }}>
                    {comment.children.map((child) => renderComment(child, depth + 1))}
                </div>
            )}
        </div>
    );

    return (
        <div className="rounded-3xl border border-white/5 bg-white/[0.02] p-6 font-sans shadow-xl sm:p-10">
            <h3 className="mb-8 flex items-center gap-3 text-2xl font-bold text-slate-100">
                <MessageSquare className="h-6 w-6 text-indigo-400" />
                Comments ({comments.length})
            </h3>

            <form onSubmit={handleSubmit} className="mb-14 rounded-2xl border border-white/5 bg-[#09090b]/50 p-6">
                {replyTarget && (
                    <div className="mb-4 flex items-center justify-between rounded-2xl border border-amber-300/30 bg-amber-300/10 p-3 text-sm text-amber-100">
                        <span>Replying to {replyTarget.nickname}</span>
                        <button type="button" onClick={cancelReply} className="inline-flex items-center gap-1 text-xs text-amber-200 hover:text-amber-100">
                            <X className="h-3.5 w-3.5" />
                            Cancel
                        </button>
                    </div>
                )}

                {user ? (
                    <div className="mb-4 rounded-2xl border border-cyan-500/20 bg-cyan-500/5 p-4 text-sm text-slate-300">
                        You are commenting as <Link to={`/u/${user.username}`} className="text-cyan-300 hover:text-cyan-200">{user.displayName || user.username}</Link>.
                    </div>
                ) : (
                    <div className="mb-4 grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <div className="relative">
                            <User className="absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
                            <input
                                type="text"
                                placeholder="Nickname *"
                                required
                                value={form.nickname}
                                onChange={(event) => setForm({ ...form, nickname: event.target.value })}
                                className="w-full rounded-xl border border-white/10 bg-white/[0.03] py-3 pl-11 pr-4 text-sm text-slate-200 transition-all placeholder:text-slate-600 focus:border-indigo-500/50 focus:bg-white/[0.05] focus:outline-none"
                            />
                        </div>
                        <div className="relative">
                            <Mail className="absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
                            <input
                                type="email"
                                placeholder="Email *"
                                required
                                value={form.email}
                                onChange={(event) => setForm({ ...form, email: event.target.value })}
                                className="w-full rounded-xl border border-white/10 bg-white/[0.03] py-3 pl-11 pr-4 text-sm text-slate-200 transition-all placeholder:text-slate-600 focus:border-indigo-500/50 focus:bg-white/[0.05] focus:outline-none"
                            />
                        </div>
                    </div>
                )}

                <div className="mb-4">
                    <textarea
                        rows="4"
                        placeholder="Write your comment..."
                        required
                        value={form.content}
                        onChange={(event) => setForm({ ...form, content: event.target.value })}
                        className="w-full resize-none rounded-xl border border-white/10 bg-white/[0.03] p-4 text-sm text-slate-200 transition-all placeholder:text-slate-600 focus:border-indigo-500/50 focus:bg-white/[0.05] focus:outline-none"
                    />
                </div>

                <div className="flex justify-end">
                    <button type="submit" disabled={submitting} className="flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2.5 text-sm font-medium text-white transition-colors hover:bg-indigo-600 disabled:opacity-50">
                        {submitting ? 'Submitting...' : <><Send className="h-4 w-4" /> Post comment</>}
                    </button>
                </div>
                {submitMessage && <p className="mt-4 text-sm text-slate-400">{submitMessage}</p>}
            </form>

            <div className="space-y-8">
                {loading ? (
                    <div className="py-10 text-center text-slate-500">Loading comments...</div>
                ) : commentTree.length > 0 ? (
                    commentTree.map((comment) => renderComment(comment))
                ) : (
                    <div className="rounded-2xl border border-dashed border-white/5 bg-white/[0.01] py-12 text-center text-slate-500">
                        No comments yet. Be the first one.
                    </div>
                )}
            </div>
        </div>
    );
};

export default CommentSection;

