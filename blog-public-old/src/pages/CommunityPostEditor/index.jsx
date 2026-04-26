/*
 * 功能：前端模块逻辑。
 */
import React, { useEffect, useRef, useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { Link, Navigate, useNavigate, useParams } from 'react-router-dom';
import { communityApi } from '../../api/community';
import { useAuth } from '../../auth/AuthContext';
import { Bold, Italic, Heading1, Heading2, Heading3, Code, Link as LinkIcon, List, ListOrdered, Quote } from 'lucide-react';

const CommunityPostEditorPage = () => {
    const { id } = useParams();
    const isEdit = Boolean(id);
    const navigate = useNavigate();
    const { user, initializing } = useAuth();
    const [nodes, setNodes] = useState([]);
    const [form, setForm] = useState({
        nodeId: '',
        title: '',
        summary: '',
        content: '',
        status: 'DRAFT',
    });
    const [loading, setLoading] = useState(isEdit);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const contentRef = useRef(null);

    const insertMarkdown = (prefix, suffix = '', placeholder = '') => {
        const textarea = contentRef.current;
        if (!textarea) return;

        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const selectedText = form.content.substring(start, end);
        const textToInsert = selectedText || placeholder;

        const before = form.content.substring(0, start);
        const after = form.content.substring(end);

        const newContent = before + prefix + textToInsert + suffix + after;
        setForm({ ...form, content: newContent });

        setTimeout(() => {
            textarea.focus();
            const newPos = start + prefix.length + textToInsert.length;
            textarea.setSelectionRange(newPos, newPos);
        }, 0);
    };

    const mdTools = [
        { icon: Heading1, label: '涓€绾ф爣棰?, action: () => insertMarkdown('# ', '\n', '鏍囬') },
        { icon: Heading2, label: '浜岀骇鏍囬', action: () => insertMarkdown('## ', '\n', '鏍囬') },
        { icon: Heading3, label: '涓夌骇鏍囬', action: () => insertMarkdown('### ', '\n', '鏍囬') },
        { icon: Bold, label: '鍔犵矖', action: () => insertMarkdown('**', '**', '鍔犵矖鏂囨湰') },
        { icon: Italic, label: '鏂滀綋', action: () => insertMarkdown('*', '*', '鏂滀綋鏂囨湰') },
        { icon: Code, label: '浠ｇ爜', action: () => insertMarkdown('`', '`', '浠ｇ爜') },
        { icon: LinkIcon, label: '閾炬帴', action: () => insertMarkdown('[', '](url)', '閾炬帴鏂囨湰') },
        { icon: List, label: '鏃犲簭鍒楄〃', action: () => insertMarkdown('- ', '\n', '鍒楄〃椤?) },
        { icon: ListOrdered, label: '鏈夊簭鍒楄〃', action: () => insertMarkdown('1. ', '\n', '鍒楄〃椤?) },
        { icon: Quote, label: '寮曠敤', action: () => insertMarkdown('> ', '\n', '寮曠敤鏂囨湰') },
    ];

    useEffect(() => {
        const bootstrap = async () => {
            try {
                const nodeData = await communityApi.listNodes();
                setNodes(Array.isArray(nodeData) ? nodeData : []);
                if (!isEdit) {
                    return;
                }
                const post = await communityApi.getPostEditor(id);
                setForm({
                    nodeId: String(post.nodeId),
                    title: post.title || '',
                    summary: post.summary || '',
                    content: post.content || '',
                    status: post.status || 'DRAFT',
                });
            } catch (err) {
                setError(err.message || '鍔犺浇甯栧瓙缂栬緫椤靛け璐ャ€?);
            } finally {
                setLoading(false);
            }
        };

        bootstrap();
    }, [id, isEdit]);

    if (!initializing && !user) {
        return <Navigate to="/login" replace />;
    }

    const handleSubmit = async (nextStatus) => {
        if (!form.nodeId || !form.title.trim() || !form.content.trim()) {
            setError('鑺傜偣銆佹爣棰樺拰姝ｆ枃涓嶈兘涓虹┖銆?);
            return;
        }
        setSaving(true);
        setError('');
        try {
            const payload = {
                ...form,
                nodeId: Number(form.nodeId),
                title: form.title.trim(),
                summary: form.summary.trim(),
                content: form.content.trim(),
                status: nextStatus,
            };
            const response = isEdit
                ? await communityApi.updatePost(id, payload)
                : await communityApi.createPost(payload);
            navigate(response.status === 'PUBLISHED' ? `/community/post/${response.slug}` : '/me');
        } catch (err) {
            setError(err.message || '淇濆瓨甯栧瓙澶辫触銆?);
        } finally {
            setSaving(false);
        }
    };

    return (
        <section className="space-y-8">
            <Helmet>
                <title>{isEdit ? '缂栬緫甯栧瓙' : '鍙戝竷甯栧瓙'} - 绀惧尯</title>
            </Helmet>

            <div className="rounded-[2rem] border border-white/5 bg-white/[0.03] p-8 shadow-2xl sm:p-10">
                <div className="flex flex-wrap items-center justify-between gap-4">
                    <div>
                        <p className="text-xs uppercase tracking-[0.3em] text-cyan-300/80">绀惧尯缂栬緫鍣?/p>
                        <h1 className="mt-3 text-3xl font-bold text-slate-100">{isEdit ? '缂栬緫浣犵殑甯栧瓙' : '鍐欎竴绡囨柊鐨勫笘瀛?}</h1>
                    </div>
                    <Link to="/me" className="text-sm text-slate-400 hover:text-slate-200">鍥炲埌鎴戠殑涓婚〉</Link>
                </div>

                {loading ? (
                    <div className="mt-8 text-sm text-slate-500">鍔犺浇涓?..</div>
                ) : (
                    <div className="mt-8 space-y-5">
                        <select
                            value={form.nodeId}
                            onChange={(event) => setForm({ ...form, nodeId: event.target.value })}
                            className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-cyan-400/50"
                        >
                            <option value="">閫夋嫨涓€涓妭鐐?/option>
                            {nodes.map((node) => (
                                <option key={node.id} value={node.id}>{node.name}</option>
                            ))}
                        </select>
                        <input
                            type="text"
                            value={form.title}
                            onChange={(event) => setForm({ ...form, title: event.target.value })}
                            placeholder="甯栧瓙鏍囬"
                            className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-cyan-400/50"
                        />
                        <textarea
                            rows={4}
                            value={form.summary}
                            onChange={(event) => setForm({ ...form, summary: event.target.value })}
                            placeholder="涓€鍙ヨ瘽鎽樿锛屾柟渚垮垪琛ㄩ〉蹇€熸祻瑙?
                            className="w-full rounded-2xl border border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-cyan-400/50"
                        />
                        <div className="space-y-2">
                            <div className="flex flex-wrap gap-1 rounded-t-2xl border border-b-0 border-white/10 bg-[#09090b] px-3 py-2">
                                {mdTools.map((tool, index) => (
                                    <button
                                        key={index}
                                        type="button"
                                        title={tool.label}
                                        onClick={tool.action}
                                        className="rounded-lg p-2 text-slate-400 transition hover:bg-white/10 hover:text-slate-100"
                                    >
                                        <tool.icon className="h-4 w-4" />
                                    </button>
                                ))}
                            </div>
                            <textarea
                                ref={contentRef}
                                rows={16}
                                value={form.content}
                                onChange={(event) => setForm({ ...form, content: event.target.value })}
                                placeholder="鏀寔 Markdown锛屾斁蹇冨睍寮€鍐欍€?
                                className="w-full rounded-b-2xl border border-t-0 border-white/10 bg-[#09090b] px-5 py-3.5 text-sm text-slate-100 outline-none transition focus:border-cyan-400/50 focus:rounded-2xl focus:border-t"
                            />
                        </div>
                        <div className="flex flex-wrap gap-3">
                            <button type="button" disabled={saving} onClick={() => handleSubmit('DRAFT')} className="rounded-2xl border border-white/10 px-5 py-3 text-sm text-slate-300 hover:bg-white/5 disabled:opacity-50">
                                {saving ? '淇濆瓨涓?..' : '淇濆瓨鑽夌'}
                            </button>
                            <button type="button" disabled={saving} onClick={() => handleSubmit('PUBLISHED')} className="rounded-2xl bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 hover:bg-cyan-400 disabled:opacity-50">
                                {saving ? '鍙戝竷涓?..' : '鍙戝竷甯栧瓙'}
                            </button>
                        </div>
                        {error && <p className="text-sm text-rose-300">{error}</p>}
                    </div>
                )}
            </div>
        </section>
    );
};

export default CommunityPostEditorPage;

