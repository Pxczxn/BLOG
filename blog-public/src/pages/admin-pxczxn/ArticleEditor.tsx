import { useEffect, useMemo, useRef, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import toast from 'react-hot-toast';
import {
  ArrowLeft,
  Bold,
  Clock3,
  Code2,
  Edit3,
  ExternalLink,
  Eye,
  Heading1,
  Image as ImageIcon,
  Italic,
  List,
  ListOrdered,
  Quote,
  RotateCcw,
  Save,
  Send,
  Upload,
} from 'lucide-react';
import request, { getStaticUrl } from '../../lib/request';
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
const AUTOSAVE_INTERVAL_MS = 1200;
const ACCEPTED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];
const ACCEPTED_IMAGE_EXTENSIONS = '.jpg,.jpeg,.png,.webp,.gif';

const emptyForm: ArticleForm = {
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: '',
  status: 'DRAFT',
};

export default function ArticleEditor() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const coverInputRef = useRef<HTMLInputElement | null>(null);
  const hydratedRef = useRef(false);

  const [form, setForm] = useState<ArticleForm>(emptyForm);
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [tags, setTags] = useState<TagOption[]>([]);
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const [previewMode, setPreviewMode] = useState<'write' | 'preview'>('write');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [lastAutoSavedAt, setLastAutoSavedAt] = useState<string | null>(null);
  const [hasLocalDraft, setHasLocalDraft] = useState(false);
  const [savedSlug, setSavedSlug] = useState<string | null>(null);

  const draftKey = useMemo(() => `pxczxn_admin_article_draft_${id || 'new'}`, [id]);
  const previewUrl = savedSlug ? `/post/${savedSlug}` : null;

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
      if (!isEdit) {
        hydratedRef.current = true;
        return;
      }

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
        setSavedSlug(data.slug || null);
        setSelectedTagIds(Array.isArray(data.tagIds) ? data.tagIds.map(Number) : []);
      } catch {
        if (mounted) {
          setError('获取文章详情失败');
        }
      } finally {
        if (mounted) {
          setLoading(false);
          hydratedRef.current = true;
        }
      }
    }

    const localDraft = window.localStorage.getItem(draftKey);
    setHasLocalDraft(Boolean(localDraft));
    fetchDeps();
    fetchArticle();

    return () => {
      mounted = false;
    };
  }, [draftKey, id, isEdit]);

  useEffect(() => {
    if (!hydratedRef.current) return;
    const timer = window.setTimeout(() => {
      const hasContent = form.title.trim() || form.summary.trim() || form.content.trim() || form.coverImage.trim() || selectedTagIds.length;
      if (!hasContent) return;

      window.localStorage.setItem(
        draftKey,
        JSON.stringify({
          form,
          selectedTagIds,
          savedAt: new Date().toISOString(),
        }),
      );
      setLastAutoSavedAt(new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }));
      setHasLocalDraft(true);
    }, AUTOSAVE_INTERVAL_MS);

    return () => window.clearTimeout(timer);
  }, [draftKey, form, selectedTagIds]);

  const setField = <K extends keyof ArticleForm>(key: K, value: ArticleForm[K]) => {
    setForm((current) => ({ ...current, [key]: value }));
  };

  const restoreLocalDraft = () => {
    const raw = window.localStorage.getItem(draftKey);
    if (!raw) return;
    try {
      const parsed = JSON.parse(raw);
      setForm({ ...emptyForm, ...(parsed.form || {}) });
      setSelectedTagIds(Array.isArray(parsed.selectedTagIds) ? parsed.selectedTagIds.map(Number) : []);
      toast.success('已恢复本地草稿');
    } catch {
      toast.error('本地草稿读取失败');
    }
  };

  const clearLocalDraft = () => {
    window.localStorage.removeItem(draftKey);
    setHasLocalDraft(false);
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

  const insertImageMarkdown = (url: string, alt = '图片') => {
    const textarea = textareaRef.current;
    const start = textarea?.selectionStart ?? form.content.length;
    const end = textarea?.selectionEnd ?? form.content.length;
    const imageMarkdown = `\n![${alt}](${url})\n`;
    const nextValue = `${form.content.slice(0, start)}${imageMarkdown}${form.content.slice(end)}`;
    const cursor = start + imageMarkdown.length;
    updateContentWithSelection(nextValue, cursor, cursor);
  };

  const uploadImage = async (file: File, target: 'content' | 'cover') => {
    if (!ACCEPTED_IMAGE_TYPES.includes(file.type)) {
      toast.error('仅支持 JPG、PNG、WebP、GIF 图片');
      return;
    }

    const payload = new FormData();
    payload.append('file', file);
    payload.append('dir', target === 'cover' ? 'covers' : 'misc');

    try {
      setUploading(true);
      const res: any = await request.post('/api/admin/upload', payload, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      const data = res?.data ?? res;
      const url = data?.url;
      if (!url) {
        throw new Error('上传接口没有返回图片地址');
      }
      if (target === 'cover') {
        setField('coverImage', url);
      } else {
        insertImageMarkdown(url, file.name.replace(/\.[^.]+$/, '') || '图片');
      }
      toast.success('图片已上传');
    } catch (err: any) {
      toast.error(err?.message || '图片上传失败');
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
      if (coverInputRef.current) coverInputRef.current.value = '';
    }
  };

  const toggleTag = (tagId: number) => {
    setSelectedTagIds((current) => {
      if (current.includes(tagId)) {
        return current.filter((item) => item !== tagId);
      }
      if (current.length >= MAX_SELECTED_TAGS) {
        toast.error(`最多选择 ${MAX_SELECTED_TAGS} 个标签`);
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
        const res: any = await request.put(`/api/admin/articles/${id}`, payload);
        const data = res?.data ?? res;
        setSavedSlug(data?.slug || savedSlug);
      } else {
        const res: any = await request.post('/api/admin/articles', payload);
        const data = res?.data ?? res;
        savedArticleId = String(data.id);
        setSavedSlug(data?.slug || null);
      }

      if (savedArticleId) {
        await request.put(`/api/admin/articles/${savedArticleId}/tags`, {
          tagIds: selectedTagIds,
        });
      }

      clearLocalDraft();
      toast.success(forceStatus === 'PUBLISHED' ? '文章已发布' : '草稿已保存');
      navigate('/admin-pxczxn/articles');
    } catch (err: any) {
      setError(err?.message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  const toolbarItems = [
    { label: '一级标题', icon: Heading1, action: () => insertLinePrefix('# ', '一级标题') },
    { label: '加粗', icon: Bold, action: () => insertInline('**', '**', '加粗文字') },
    { label: '斜体', icon: Italic, action: () => insertInline('*', '*', '斜体文字') },
    { label: '无序列表', icon: List, action: () => insertLinePrefix('- ', '列表项') },
    { label: '有序列表', icon: ListOrdered, action: () => insertLinePrefix('1. ', '列表项') },
    { label: '引用', icon: Quote, action: () => insertLinePrefix('> ', '引用内容') },
    { label: '代码', icon: Code2, action: () => insertInline('`', '`', 'code') },
  ];

  const seoDescription = form.summary.trim() || form.content.replace(/[#>*_~`\-\[\]()`]/g, ' ').replace(/\s+/g, ' ').trim().slice(0, 160);
  const seoTitleLength = form.title.trim().length;
  const seoDescriptionLength = seoDescription.length;

  if (loading) {
    return <div className="text-white">加载中...</div>;
  }

  return (
    <div className="mx-auto max-w-6xl space-y-6">
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
          <div className="flex flex-wrap items-center gap-3">
            {previewUrl ? (
              <Link
                to={previewUrl}
                target="_blank"
                className="flex items-center gap-2 rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-white transition-all hover:bg-white/10"
              >
                <ExternalLink className="h-4 w-4" /> 预览文章
              </Link>
            ) : null}
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

      {error ? (
        <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-400">
          {error}
        </div>
      ) : null}

      <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_320px]">
        <div className="space-y-6">
          <section className="rounded-2xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-md">
            <div className="mb-5 flex flex-wrap items-center justify-between gap-3 rounded-xl border border-white/10 bg-black/20 px-4 py-3 text-xs text-slate-400">
              <span className="inline-flex items-center gap-2">
                <Clock3 className="h-4 w-4 text-purple-300" />
                {lastAutoSavedAt ? `已自动保存到本地：${lastAutoSavedAt}` : '输入后会自动保存到本地草稿'}
              </span>
              {hasLocalDraft ? (
                <button
                  type="button"
                  onClick={restoreLocalDraft}
                  className="inline-flex items-center gap-1 rounded-lg border border-white/10 px-2.5 py-1.5 text-slate-200 transition hover:bg-white/10"
                >
                  <RotateCcw className="h-3.5 w-3.5" />
                  恢复草稿
                </button>
              ) : null}
            </div>

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
                <label className="mb-1.5 block text-xs font-medium text-slate-400">封面大图</label>
                <div className="flex flex-wrap items-center gap-3">
                  <input
                    ref={coverInputRef}
                    type="file"
                    accept={ACCEPTED_IMAGE_EXTENSIONS}
                    className="hidden"
                    onChange={(event) => {
                      const file = event.target.files?.[0];
                      if (file) uploadImage(file, 'cover');
                    }}
                  />
                  <button
                    type="button"
                    onClick={() => coverInputRef.current?.click()}
                    disabled={uploading}
                    className="inline-flex items-center gap-2 rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-200 transition hover:bg-white/10 disabled:opacity-50"
                  >
                    <Upload className="h-4 w-4" />
                    {form.coverImage ? '更换图片' : '上传图片'}
                  </button>
                  <span className="text-xs text-slate-500">支持 JPG、PNG、WebP、GIF</span>
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
                {tags.length === 0 ? <span className="text-sm text-slate-500">暂无可选标签</span> : null}
              </div>
            </div>

            <div className="mt-5">
              <label className="mb-1.5 block text-xs font-medium text-slate-400">文章摘要</label>
              <textarea
                value={form.summary}
                onChange={(event) => setField('summary', event.target.value)}
                rows={3}
                maxLength={500}
                className="w-full resize-none rounded-lg border border-white/10 bg-black/20 px-3 py-2 text-sm text-slate-300 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
                placeholder="建议 80-160 字；留空则自动提取正文。"
              />
            </div>

            {form.coverImage ? (
              <div className="mt-5 h-40 overflow-hidden rounded-2xl border border-white/10">
                <img src={getStaticUrl(form.coverImage)} alt="封面预览" className="h-full w-full object-cover" />
              </div>
            ) : null}
          </section>

          <section className="rounded-2xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-md">
            <input
              value={form.title}
              onChange={(event) => setField('title', event.target.value)}
              placeholder="输入文章标题..."
              maxLength={200}
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
                <input
                  ref={fileInputRef}
                  type="file"
                  accept={ACCEPTED_IMAGE_EXTENSIONS}
                  className="hidden"
                  onChange={(event) => {
                    const file = event.target.files?.[0];
                    if (file) uploadImage(file, 'content');
                  }}
                />
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  disabled={uploading}
                  title="上传图片并插入正文"
                  className="inline-flex h-9 items-center gap-2 rounded-xl px-3 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white disabled:opacity-50"
                >
                  <ImageIcon className="h-4 w-4" />
                  <span className="hidden sm:inline">{uploading ? '上传中' : '图片'}</span>
                </button>
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
                placeholder="输入文章正文，支持 Markdown。可以用工具栏插入标题、列表、引用、代码和图片。"
                className="h-[560px] w-full resize-y rounded-xl border border-white/10 bg-black/20 p-4 font-mono text-sm leading-relaxed text-slate-300 placeholder-slate-600 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
              />
            ) : (
              <div className="min-h-[560px] rounded-xl border border-white/10 bg-black/20 p-6">
                {form.content.trim() ? (
                  <article className="prose prose-invert max-w-none prose-headings:text-white prose-p:text-slate-300 prose-a:text-purple-300 prose-strong:text-white prose-code:text-purple-200 prose-pre:border prose-pre:border-white/10 prose-pre:bg-black/30">
                    <ReactMarkdown remarkPlugins={[remarkGfm]}>{form.content}</ReactMarkdown>
                  </article>
                ) : (
                  <div className="flex min-h-[500px] flex-col items-center justify-center text-center text-slate-500">
                    <Eye className="mb-3 h-8 w-8 text-purple-300/70" />
                    <p className="font-medium text-slate-400">还没有可预览的内容</p>
                    <p className="mt-1 text-sm">写一点 Markdown，再切到预览看看效果。</p>
                  </div>
                )}
              </div>
            )}
          </section>
        </div>

        <aside className="space-y-6">
          <section className="rounded-2xl border border-white/5 bg-white/[0.02] p-5 backdrop-blur-md">
            <h2 className="mb-4 text-sm font-semibold text-white">SEO 辅助</h2>
            <div className="space-y-4 text-sm">
              <Metric label="标题长度" value={`${seoTitleLength}/60`} healthy={seoTitleLength > 0 && seoTitleLength <= 60} />
              <Metric label="描述长度" value={`${seoDescriptionLength}/160`} healthy={seoDescriptionLength >= 40 && seoDescriptionLength <= 160} />
              <Metric label="封面图片" value={form.coverImage ? '已设置' : '未设置'} healthy={Boolean(form.coverImage)} />
              <Metric label="分类标签" value={`${form.categoryId ? 1 : 0} 类 / ${selectedTagIds.length} 标签`} healthy={Boolean(form.categoryId) && selectedTagIds.length > 0} />
            </div>
            <div className="mt-5 rounded-xl border border-white/10 bg-black/20 p-4">
              <div className="line-clamp-1 text-sm text-blue-300">{form.title || '文章标题会显示在这里'}</div>
              <div className="mt-1 text-xs text-emerald-400">pxczxn.top/post/{savedSlug || '自动生成-slug'}</div>
              <p className="mt-2 line-clamp-3 text-xs leading-5 text-slate-400">
                {seoDescription || '摘要或正文前 160 字会作为搜索描述。'}
              </p>
            </div>
          </section>

          <section className="rounded-2xl border border-white/5 bg-white/[0.02] p-5 text-sm text-slate-400 backdrop-blur-md">
            <h2 className="mb-3 text-sm font-semibold text-white">发布建议</h2>
            <ul className="space-y-2 leading-6">
              <li>先写清楚标题和摘要，让首页卡片更容易被点开。</li>
              <li>每篇文章建议至少选择一个分类和一到三个标签。</li>
              <li>正文图片可直接上传插入，封面图会用于列表和分享预览。</li>
              <li>未发布前本地草稿会自动保存，刷新页面也能恢复。</li>
            </ul>
          </section>
        </aside>
      </div>
    </div>
  );
}

function Metric({ label, value, healthy }: { label: string; value: string; healthy: boolean }) {
  return (
    <div className="flex items-center justify-between gap-3">
      <span className="text-slate-500">{label}</span>
      <span className={healthy ? 'text-emerald-300' : 'text-amber-300'}>{value}</span>
    </div>
  );
}
