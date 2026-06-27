import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { format } from 'date-fns';
import toast from 'react-hot-toast';
import {
  CheckSquare,
  Copy,
  Edit,
  Eye,
  FileText,
  Filter,
  MessageSquare,
  Plus,
  RefreshCcw,
  Search,
  Send,
  Square,
  Trash2,
} from 'lucide-react';
import request, { getStaticUrl } from '../../lib/request';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

type ArticleStatus = 'PUBLISHED' | 'DRAFT';

type ArticleItem = {
  id: number;
  title: string;
  slug?: string;
  summary?: string;
  coverImage?: string;
  status: ArticleStatus;
  viewCount?: number;
  commentCount?: number;
  createdAt?: string;
  publishedAt?: string;
  category?: {
    id?: number;
    name?: string;
  };
};

type SortMode = 'created_desc' | 'views_desc' | 'title_asc';

const statusMeta: Record<'ALL' | ArticleStatus, { label: string; badge: string; dot: string }> = {
  ALL: {
    label: '全部文章',
    badge: 'border-white/10 bg-white/5 text-slate-300',
    dot: 'bg-slate-400',
  },
  PUBLISHED: {
    label: '已发布',
    badge: 'border-emerald-500/30 bg-emerald-500/10 text-emerald-300',
    dot: 'bg-emerald-400',
  },
  DRAFT: {
    label: '草稿',
    badge: 'border-amber-500/30 bg-amber-500/10 text-amber-300',
    dot: 'bg-amber-400',
  },
};

