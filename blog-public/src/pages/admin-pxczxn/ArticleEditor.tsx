import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import {
  ArrowLeft,
  Bold,
  Code2,
  Edit3,
  Eye,
  Heading1,
  Image as ImageIcon,
  Italic,
  List,
  ListOrdered,
  Quote,
  Save,
  Send,
} from 'lucide-react';
import request from '../../lib/request';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

type CategoryOption = {
  id: number;
  name: string;
};

type TagOption = {
  id: number;
  name: string;
};

type ArticleForm = {
  title: string;
  summary: string;
  content: string;
  coverImage: string;
  categoryId: string;
  status: string;
};

const MAX_SELECTED_TAGS = 8;

export default function ArticleEditor() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);

  const [form, setForm] = useState<ArticleForm>({
    title: '',
    summary: '',
    content: '',
    coverImage: '',
    categoryId: '',
    status: 'DRAFT',
  });
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [tags, setTags] = useState<TagOption[]>([]);
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const [previewMode, setPreviewMode] = useState<'write' | 'preview'>('write');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;

    async function fetchDeps() {
      try {
        const [catRes, tagRes] = await Promise.all([
          request.get('/api/public/categories'),
          request.get('/api/public/tags'),
        ]);

        if (!mounted) return;
        setCategories((catRes?.data ?? catRes ?? []) as CategoryOption[]);
        setTags((tagRes?.data ?? tagRes ?? []) as TagOption[]);
      } catch {
        if (mounted) {
          setCategories([]);
          setTags([]);
        }
      }
    }

    async function fetchArticle() {
      if (!isEdit) return;

      try {
        setLoading(true);
        const res: any = await request.get(`/api/admin/articles/${id}`);
        const data = res?.data ?? res;

        if (!mounted) return;
        setForm({
          title: data.title || '',
          summary: data.summary || '',
          content: data.content || '',
          coverImage: data.coverImage || data.cover || '',
          categoryId: data.categoryId ? String(data.categoryId) : data.category?.id ? String(data.category.id) : '',
          status: data.status || 'DRAFT',
        });
        setSelectedTagIds(Array.isArray(data.tagIds) ? data.tagIds.map(Number) : []);
      } catch {
        if (mounted) {
          setError('获取文章详情失败');
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    }

    fetchDeps();
    fetchArticle();

    return () => {
      mounted = false;
    };
  }, [id, isEdit]);

  const setField = <K extends keyof ArticleForm>(key: K, value: ArticleForm[K]) => {
    setForm((current) => ({ ...current, [key]: value }));
  };

  const updateContentWithSelection = (nextValue: string, nextStart: number, nextEnd: number) => {
    setField('content', nextValue);
    window.setTimeout(() => {
      textareaRef.current?.focus();
      textareaRef.current?.setSelectionRange(nextStart, nextEnd);
    }, 0);
  };

  const insertInline = (before: string, after: string, placeholder: string) => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selected = form.content.slice(start, end) || placeholder;
    const nextValue = `${form.content.slice(0, start)}${before}${selected}${after}${form.content.slice(end)}`;
    const selectionStart = start + before.length;
    const selectionEnd = selectionStart + selected.length;

    updateContentWithSelection(nextValue, selectionStart, selectionEnd);
  };

  const insertLinePrefix = (prefix: string, placeholder: string) => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selected = form.content.slice(start, end) || placeholder;
    const prefixed = selected
      .split('\n')
      .map((line) => `${prefix}${line}`)
      .join('\n');
    const nextValue = `${form.content.slice(0, start)}${prefixed}${form.content.slice(end)}`;

    updateContentWithSelection(nextValue, start + prefix.length, start + prefixed.length);
  };

  const toggleTag = (tagId: number) => {
    setSelectedTagIds((current) => {
      if (current.includes(tagId)) {
        return current.filter((id) => id !== tagId);
      }

      if (current.length >= MAX_SELECTED_TAGS) {
        return current;
      }

      return [...current, tagId];
    });
  };

  const saveArticle = async (forceStatus: string) => {
    if (!form.title.trim() || !form.content.trim()) {
      setError('标题和内容不能为空');
      return;
    }

    setError('');
    setSaving(true);

    const payload = {
      ...form,
      title: form.title.trim(),
      content: form.content.trim(),
      summary: form.summary.trim() || undefined,
      coverImage: form.coverImage.trim() || undefined,
      categoryId: form.categoryId ? Number(form.categoryId) : null,
      status: forceStatus,
    };

    try {
      let savedArticleId = id;
      if (isEdit) {
        await request.put(`/api/admin/articles/${id}`, payload);
      } else {
        const res: any = await request.post('/api/admin/articles', payload);
        const data = res?.data ?? res;
        savedArticleId = String(data.id);
      }

      if (savedArticleId) {
        await request.put(`/api/admin/articles/${savedArticleId}/tags`, {
          tagIds: selectedTagIds,
        });
      }

      navigate('/admin-pxczxn/articles');
    } catch (err: any) {
      setError(err?.message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  const toolbarItems = [
    {
      label: '一级标题',
      icon: Heading1,
      action: () => insertLinePrefix('# ', '一级标题'),
    },
    {
      label: '加粗',
      icon: Bold,
      action: () => insertInline('**', '**', '加粗文字'),
    },
    {
      label: '斜体',
      icon: Italic,
      action: () => insertInline('*', '*', '斜体文字'),
    },
    {
      label: '无序列表',
      icon: List,
      action: () => insertLinePrefix('- ', '列表项'),
    },
    {
      label: '有序列表',
      icon: ListOrdered,
      action: () => insertLinePrefix('1. ', '列表项'),
    },
    {
      label: '引用',
      icon: Quote,
      action: () => insertLinePrefix('> ', '引用内容'),
    },
    {
      label: '代码',
      icon: Code2,
      action: () => insertInline('`', '`', 'code'),
    },
  ];

  if (loading) {
    return <div className="text-white">加载中...</div>;
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6">
      <AdminPageHeader
        className="mb-6"
        title={isEdit ? '编辑文章' : '新增文章'}
        leading={
          <Link
            to="/admin-pxczxn/articles"
            className="flex h-8 w-8 items-center justify-center rounded-full border border-white/10 bg-white/5 text-slate-400 transition-colors hover:bg-white/10 hover:text-white"
          >
            <ArrowLeft className="h-4 w-4" />
          </Link>
        }
        actions={
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={() => saveArticle('DRAFT')}
              disabled={saving}
              className="flex items-center gap-2 rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-white transition-all hover:bg-white/10 disabled:opacity-50"
            >
              <Save className="h-4 w-4" /> 存为草稿
            </button>
            <button
              type="button"
              onClick={() => saveArticle('PUBLISHED')}
              disabled={saving}
              className="flex items-center gap-2 rounded-xl border-none bg-gradient-to-r from-purple-600 to-blue-600 px-4 py-2 text-sm font-medium text-white shadow-[0_0_15px_rgba(168,85,247,0.4)] transition-all hover:from-purple-500 hover:to-blue-500 disabled:opacity-50"
            >
              <Send className="h-4 w-4" /> 发布文章
            </button>
          </div>
        }
      />

      {error && (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-400">
          {error}
        </div>
      )}

      <section className="rounded-3xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-md">
        <div className="grid gap-5 md:grid-cols-2">
          <div>
            <label className="mb-1.5 block text-xs font-medium text-slate-400">分类</label>
            <select
              value={form.categoryId}
              onChange={(event) => setField('categoryId', event.target.value)}
              className="w-full appearance-none rounded-lg border border-white/10 bg-black/20 px-3 py-2 text-sm text-slate-300 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
            >
              <option value="">选择分类...</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="mb-1.5 block text-xs font-medium text-slate-400">封面大图 URL</label>
            <div className="relative">
              <ImageIcon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
              <input
                value={form.coverImage}
                onChange={(event) => setField('coverImage', event.target.value)}
                placeholder="https://..."
                className="w-full rounded-lg border border-white/10 bg-black/20 py-2 pl-9 pr-3 text-sm text-slate-300 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
              />
            </div>
          </div>
        </div>

        <div className="mt-5">
          <div className="mb-2 flex items-center justify-between">
            <label className="block text-xs font-medium text-slate-400">标签</label>
            <span className="text-xs text-slate-500">
              已选 {selectedTagIds.length}/{MAX_SELECTED_TAGS}
            </span>
          </div>
          <div className="flex flex-wrap gap-2">
            {tags.map((tag) => {
              const tagId = Number(tag.id);
              const active = selectedTagIds.includes(tagId);
              const disabled = !active && selectedTagIds.length >= MAX_SELECTED_TAGS;
              return (
                <button
                  key={tag.id}
                  type="button"
                  disabled={disabled}
                  onClick={() => toggleTag(tagId)}
                  className={`rounded-full border px-3 py-1.5 text-sm transition ${
                    active
                      ? 'border-purple-400 bg-purple-500/25 text-white'
                      : 'border-white/10 bg-white/5 text-slate-400 hover:border-purple-300/60 hover:text-white'
                  } ${disabled ? 'cursor-not-allowed opacity-40 hover:border-white/10 hover:text-slate-400' : ''}`}
                >
                  {tag.name}
                </button>
              );
            })}
            {tags.length === 0 && <span className="text-sm text-slate-500">暂无可选标签</span>}
          </div>
        </div>

        <div className="mt-5">
          <label className="mb-1.5 block text-xs font-medium text-slate-400">文章摘要</label>
          <textarea
            value={form.summary}
            onChange={(event) => setField('summary', event.target.value)}
            rows={3}
            className="w-full resize-none rounded-lg border border-white/10 bg-black/20 px-3 py-2 text-sm text-slate-300 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
            placeholder="留空则自动提取正文前 100 字..."
          />
        </div>

        {form.coverImage && (
          <div className="mt-5 h-36 overflow-hidden rounded-2xl border border-white/10">
            <img src={form.coverImage} alt="封面预览" className="h-full w-full object-cover" />
          </div>
        )}
      </section>

      <section className="rounded-3xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-md">
        <input
          value={form.title}
          onChange={(event) => setField('title', event.target.value)}
          placeholder="输入文章标题..."
          className="mb-6 w-full bg-transparent text-2xl font-bold text-white placeholder-slate-500 focus:outline-none"
        />

        <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
          <div className="flex flex-wrap gap-2 rounded-2xl border border-white/10 bg-black/20 p-2">
            {toolbarItems.map((item) => {
              const Icon = item.icon;
              return (
                <button
                  key={item.label}
                  type="button"
                  onClick={item.action}
                  title={item.label}
                  className="inline-flex h-9 items-center gap-2 rounded-xl px-3 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
                >
                  <Icon className="h-4 w-4" />
                  <span className="hidden sm:inline">{item.label}</span>
                </button>
              );
            })}
          </div>

          <div className="flex rounded-full border border-white/10 bg-white/5 p-1">
            <button
              type="button"
              onClick={() => setPreviewMode('write')}
              className={`inline-flex items-center gap-1 rounded-full px-3 py-1.5 text-xs transition ${
                previewMode === 'write' ? 'bg-purple-500/30 text-white' : 'text-slate-400'
              }`}
            >
              <Edit3 className="h-3.5 w-3.5" />
              编写
            </button>
            <button
              type="button"
              onClick={() => setPreviewMode('preview')}
              className={`inline-flex items-center gap-1 rounded-full px-3 py-1.5 text-xs transition ${
                previewMode === 'preview' ? 'bg-purple-500/30 text-white' : 'text-slate-400'
              }`}
            >
              <Eye className="h-3.5 w-3.5" />
              预览
            </button>
          </div>
        </div>

        {previewMode === 'write' ? (
          <textarea
            ref={textareaRef}
            value={form.content}
            onChange={(event) => setField('content', event.target.value)}
            placeholder="输入文章正文，支持 Markdown..."
            className="h-[520px] w-full resize-y rounded-xl border border-white/10 bg-black/20 p-4 font-mono text-sm leading-relaxed text-slate-300 placeholder-slate-600 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
          />
        ) : (
          <div className="min-h-[520px] rounded-xl border border-white/10 bg-black/20 p-6">
            {form.content.trim() ? (
              <article className="prose prose-invert max-w-none prose-headings:text-white prose-p:text-slate-300 prose-a:text-purple-300 prose-strong:text-white prose-code:text-purple-200 prose-pre:border prose-pre:border-white/10 prose-pre:bg-black/30">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{form.content}</ReactMarkdown>
              </article>
            ) : (
              <div className="flex min-h-[460px] flex-col items-center justify-center text-center text-slate-500">
                <Eye className="mb-3 h-8 w-8 text-purple-300/70" />
                <p className="font-medium text-slate-400">还没有可预览的内容</p>
                <p className="mt-1 text-sm">写一点 Markdown，再切到预览看看效果。</p>
              </div>
            )}
          </div>
        )}
      </section>
    </div>
  );
}
