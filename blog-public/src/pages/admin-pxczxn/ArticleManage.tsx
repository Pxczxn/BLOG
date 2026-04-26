/**
 * 文章管理页
 * <p>
 * 文章的增删改查和发布管理
 */
import { useState, useEffect } from 'react';
import { Plus, Search, Edit, Trash2, Eye } from 'lucide-react';
import { format } from 'date-fns';
import request from '../../lib/request';
import { Link } from 'react-router-dom';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

export default function ArticleManage() {
  const [articles, setArticles] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [keywordInput, setKeywordInput] = useState('');
  const [statusInput, setStatusInput] = useState('');
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState('');

  const fetchArticles = async () => {
    try {
      setLoading(true);
      const res: any = await request.get('/api/admin/articles', {
        params: { page, size: 10, keyword, status }
      });
      const data = res?.data ?? res;
      setArticles(data.items || []);
      setTotal(data.total || 0);
    } catch (error) {
      setArticles([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchArticles();
  }, [page, keyword, status]);

  const applyFilters = () => {
    setPage(1);
    setKeyword(keywordInput.trim());
    setStatus(statusInput);
  };

  const clearFilters = () => {
    setKeywordInput('');
    setStatusInput('');
    setPage(1);
    setKeyword('');
    setStatus('');
  };

  const statusMeta = {
    '': { label: '全部状态', className: 'border-white/10 bg-white/5 text-slate-300' },
    PUBLISHED: { label: '已发布', className: 'border-emerald-500/30 bg-emerald-500/15 text-emerald-300' },
    DRAFT: { label: '草稿', className: 'border-orange-500/30 bg-orange-500/15 text-orange-300' },
  } as const;

  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要删除这篇文章吗？')) return;
    try {
      await request.delete(`/api/admin/articles/${id}`);
      fetchArticles();
    } catch (error) {
      // 删除失败，刷新列表
      fetchArticles();
    }
  };

  const handleStatusChange = async (id: number, currentStatus: string) => {
    const action = currentStatus === 'PUBLISHED' ? 'draft' : 'publish';
    try {
      await request.put(`/api/admin/articles/${id}/${action}`);
      fetchArticles();
    } catch (error) {
      // 状态更新失败，刷新列表
      fetchArticles();
    }
  };

  return (
    <div className="space-y-6">
      <AdminPageHeader
        title="文章管理"
        actions={
          <Link
            to="/admin-pxczxn/articles/new"
            className="flex items-center gap-2 rounded-xl border-none bg-gradient-to-r from-purple-600 to-blue-600 px-4 py-2 text-sm font-medium text-white shadow-[0_0_15px_rgba(168,85,247,0.4)] transition-all hover:from-purple-500 hover:to-blue-500"
          >
            <Plus className="h-4 w-4" />
            写文章
          </Link>
        }
      />

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[320px_minmax(0,1fr)]">
        <aside className="lg:sticky lg:top-6 h-fit rounded-3xl border border-white/5 bg-slate-950/45 p-6 shadow-[0_16px_45px_rgba(0,0,0,0.18)]">
          <div className="mb-6 flex items-center justify-between">
            <div>
              <h2 className="text-lg font-semibold text-white">筛选文章</h2>
              <p className="mt-1 text-sm text-slate-500">左侧调整条件，右侧查看结果</p>
            </div>
            <button
              type="button"
              onClick={clearFilters}
              className="rounded-full border border-white/10 bg-white/5 px-3 py-1.5 text-sm text-slate-300 transition-colors hover:bg-white/10 hover:text-white"
            >
              清空筛选
            </button>
          </div>

          <div className="space-y-6">
            <label className="block">
              <span className="mb-3 block text-sm font-medium text-slate-300">关键词</span>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-white/40" />
                <input
                  type="text"
                  value={keywordInput}
                  onChange={(e) => setKeywordInput(e.target.value)}
                  placeholder="搜索文章标题..."
                  className="w-full rounded-2xl border border-white/10 bg-white/5 py-3 pl-9 pr-4 text-sm text-white placeholder-slate-500 transition-all focus:border-purple-500/50 focus:outline-none"
                />
              </div>
            </label>

            <div>
              <span className="mb-3 block text-sm font-medium text-slate-300">状态</span>
              <div className="grid grid-cols-1 gap-3">
                {(['', 'PUBLISHED', 'DRAFT'] as const).map((value) => {
                  const meta = statusMeta[value];
                  const active = statusInput === value;
                  return (
                    <button
                      key={value || 'all'}
                      type="button"
                      onClick={() => setStatusInput(value)}
                      className={`flex items-center justify-between rounded-2xl border px-4 py-3 text-left text-sm transition-all ${
                        active
                          ? 'border-purple-500/50 bg-purple-500/15 text-white shadow-[0_0_0_1px_rgba(168,85,247,0.15)]'
                          : 'border-white/10 bg-white/5 text-slate-300 hover:bg-white/10'
                      }`}
                    >
                      <span>{meta.label}</span>
                      <span className={`rounded-full border px-2 py-0.5 text-xs ${meta.className}`}>筛选</span>
                    </button>
                  );
                })}
              </div>
            </div>

            <button
              type="button"
              onClick={applyFilters}
              className="flex w-full items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-purple-600 to-blue-600 px-4 py-3 text-sm font-medium text-white shadow-[0_0_15px_rgba(168,85,247,0.25)] transition-all hover:from-purple-500 hover:to-blue-500"
            >
              应用筛选
            </button>

            <div className="rounded-2xl border border-white/5 bg-black/20 p-4">
              <div className="mb-3 text-sm font-medium text-slate-300">当前筛选</div>
              <div className="space-y-3 text-sm text-slate-400">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-slate-500">关键词</span>
                  <span className="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-slate-200">
                    {keyword || '全部'}
                  </span>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-slate-500">状态</span>
                  <span className={`rounded-full border px-3 py-1 ${statusMeta[status as keyof typeof statusMeta].className}`}>
                    {statusMeta[status as keyof typeof statusMeta].label}
                  </span>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-slate-500">结果</span>
                  <span className="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-slate-200">
                    共 {total} 条
                  </span>
                </div>
              </div>
            </div>
          </div>
        </aside>

        <section className="rounded-3xl border border-white/5 bg-slate-950/45 shadow-[0_16px_45px_rgba(0,0,0,0.18)] overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                  <th className="px-6 py-4 font-medium">标题</th>
                  <th className="px-6 py-4 font-medium">分类</th>
                  <th className="px-6 py-4 font-medium">状态</th>
                  <th className="px-6 py-4 font-medium">阅读</th>
                  <th className="px-6 py-4 font-medium">创建时间</th>
                  <th className="px-6 py-4 font-medium text-right">操作</th>
                </tr>
              </thead>
              <tbody className="text-sm divide-y divide-white/5">
                {loading ? (
                  <tr>
                    <td colSpan={6} className="px-6 py-16 text-center text-slate-500">加载中...</td>
                  </tr>
                ) : articles.length > 0 ? (
                  articles.map((article: any) => (
                    <tr key={article.id} className="hover:bg-white/5 transition-colors">
                      <td className="px-6 py-4">
                        <div className="max-w-[240px] truncate font-medium text-white" title={article.title}>{article.title}</div>
                        <div className="mt-1 text-xs text-slate-500">{article.slug}</div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="rounded border border-purple-500/30 bg-purple-500/20 px-2 py-1 text-xs text-purple-300">
                          {article.category?.name || '无分类'}
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <button
                          onClick={() => handleStatusChange(article.id, article.status)}
                          className={`cursor-pointer rounded border px-2 py-1 text-xs transition-opacity hover:opacity-80 ${
                            article.status === 'PUBLISHED'
                              ? 'border-emerald-500/30 bg-emerald-500/20 text-emerald-300'
                              : 'border-orange-500/30 bg-orange-500/20 text-orange-300'
                          }`}
                        >
                          {article.status === 'PUBLISHED' ? '已发布' : '草稿'}
                        </button>
                      </td>
                      <td className="px-6 py-4 text-slate-300">{article.viewCount || 0}</td>
                      <td className="px-6 py-4 text-slate-400">
                        {article.createdAt ? format(new Date(article.createdAt), 'yyyy-MM-dd HH:mm') : '-'}
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex justify-end gap-3">
                          <button className="text-slate-400 transition-colors hover:text-purple-400" title="查看">
                            <Eye className="h-4 w-4" />
                          </button>
                          <Link to={`/admin-pxczxn/articles/edit/${article.id}`} className="text-slate-400 transition-colors hover:text-purple-400" title="编辑">
                            <Edit className="h-4 w-4" />
                          </Link>
                          <button
                            onClick={() => handleDelete(article.id)}
                            className="text-slate-400 transition-colors hover:text-red-400"
                            title="删除"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                      <div className="flex flex-col items-center justify-center">
                        <EmptyArticleIcon className="mb-3 h-12 w-12 text-slate-700" />
                        <p>暂无数据</p>
                      </div>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="border-t border-white/10 px-6 py-4">
            <div className="flex items-center justify-between gap-4">
              <div className="text-sm text-slate-500">共 {total} 条记录</div>
              <div className="flex gap-1">
                <button
                  disabled={page <= 1}
                  onClick={() => setPage(p => p - 1)}
                  className="flex h-8 w-8 shrink-0 items-center justify-center rounded border border-white/5 bg-white/5 text-slate-500 transition-colors hover:bg-white/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
                >&lt;</button>
                <button className="flex h-8 w-8 shrink-0 items-center justify-center rounded border-none bg-gradient-to-r from-purple-600 to-blue-600 text-white shadow-[0_0_10px_rgba(168,85,247,0.4)]">
                  {page}
                </button>
                <button
                  disabled={articles.length < 10}
                  onClick={() => setPage(p => p + 1)}
                  className="flex h-8 w-8 shrink-0 items-center justify-center rounded border border-white/5 bg-white/5 text-slate-500 transition-colors hover:bg-white/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
                >&gt;</button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

// 空状态本地图标
function EmptyArticleIcon({ className }: { className?: string }) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
      <path d="M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z"/>
      <path d="M14 2v4a2 2 0 0 0 2 2h4"/>
      <path d="M10 9H8"/>
      <path d="M16 13H8"/>
      <path d="M16 17H8"/>
    </svg>
  );
}
