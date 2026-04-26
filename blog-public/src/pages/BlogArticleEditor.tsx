import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import toast from 'react-hot-toast';
import {
  ArrowLeft,
  Bold,
  Code2,
  Edit3,
  Eye,
  Heading2,
  Heading3,
  Heading1,
  ImagePlus,
  Image as ImageIcon,
  Italic,
  Link as LinkIcon,
  List,
  ListOrdered,
  Minus,
  Quote,
  Send,
  Upload,
} from 'lucide-react';
import request from '../lib/request';
import { useAuth } from '../lib/AuthContext';

type CategoryOption = {
  id: number;
  name: string;
};

type TagOption = {
  id: number;
  name: string;
};

const MAX_SELECTED_TAGS = 8;

export default function BlogArticleEditor() {
  const { user, initializing } = useAuth();
  const navigate = useNavigate();
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);
  const coverInputRef = useRef<HTMLInputElement | null>(null);

  const [title, setTitle] = useState('');
  const [summary, setSummary] = useState('');
  const [content, setContent] = useState('');
  const [coverImage, setCoverImage] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [tags, setTags] = useState<TagOption[]>([]);
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const [previewMode, setPreviewMode] = useState<'write' | 'preview'>('write');
  const [loadingMeta, setLoadingMeta] = useState(true);
  const [coverUploading, setCoverUploading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;

    async function loadMeta() {
      try {
        setLoadingMeta(true);
        const [categoryRes, tagRes] = await Promise.all([
          request.get('/api/public/categories'),
          request.get('/api/public/tags'),
        ]);

        if (!mounted) return;

        setCategories((categoryRes?.data ?? categoryRes ?? []) as CategoryOption[]);
        setTags((tagRes?.data ?? tagRes ?? []) as TagOption[]);
      } catch {
        if (mounted) {
          setError('分类和标签加载失败，刷新页面再试一次。');
        }
      } finally {
        if (mounted) {
          setLoadingMeta(false);
        }
      }
    }

    loadMeta();

    return () => {
      mounted = false;
    };
  }, []);

  const updateContentWithSelection = (nextValue: string, nextStart: number, nextEnd: number) => {
    setContent(nextValue);
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
    const selected = content.slice(start, end) || placeholder;
    const nextValue = `${content.slice(0, start)}${before}${selected}${after}${content.slice(end)}`;
    const selectionStart = start + before.length;
    const selectionEnd = selectionStart + selected.length;

    updateContentWithSelection(nextValue, selectionStart, selectionEnd);
  };

  const insertLinePrefix = (prefix: string, placeholder: string) => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selected = content.slice(start, end) || placeholder;
    const prefixed = selected
      .split('\n')
      .map((line) => `${prefix}${line}`)
      .join('\n');
    const nextValue = `${content.slice(0, start)}${prefixed}${content.slice(end)}`;

    updateContentWithSelection(nextValue, start + prefix.length, start + prefixed.length);
  };

  const insertBlock = (block: string, cursorOffset = block.length) => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const needsBeforeBreak = start > 0 && content[start - 1] !== '\n';
    const needsAfterBreak = end < content.length && content[end] !== '\n';
    const nextBlock = `${needsBeforeBreak ? '\n' : ''}${block}${needsAfterBreak ? '\n' : ''}`;
    const nextValue = `${content.slice(0, start)}${nextBlock}${content.slice(end)}`;
    const nextCursor = start + (needsBeforeBreak ? 1 : 0) + cursorOffset;

    updateContentWithSelection(nextValue, nextCursor, nextCursor);
  };

  const handleCoverUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      setCoverUploading(true);
      const res: any = await request.post('/api/community/upload/cover', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      const url = res?.data?.url || res?.data || res?.url || res;
      if (typeof url === 'string') {
        setCoverImage(url);
        toast.success('封面上传成功', { duration: 1500 });
      } else {
        toast.error('封面上传成功，但没有拿到图片地址', { duration: 1800 });
      }
    } catch (err: any) {
      toast.error(err?.message || '封面上传失败', { duration: 1800 });
    } finally {
      setCoverUploading(false);
      if (event.target) {
        event.target.value = '';
      }
    }
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

  const publishArticle = async () => {
    const trimmedTitle = title.trim();
    const trimmedContent = content.trim();

    if (!trimmedTitle || !trimmedContent) {
      setError('标题和正文都要写一点内容。');
      return;
    }

    try {
      setSaving(true);
      setError('');
      await request.post('/api/community/articles', {
        title: trimmedTitle,
        summary: summary.trim() || trimmedContent.replace(/\s+/g, ' ').slice(0, 160),
        content: trimmedContent,
        coverImage: coverImage.trim() || undefined,
        categoryId: categoryId ? Number(categoryId) : null,
        status: 'PUBLISHED',
        tagIds: selectedTagIds,
      });
      toast.success('文章发布成功', { duration: 1500 });
      navigate('/blog');
    } catch (err: any) {
      setError(err?.message || '发布失败，请稍后重试。');
    } finally {
      setSaving(false);
    }
  };

  const toolbarGroups = [
    {
      label: '标题',
      items: [
        { label: '一级标题', short: 'H1', icon: Heading1, action: () => insertLinePrefix('# ', '一级标题') },
        { label: '二级标题', short: 'H2', icon: Heading2, action: () => insertLinePrefix('## ', '二级标题') },
        { label: '三级标题', short: 'H3', icon: Heading3, action: () => insertLinePrefix('### ', '三级标题') },
      ],
    },
    {
      label: '样式',
      items: [
        { label: '加粗', icon: Bold, action: () => insertInline('**', '**', '加粗文字') },
        { label: '斜体', icon: Italic, action: () => insertInline('*', '*', '斜体文字') },
        { label: '行内代码', icon: Code2, action: () => insertInline('`', '`', 'code') },
      ],
    },
    {
      label: '列表',
      items: [
        { label: '无序列表', icon: List, action: () => insertLinePrefix('- ', '列表项') },
        { label: '有序列表', icon: ListOrdered, action: () => insertLinePrefix('1. ', '列表项') },
        { label: '引用', icon: Quote, action: () => insertLinePrefix('> ', '引用内容') },
      ],
    },
    {
      label: '插入',
      items: [
        { label: '代码块', icon: Code2, action: () => insertBlock('```js\nconsole.log(\"hello\");\n```\n', 6) },
        { label: '链接', icon: LinkIcon, action: () => insertInline('[', '](https://example.com)', '链接文字') },
        { label: '图片', icon: ImagePlus, action: () => insertInline('![', '](https://example.com/image.png)', '图片描述') },
        { label: '分割线', icon: Minus, action: () => insertBlock('---\n') },
      ],
    },
  ];

  if (initializing) {
    return (
      <div className="mx-auto max-w-3xl px-6 py-24">
        <div className="rounded-[2rem] border border-white/10 bg-slate-950/60 p-10 text-center text-slate-400">
          正在同步你的账号信息...
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="mx-auto max-w-3xl px-6 py-24">
        <div className="rounded-[2rem] border border-white/10 bg-slate-950/60 p-10 text-center shadow-2xl shadow-purple-950/20">
          <p className="text-sm font-semibold uppercase tracking-[0.35em] text-purple-300">Login Required</p>
          <h1 className="mt-4 text-3xl font-black text-white">登录后再写文章</h1>
          <p className="mt-3 text-slate-400">登录后可以发布博客文章、选择分类标签，并在博客页展示。</p>
          <Link
            to="/login?redirect=/blog/new"
            className="mt-8 inline-flex items-center justify-center rounded-2xl bg-gradient-to-r from-purple-600 to-blue-500 px-6 py-3 font-semibold text-white shadow-lg shadow-purple-900/30 transition hover:scale-[1.02]"
          >
            去登录
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-6xl px-6 py-16">
      <Link
        to="/blog"
        className="mb-8 inline-flex items-center gap-2 text-sm text-slate-400 transition-colors hover:text-white"
      >
        <ArrowLeft className="h-4 w-4" />
        返回博客
      </Link>

      <div className="mb-10">
        <p className="mb-3 text-sm font-semibold uppercase tracking-[0.35em] text-purple-300">Blog</p>
        <h1 className="text-4xl font-black text-white md:text-5xl">写一篇文章</h1>
        <p className="mt-4 max-w-2xl text-lg text-slate-400">
          用 Markdown 整理一篇更正式的文章。发布后会出现在博客列表，后台仍然可以继续管理。
        </p>
      </div>

      <div className="rounded-[2rem] border border-white/10 bg-slate-950/60 p-6 shadow-2xl shadow-purple-950/20 md:p-8">
        <div className="grid gap-5 md:grid-cols-2">
          <div>
            <label className="mb-2 block text-sm font-medium text-slate-300">分类</label>
            <select
              value={categoryId}
              onChange={(event) => setCategoryId(event.target.value)}
              className="w-full rounded-2xl border border-white/10 bg-black/20 px-4 py-3 text-sm text-slate-300 outline-none transition focus:border-purple-400 [&>option]:bg-slate-950 [&>option]:text-slate-100"
            >
              <option value="" className="bg-slate-950 text-slate-100">选择分类...</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id} className="bg-slate-950 text-slate-100">
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="mb-2 block text-sm font-medium text-slate-300">封面图</label>
            <div className="grid gap-3 sm:grid-cols-[1fr_auto]">
              <div className="relative">
                <ImageIcon className="absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
                <input
                  value={coverImage}
                  onChange={(event) => setCoverImage(event.target.value)}
                  placeholder="可粘贴 URL，也可以右侧上传"
                  className="w-full rounded-2xl border border-white/10 bg-black/20 py-3 pl-11 pr-4 text-sm text-slate-300 outline-none transition focus:border-purple-400"
                />
              </div>
              <button
                type="button"
                disabled={coverUploading}
                onClick={() => coverInputRef.current?.click()}
                className="inline-flex items-center justify-center gap-2 rounded-2xl border border-purple-400/40 bg-purple-500/15 px-4 py-3 text-sm font-semibold text-purple-100 transition hover:bg-purple-500/25 disabled:cursor-not-allowed disabled:opacity-60"
              >
                <Upload className="h-4 w-4" />
                {coverUploading ? '上传中' : '本地上传'}
              </button>
              <input
                ref={coverInputRef}
                type="file"
                accept="image/png,image/jpeg,image/webp,image/gif"
                onChange={handleCoverUpload}
                className="hidden"
              />
            </div>
            {coverImage && (
              <div className="mt-3 overflow-hidden rounded-2xl border border-white/10 bg-black/20">
                <img src={coverImage} alt="封面预览" className="h-32 w-full object-cover" />
              </div>
            )}
          </div>
        </div>

        <div className="mt-6">
          <label className="mb-2 block text-sm font-medium text-slate-300">标题</label>
          <input
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="请输入文章标题"
            className="w-full rounded-2xl border border-white/10 bg-black/20 px-5 py-4 text-white outline-none transition focus:border-purple-400"
          />
        </div>

        <div className="mt-6">
          <label className="mb-2 block text-sm font-medium text-slate-300">摘要</label>
          <textarea
            value={summary}
            onChange={(event) => setSummary(event.target.value)}
            placeholder="简单说明这篇文章在讲什么，也可以留空自动截取正文。"
            className="h-24 w-full resize-none rounded-2xl border border-white/10 bg-black/20 px-5 py-4 text-white outline-none transition placeholder:text-slate-600 focus:border-purple-400"
          />
        </div>

        <div className="mt-6">
          <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
            <label className="block text-sm font-medium text-slate-300">标签</label>
            <span className="text-xs text-slate-500">
              已选 {selectedTagIds.length}/{MAX_SELECTED_TAGS}
            </span>
          </div>
          <div className="flex flex-wrap gap-3">
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
                  className={`rounded-full border px-4 py-2 text-sm font-medium transition ${
                    active
                      ? 'border-purple-400 bg-purple-500/25 text-white shadow-lg shadow-purple-900/25'
                      : 'border-white/10 bg-white/5 text-slate-400 hover:border-purple-300/60 hover:text-white'
                  } ${disabled ? 'cursor-not-allowed opacity-40 hover:border-white/10 hover:text-slate-400' : ''}`}
                >
                  {tag.name}
                </button>
              );
            })}
            {!loadingMeta && tags.length === 0 && (
              <span className="text-sm text-slate-500">暂无可选标签。</span>
            )}
          </div>
        </div>

        <div className="mt-6">
          <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
            <label className="block text-sm font-medium text-slate-300">正文</label>
            <div className="flex rounded-full border border-white/10 bg-white/5 p-1 lg:hidden">
              <button
                type="button"
                onClick={() => setPreviewMode('write')}
                className={`inline-flex items-center gap-1 rounded-full px-3 py-1.5 text-xs transition ${
                  previewMode === 'write' ? 'bg-purple-500/30 text-white' : 'text-slate-400'
                }`}
              >
                <Edit3 className="h-3.5 w-3.5" />
                编辑
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

          <div className="mb-3 flex flex-wrap items-center gap-2 rounded-2xl border border-white/10 bg-black/20 p-2">
            {toolbarGroups.map((group) => (
              <div
                key={group.label}
                className="inline-flex items-center gap-1 rounded-xl border border-white/10 bg-white/[0.03] p-1"
              >
                <span className="hidden px-2 text-[11px] font-medium text-slate-500 sm:inline">
                  {group.label}
                </span>
                {group.items.map((item) => {
                  const Icon = item.icon;
                  return (
                    <button
                      key={item.label}
                      type="button"
                      onClick={item.action}
                      title={item.label}
                      className="inline-flex h-8 min-w-8 items-center justify-center rounded-lg px-2 text-xs font-semibold text-slate-300 transition hover:bg-purple-500/20 hover:text-white"
                    >
                      {'short' in item && item.short ? (
                        <span>{item.short}</span>
                      ) : (
                        <Icon className="h-4 w-4" />
                      )}
                    </button>
                  );
                })}
              </div>
            ))}
          </div>

          <div className="grid grid-cols-1 gap-4 lg:grid-cols-[minmax(0,1.05fr)_minmax(320px,0.95fr)]">
            <div className={previewMode === 'preview' ? 'hidden lg:block' : 'block'}>
              <textarea
                ref={textareaRef}
                value={content}
                onChange={(event) => setContent(event.target.value)}
                placeholder="在这里写正文，支持 Markdown..."
                className="h-80 w-full resize-y rounded-2xl border border-purple-500/40 bg-black/20 px-5 py-4 text-white outline-none transition placeholder:text-slate-600 focus:border-purple-400 lg:h-[460px]"
              />
            </div>

            <div className={previewMode === 'write' ? 'hidden lg:block' : 'block'}>
              <div className="min-h-80 rounded-2xl border border-white/10 bg-white/[0.03] p-5 lg:min-h-[460px]">
                <div className="mb-4 flex items-center justify-between border-b border-white/10 pb-3">
                  <span className="inline-flex items-center gap-2 text-sm font-medium text-slate-300">
                    <Eye className="h-4 w-4 text-purple-300" />
                    实时预览
                  </span>
                  <span className="text-xs text-slate-500">{content.length} 字符</span>
                </div>

                {content.trim() ? (
                  <article className="prose prose-invert max-w-none prose-headings:text-white prose-p:text-slate-300 prose-a:text-purple-300 prose-strong:text-white prose-code:text-purple-200 prose-pre:border prose-pre:border-white/10 prose-pre:bg-black/30">
                    <ReactMarkdown remarkPlugins={[remarkGfm]}>{content}</ReactMarkdown>
                  </article>
                ) : (
                  <div className="flex min-h-60 flex-col items-center justify-center rounded-2xl border border-dashed border-white/10 text-center text-slate-500">
                    <Edit3 className="mb-3 h-8 w-8 text-purple-300/70" />
                    <p className="font-medium text-slate-400">预览会显示在这里</p>
                    <p className="mt-1 text-sm">写一点 Markdown，就能看到发布后的效果。</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

        {error && (
          <div className="mt-6 rounded-2xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-200">
            {error}
          </div>
        )}

        <div className="mt-8 flex flex-col gap-3 sm:flex-row sm:justify-end">
          <Link
            to="/blog"
            className="inline-flex items-center justify-center rounded-2xl border border-white/10 px-6 py-3 font-semibold text-slate-300 transition hover:border-white/20 hover:text-white"
          >
            取消
          </Link>
          <button
            type="button"
            onClick={publishArticle}
            disabled={saving || loadingMeta}
            className="inline-flex items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-purple-600 to-blue-500 px-8 py-3 font-semibold text-white shadow-lg shadow-purple-900/30 transition hover:scale-[1.01] disabled:cursor-not-allowed disabled:opacity-60 disabled:hover:scale-100"
          >
            {saving ? '发布中...' : '发布文章'}
            <Send className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