export default function ArticleManage() {
  const [articles, setArticles] = useState<ArticleItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keywordInput, setKeywordInput] = useState('');
  const [statusInput, setStatusInput] = useState<'ALL' | ArticleStatus>('ALL');
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState<'ALL' | ArticleStatus>('ALL');
  const [sortMode, setSortMode] = useState<SortMode>('created_desc');
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [busy, setBusy] = useState(false);

  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  const fetchArticles = async () => {
    try {
      setLoading(true);
      const res: any = await request.get('/api/admin/articles', {
        params: {
          page,
          size: pageSize,
          keyword: keyword || undefined,
          status: status === 'ALL' ? undefined : status,
        },
      });
      const data = res?.data ?? res;
      setArticles(data.items || []);
      setTotal(data.total || 0);
      setSelectedIds([]);
    } catch {
      setArticles([]);
      toast.error('文章列表加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchArticles();
  }, [page, pageSize, keyword, status]);

  const sortedArticles = useMemo(() => {
    const items = [...articles];
    if (sortMode === 'views_desc') {
      return items.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0));
    }
    if (sortMode === 'title_asc') {
      return items.sort((a, b) => a.title.localeCompare(b.title, 'zh-CN'));
    }
    return items.sort((a, b) => {
      const bTime = new Date(b.createdAt || b.publishedAt || 0).getTime();
      const aTime = new Date(a.createdAt || a.publishedAt || 0).getTime();
      return bTime - aTime;
    });
  }, [articles, sortMode]);

  const pageStats = useMemo(() => {
    const published = articles.filter((article) => article.status === 'PUBLISHED').length;
    const drafts = articles.filter((article) => article.status === 'DRAFT').length;
    const views = articles.reduce((sum, article) => sum + (article.viewCount || 0), 0);
    return { published, drafts, views };
  }, [articles]);

  const selectedArticles = articles.filter((article) => selectedIds.includes(article.id));
  const allSelected = sortedArticles.length > 0 && sortedArticles.every((article) => selectedIds.includes(article.id));

  const applyFilters = () => {
    setPage(1);
    setKeyword(keywordInput.trim());
    setStatus(statusInput);
  };

  const clearFilters = () => {
    setKeywordInput('');
    setStatusInput('ALL');
    setKeyword('');
    setStatus('ALL');
    setSortMode('created_desc');
    setPage(1);
  };

  const toggleAll = () => {
    if (allSelected) {
      setSelectedIds([]);
      return;
    }
    setSelectedIds(sortedArticles.map((article) => article.id));
  };

  const toggleOne = (id: number) => {
    setSelectedIds((current) => (current.includes(id) ? current.filter((item) => item !== id) : [...current, id]));
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('确定删除这篇文章吗？')) return;
    try {
      await request.delete(`/api/admin/articles/${id}`);
      toast.success('文章已删除');
      fetchArticles();
    } catch {
      toast.error('删除失败');
      fetchArticles();
    }
  };

  const changeArticleStatus = async (id: number, targetStatus: ArticleStatus) => {
    const action = targetStatus === 'PUBLISHED' ? 'publish' : 'draft';
    await request.put(`/api/admin/articles/${id}/${action}`);
  };

  const handleStatusChange = async (article: ArticleItem) => {
    try {
      await changeArticleStatus(article.id, article.status === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED');
      toast.success(article.status === 'PUBLISHED' ? '已退回草稿' : '已发布');
      fetchArticles();
    } catch {
      toast.error('状态更新失败');
      fetchArticles();
    }
  };

  const runBulkStatus = async (targetStatus: ArticleStatus) => {
    if (selectedIds.length === 0) return;
    try {
      setBusy(true);
      await Promise.all(selectedIds.map((id) => changeArticleStatus(id, targetStatus)));
      toast.success(targetStatus === 'PUBLISHED' ? '已批量发布' : '已批量退回草稿');
      fetchArticles();
    } catch {
      toast.error('批量操作失败，已刷新列表');
      fetchArticles();
    } finally {
      setBusy(false);
    }
  };

  const runBulkDelete = async () => {
    if (selectedIds.length === 0 || !window.confirm(`确定删除选中的 ${selectedIds.length} 篇文章吗？`)) return;
    try {
      setBusy(true);
      await Promise.all(selectedIds.map((id) => request.delete(`/api/admin/articles/${id}`)));
      toast.success('已批量删除');
      fetchArticles();
    } catch {
      toast.error('批量删除失败，已刷新列表');
      fetchArticles();
    } finally {
      setBusy(false);
    }
  };

  const copyArticleLink = async (article: ArticleItem) => {
    if (!article.slug) {
      toast.error('这篇文章还没有访问地址');
      return;
    }
    await navigator.clipboard.writeText(`${window.location.origin}/post/${article.slug}`);
    toast.success('文章链接已复制');
  };

  return (
    <div className="animate-in fade-in space-y-6 duration-500">
      <AdminPageHeader
        title="文章管理"
        actions={
          <div className="flex flex-wrap items-center gap-3">
            <Link
              to="/admin-pxczxn/articles/comments"
              className="flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-200 backdrop-blur-md transition-all hover:bg-white/10 hover:text-white"
            >
              <MessageSquare className="h-4 w-4" />
              评论管理
            </Link>
            <Link
              to="/admin-pxczxn/articles/new"
              className="flex items-center gap-2 rounded-xl border-none bg-gradient-to-r from-purple-600 to-blue-600 px-4 py-2 text-sm font-medium text-white shadow-[0_0_20px_rgba(168,85,247,0.3)] transition-all hover:scale-105 active:scale-95"
            >
              <Plus className="h-4 w-4" />
              写文章
            </Link>
          </div>
        }
      />

      <section className="grid gap-4 md:grid-cols-4">
        <MetricCard label="当前页文章" value={articles.length} tone="purple" />
        <MetricCard label="已发布" value={pageStats.published} tone="emerald" />
        <MetricCard label="草稿" value={pageStats.drafts} tone="amber" />
        <MetricCard label="当前页阅读" value={pageStats.views} tone="blue" />
      </section>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[320px_minmax(0,1fr)]">
        <aside className="h-fit space-y-4 xl:sticky xl:top-6">
          <section className="rounded-2xl border border-white/5 bg-slate-900/40 p-5 shadow-2xl backdrop-blur-xl">
            <div className="mb-5 flex items-center justify-between">
              <div className="flex items-center gap-2 text-white">
                <Filter className="h-4 w-4 text-purple-400" />
                <h2 className="font-bold">筛选与排序</h2>
              </div>
              <button
                type="button"
                onClick={clearFilters}
                className="flex items-center gap-1 text-xs text-slate-500 transition-colors hover:text-white"
              >
                <RefreshCcw className="h-3 w-3" />
                重置
              </button>
            </div>

            <div className="space-y-5">
              <div>
                <label className="mb-2 block text-xs font-semibold text-slate-500">关键词搜索</label>
                <div className="group relative">
                  <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500 transition-colors group-focus-within:text-purple-400" />
                  <input
                    type="text"
                    value={keywordInput}
                    onChange={(event) => setKeywordInput(event.target.value)}
                    onKeyDown={(event) => {
                      if (event.key === 'Enter') applyFilters();
                    }}
                    placeholder="搜索标题或摘要..."
                    className="w-full rounded-xl border border-white/10 bg-black/20 py-2.5 pl-10 pr-4 text-sm text-white placeholder-slate-600 transition-all focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
                  />
                </div>
              </div>

              <div>
                <label className="mb-2 block text-xs font-semibold text-slate-500">发布状态</label>
                <div className="grid gap-2">
                  {(['ALL', 'PUBLISHED', 'DRAFT'] as const).map((value) => {
                    const meta = statusMeta[value];
                    const active = statusInput === value;
                    return (
                      <button
                        key={value}
                        type="button"
                        onClick={() => setStatusInput(value)}
                        className={`flex items-center justify-between rounded-xl border px-4 py-2.5 text-sm transition-all ${
                          active
                            ? 'border-purple-500/50 bg-purple-500/10 text-white shadow-[0_0_15px_rgba(168,85,247,0.1)]'
                            : 'border-white/5 bg-white/[0.02] text-slate-400 hover:border-white/10 hover:bg-white/5'
                        }`}
                      >
                        <span>{meta.label}</span>
                        <span className={`h-1.5 w-1.5 rounded-full ${active ? 'bg-purple-400' : meta.dot}`} />
                      </button>
                    );
                  })}
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <label className="text-xs font-semibold text-slate-500">
                  每页
                  <select
                    value={pageSize}
                    onChange={(event) => {
                      setPage(1);
                      setPageSize(Number(event.target.value));
                    }}
                    className="mt-2 w-full rounded-xl border border-white/10 bg-black/20 px-3 py-2 text-sm text-slate-200 focus:border-purple-500/50 focus:outline-none"
                  >
                    <option value={10}>10 篇</option>
                    <option value={20}>20 篇</option>
                    <option value={50}>50 篇</option>
                  </select>
                </label>
                <label className="text-xs font-semibold text-slate-500">
                  排序
                  <select
                    value={sortMode}
                    onChange={(event) => setSortMode(event.target.value as SortMode)}
                    className="mt-2 w-full rounded-xl border border-white/10 bg-black/20 px-3 py-2 text-sm text-slate-200 focus:border-purple-500/50 focus:outline-none"
                  >
                    <option value="created_desc">最新创建</option>
                    <option value="views_desc">阅读最高</option>
                    <option value="title_asc">标题 A-Z</option>
                  </select>
                </label>
              </div>

              <button
                type="button"
                onClick={applyFilters}
                className="w-full rounded-xl border border-white/10 bg-white/5 py-2.5 text-sm font-bold text-white transition-all hover:bg-white/10 active:scale-[0.98]"
              >
                应用筛选
              </button>
            </div>
          </section>

          <section className="rounded-2xl border border-white/5 bg-white/[0.02] p-5 backdrop-blur-md">
            <div className="flex items-center justify-between text-sm">
              <span className="text-slate-400">查询结果</span>
              <span className="font-mono font-bold text-purple-300">{total} 篇</span>
            </div>
          </section>
        </aside>

        <div className="space-y-4">
          <section className="rounded-2xl border border-white/5 bg-slate-900/40 shadow-2xl backdrop-blur-xl">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-white/5 px-5 py-4">
              <button
                type="button"
                onClick={toggleAll}
                className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
              >
                {allSelected ? <CheckSquare className="h-4 w-4" /> : <Square className="h-4 w-4" />}
                已选 {selectedIds.length}
              </button>

              <div className="flex flex-wrap items-center gap-2">
                <button
                  type="button"
                  disabled={busy || selectedIds.length === 0}
                  onClick={() => runBulkStatus('PUBLISHED')}
                  className="inline-flex items-center gap-2 rounded-xl border border-emerald-500/20 bg-emerald-500/10 px-3 py-2 text-sm text-emerald-300 transition hover:bg-emerald-500/20 disabled:opacity-40"
                >
                  <Send className="h-4 w-4" />
                  批量发布
                </button>
                <button
                  type="button"
                  disabled={busy || selectedIds.length === 0}
                  onClick={() => runBulkStatus('DRAFT')}
                  className="inline-flex items-center gap-2 rounded-xl border border-amber-500/20 bg-amber-500/10 px-3 py-2 text-sm text-amber-300 transition hover:bg-amber-500/20 disabled:opacity-40"
                >
                  <FileText className="h-4 w-4" />
                  退回草稿
                </button>
                <button
                  type="button"
                  disabled={busy || selectedIds.length === 0}
                  onClick={runBulkDelete}
                  className="inline-flex items-center gap-2 rounded-xl border border-red-500/20 bg-red-500/10 px-3 py-2 text-sm text-red-300 transition hover:bg-red-500/20 disabled:opacity-40"
                >
                  <Trash2 className="h-4 w-4" />
                  批量删除
                </button>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full min-w-[940px] border-collapse text-left">
                <thead>
                  <tr className="border-b border-white/5 bg-white/[0.02] text-[10px] uppercase tracking-[0.2em] text-slate-500">
                    <th className="w-12 px-5 py-4" />
                    <th className="px-5 py-4 font-bold">文章</th>
                    <th className="px-5 py-4 font-bold">分类</th>
                    <th className="px-5 py-4 text-center font-bold">状态</th>
                    <th className="px-5 py-4 text-center font-bold">阅读</th>
                    <th className="px-5 py-4 font-bold">时间</th>
                    <th className="px-5 py-4 text-right font-bold">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/[0.03] text-sm">
                  {loading ? (
                    <tr>
                      <td colSpan={7} className="px-6 py-20 text-center">
                        <RefreshCcw className="mx-auto mb-4 h-8 w-8 animate-spin text-purple-500/30" />
                        <span className="text-xs uppercase tracking-widest text-slate-600">数据加载中</span>
                      </td>
                    </tr>
                  ) : sortedArticles.length > 0 ? (
                    sortedArticles.map((article) => {
                      const selected = selectedIds.includes(article.id);
                      const meta = statusMeta[article.status];
                      return (
                        <tr key={article.id} className={`group transition-colors ${selected ? 'bg-purple-500/5' : 'hover:bg-white/[0.02]'}`}>
                          <td className="px-5 py-4">
                            <button
                              type="button"
                              onClick={() => toggleOne(article.id)}
                              className="text-slate-500 transition hover:text-white"
                            >
                              {selected ? <CheckSquare className="h-4 w-4 text-purple-300" /> : <Square className="h-4 w-4" />}
                            </button>
                          </td>
                          <td className="px-5 py-4">
                            <div className="flex items-center gap-3">
                              <div className="h-12 w-16 overflow-hidden rounded-xl border border-white/10 bg-white/5">
                                {article.coverImage ? (
                                  <img src={getStaticUrl(article.coverImage)} alt="" className="h-full w-full object-cover" />
                                ) : (
                                  <div className="flex h-full w-full items-center justify-center text-slate-600">
                                    <FileText className="h-5 w-5" />
                                  </div>
                                )}
                              </div>
                              <div className="min-w-0">
                                <p className="max-w-[300px] truncate font-bold text-slate-200 transition-colors group-hover:text-purple-300" title={article.title}>
                                  {article.title}
                                </p>
                                <p className="mt-1 max-w-[300px] truncate font-mono text-[10px] uppercase tracking-tight text-slate-600">
                                  {article.slug || `article-${article.id}`}
                                </p>
                              </div>
                            </div>
                          </td>
                          <td className="px-5 py-4">
                            <span className="inline-flex items-center rounded-lg border border-purple-500/20 bg-purple-500/5 px-2 py-0.5 text-[10px] font-bold text-purple-300">
                              {article.category?.name || '未分类'}
                            </span>
                          </td>
                          <td className="px-5 py-4">
                            <div className="flex justify-center">
                              <button
                                type="button"
                                onClick={() => handleStatusChange(article)}
                                className={`rounded-full border px-2.5 py-1 text-[10px] font-black uppercase tracking-wider transition-all ${meta.badge}`}
                              >
                                {meta.label}
                              </button>
                            </div>
                          </td>
                          <td className="px-5 py-4 text-center">
                            <div className="flex flex-col items-center">
                              <span className="font-bold text-white">{article.viewCount || 0}</span>
                              <span className="text-[10px] font-bold uppercase text-slate-600">Views</span>
                            </div>
                          </td>
                          <td className="px-5 py-4">
                            <span className="font-mono text-xs text-slate-500">
                              {article.createdAt ? format(new Date(article.createdAt), 'yyyy/MM/dd HH:mm') : '--'}
                            </span>
                          </td>
                          <td className="px-5 py-4 text-right">
                            <div className="flex justify-end gap-2 opacity-60 transition-opacity group-hover:opacity-100">
                              {article.slug && article.status === 'PUBLISHED' ? (
                                <Link
                                  to={`/post/${article.slug}`}
                                  target="_blank"
                                  className="rounded-lg p-2 text-slate-500 transition-colors hover:bg-white/5 hover:text-blue-300"
                                  title="预览"
                                >
                                  <Eye className="h-4 w-4" />
                                </Link>
                              ) : null}
                              <button
                                type="button"
                                onClick={() => copyArticleLink(article)}
                                className="rounded-lg p-2 text-slate-500 transition-colors hover:bg-white/5 hover:text-emerald-300"
                                title="复制链接"
                              >
                                <Copy className="h-4 w-4" />
                              </button>
                              <Link
                                to={`/admin-pxczxn/articles/edit/${article.id}`}
                                className="rounded-lg p-2 text-slate-500 transition-colors hover:bg-white/5 hover:text-purple-300"
                                title="编辑"
                              >
                                <Edit className="h-4 w-4" />
                              </Link>
                              <button
                                type="button"
                                onClick={() => handleDelete(article.id)}
                                className="rounded-lg p-2 text-slate-500 transition-colors hover:bg-white/5 hover:text-red-300"
                                title="删除"
                              >
                                <Trash2 className="h-4 w-4" />
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan={7} className="px-6 py-20 text-center">
                        <div className="flex flex-col items-center gap-3">
                          <div className="rounded-full border border-white/5 bg-white/[0.02] p-4">
                            <FileText className="h-10 w-10 text-slate-700" />
                          </div>
                          <p className="text-sm font-bold uppercase tracking-widest text-slate-500">没有找到文章</p>
                        </div>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            <div className="border-t border-white/5 bg-white/[0.01] px-5 py-4">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <p className="text-[10px] font-bold uppercase tracking-widest text-slate-600">
                  第 {page} / {totalPages} 页
                  {selectedArticles.length > 0 ? ` · 已选 ${selectedArticles.length} 篇` : ''}
                </p>
                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    disabled={page <= 1}
                    onClick={() => setPage((current) => Math.max(1, current - 1))}
                    className="rounded-lg border border-white/5 bg-white/5 px-3 py-1 text-xs font-bold text-slate-400 transition-all hover:bg-white/10 hover:text-white disabled:opacity-30"
                  >
                    上一页
                  </button>
                  <button
                    type="button"
                    disabled={page >= totalPages}
                    onClick={() => setPage((current) => Math.min(totalPages, current + 1))}
                    className="rounded-lg border border-white/5 bg-white/5 px-3 py-1 text-xs font-bold text-slate-400 transition-all hover:bg-white/10 hover:text-white disabled:opacity-30"
                  >
                    下一页
                  </button>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}

function MetricCard({ label, value, tone }: { label: string; value: number; tone: 'purple' | 'emerald' | 'amber' | 'blue' }) {
  const toneClass = {
    purple: 'text-purple-300 bg-purple-500/10 border-purple-500/20',
    emerald: 'text-emerald-300 bg-emerald-500/10 border-emerald-500/20',
    amber: 'text-amber-300 bg-amber-500/10 border-amber-500/20',
    blue: 'text-blue-300 bg-blue-500/10 border-blue-500/20',
  }[tone];

  return (
    <div className={`rounded-2xl border p-4 backdrop-blur-md ${toneClass}`}>
      <p className="text-xs text-slate-400">{label}</p>
      <p className="mt-2 text-2xl font-black text-white">{value.toLocaleString()}</p>
    </div>
  );
}
