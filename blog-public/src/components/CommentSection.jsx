import React, { useState, useEffect } from 'react';
import { User, Mail, Send, MessageSquare } from 'lucide-react';
import request from '../api/request';

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

const CommentSection = ({ articleId }) => {
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [form, setForm] = useState({ nickname: '', email: '', content: '' });
    const [submitMessage, setSubmitMessage] = useState('');

    useEffect(() => {
        if (!articleId) return;

        const fetchComments = async () => {
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

        fetchComments();
    }, [articleId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.nickname || !form.email || !form.content) return;

        setSubmitting(true);
        setSubmitMessage('');
        try {
            await request.post('/api/public/comments', { ...form, articleId });
            setForm({ nickname: '', email: '', content: '' });
            setSubmitMessage('评论已提交，审核通过后会显示在这里。');
        } catch (error) {
            console.error('Failed to submit comment', error);
            setSubmitMessage('评论提交失败，请稍后重试。');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="bg-white/[0.02] border border-white/5 rounded-3xl p-6 sm:p-10 font-sans shadow-xl">
            <h3 className="text-2xl font-bold text-slate-100 mb-8 flex items-center gap-3">
                <MessageSquare className="w-6 h-6 text-indigo-400" />
                评论区 ({comments.length})
            </h3>

            <form onSubmit={handleSubmit} className="mb-14 bg-[#09090b]/50 p-6 rounded-2xl border border-white/5">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
                    <div className="relative">
                        <User className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                        <input
                            type="text"
                            placeholder="昵称 *"
                            required
                            value={form.nickname}
                            onChange={(e) => setForm({ ...form, nickname: e.target.value })}
                            className="w-full bg-white/[0.03] border border-white/10 rounded-xl py-3 pl-11 pr-4 text-sm text-slate-200 focus:outline-none focus:border-indigo-500/50 focus:bg-white/[0.05] transition-all placeholder:text-slate-600"
                        />
                    </div>
                    <div className="relative">
                        <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                        <input
                            type="email"
                            placeholder="邮箱 *"
                            required
                            value={form.email}
                            onChange={(e) => setForm({ ...form, email: e.target.value })}
                            className="w-full bg-white/[0.03] border border-white/10 rounded-xl py-3 pl-11 pr-4 text-sm text-slate-200 focus:outline-none focus:border-indigo-500/50 focus:bg-white/[0.05] transition-all placeholder:text-slate-600"
                        />
                    </div>
                </div>
                <div className="mb-4">
                    <textarea
                        rows="4"
                        placeholder="写下你的评论... 支持 Markdown 语法"
                        required
                        value={form.content}
                        onChange={(e) => setForm({ ...form, content: e.target.value })}
                        className="w-full bg-white/[0.03] border border-white/10 rounded-xl p-4 text-sm text-slate-200 focus:outline-none focus:border-indigo-500/50 focus:bg-white/[0.05] transition-all placeholder:text-slate-600 resize-none"
                    />
                </div>
                <div className="flex justify-end">
                    <button type="submit" disabled={submitting} className="flex items-center gap-2 bg-indigo-500 hover:bg-indigo-600 text-white px-6 py-2.5 rounded-xl text-sm font-medium transition-colors disabled:opacity-50">
                        {submitting ? '提交中...' : <><Send className="w-4 h-4" /> 发布评论</>}
                    </button>
                </div>
                {submitMessage && (
                    <p className="mt-4 text-sm text-slate-400">{submitMessage}</p>
                )}
            </form>

            <div className="space-y-8">
                {loading ? (
                    <div className="text-center py-10 text-slate-500">加载中...</div>
                ) : comments.length > 0 ? (
                    comments.map((comment) => (
                        <div key={comment.id} className="flex gap-4 sm:gap-5 group">
                            <div className="flex-shrink-0">
                                <img
                                    src={comment.avatar || `https://api.dicebear.com/7.x/identicon/svg?seed=${comment.nickname}&backgroundColor=1e1e2e`}
                                    alt={comment.nickname}
                                    className="w-10 h-10 sm:w-12 sm:h-12 rounded-full border border-white/10 bg-slate-800 object-cover"
                                />
                            </div>
                            <div className="flex-1 bg-white/[0.02] border border-white/5 rounded-2xl p-5 group-hover:bg-white/[0.04] transition-colors">
                                <div className="flex flex-wrap items-center justify-between gap-y-1 mb-3">
                                    <span className="font-semibold text-slate-200">{comment.nickname}</span>
                                    <span className="text-xs text-slate-500">{formatDate(comment.createdAt)}</span>
                                </div>
                                <div className="text-sm text-slate-300 leading-relaxed font-light whitespace-pre-wrap">
                                    {comment.content}
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="text-center py-12 text-slate-500 bg-white/[0.01] rounded-2xl border border-white/5 border-dashed">
                        还没有人评论，来做第一个留言的人吧！
                    </div>
                )}
            </div>
        </div>
    );
};

export default CommentSection;
