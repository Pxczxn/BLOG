/**
 * 文章管理页
 * <p>
 * 文章的增删改查和发布管理
 */
import { useState, useEffect } from 'react';
import { Plus, Search, Edit, Trash2, Eye, MessageSquare, Filter, RefreshCcw } from 'lucide-react';
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
    '': { label: '全部状态', color: 'slate' },
    PUBLISHED: { label: '已发布', color: 'emerald' },
    DRAFT: { label: '草稿', color: 'orange' },
  } as const;

  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要删除这篇文章吗？')) return;
    try {
      await request.delete(`/api/admin/articles/${id}`);
      fetchArticles();
    } catch (error) {
      fetchArticles();
    }
  };

  const handleStatusChange = async (id: number, currentStatus: string) => {
    const action = currentStatus === 'PUBLISHED' ? 'draft' : 'publish';
    try {
      await request.put(`/api/admin/articles/${id}/${action}`);
      fetchArticles();
    } catch (error) {
      fetchArticles();
    }
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <AdminPageHeader
        title="文章管理"
        actions={
          <div className="flex items-center gap-3">
            <Link
              to="/admin-pxczxn/articles/comments"
              className="flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-200 transition-all hover:bg-white/10 hover:text-white backdrop-blur-md"
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

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[300px_minmax(0,1fr)]">
        {/* 筛选侧边栏 */}
        <aside className="lg:sticky lg:top-6 h-fit space-y-4">
          <div className="rounded-3xl border border-white/5 bg-slate-900/40 p-6 backdrop-blur-xl shadow-2xl">
            <div className="mb-6 flex items-center justify-between">
              <div className="flex items-center gap-2 text-white">
                <Filter className="h-4 w-4 text-purple-400" />
                <h2 className="font-bold">条件筛选</h2>
              </div>
              <button
                type="button"
                onClick={clearFilters}
                className="text-xs text-slate-500 hover:text-white flex items-center gap-1 transition-colors"
              >
                <RefreshCcw className="h-3 w-3" />
                重置
              </button>
            </div>

            <div className="space-y-6">
              <div>
                <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-slate-500">关键词搜索</label>
                <div className="relative group">
                  <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500 group-focus-within:text-purple-400 transition-colors" />
                  <input
                    type="text"
                    value={keywordInput}
                    onChange={(e) => setKeywordInput(e.target.value)}
                    placeholder="输入标题搜索..."
                    className="w-full rounded-xl border border-white/10 bg-black/20 py-2.5 pl-10 pr-4 text-sm text-white placeholder-slate-600 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50 transition-all"
                  />
                </div>
              </div>

              <div>
                <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-slate-500">发布状态</label>
                <div className="flex flex-col gap-2">
                  {(['', 'PUBLISHED', 'DRAFT'] as const).map((value) => {
                    const meta = statusMeta[value];
                    const active = statusInput === value;
                    return (
                      <button
                        key={value || 'all'}
                        type="button"
                        onClick={() => setStatusInput(value)}
                        className={`flex items-center justify-between rounded-xl border px-4 py-2.5 text-sm transition-all ${
                          active
                            ? 'border-purple-500/50 bg-purple-500/10 text-white shadow-[0_0_15px_rgba(168,85,247,0.1)]'
                            : 'border-white/5 bg-white/[0.02] text-slate-400 hover:bg-white/5 hover:border-white/10'
                        }`}
                      >
                        <span>{meta.label}</span>
                        {active && <div className="h-1.5 w-1.5 rounded-full bg-purple-500 shadow-[0_0_8px_rgba(168,85,247,0.8)]" />}
                      </button>
                    );
                  })}
                </div>
              </div>

              <button
                type="button"
                onClick={applyFilters}
                className="w-full rounded-xl bg-white/5 border border-white/10 py-2.5 text-sm font-bold text-white hover:bg-white/10 transition-all active:scale-[0.98]"
              >
                应用筛选
              </button>
            </div>
          </div>

          {/* 结果统计 */}
          <div className="rounded-2xl border border-white/5 bg-purple-500/5 p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-sm">
              <span className="text-slate-400">查询结果</span>
              <span className="font-mono text-purple-400 font-bold">{total} <span className="text-[10px] text-slate-600">ITEMS</span></span>
            </div>
          </div>
        </aside>

        {/* 内容区域 */}
        <div className="space-y-4">
          <section className="rounded-3xl border border-white/5 bg-slate-900/40 shadow-2xl backdrop-blur-xl overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-white/5 bg-white/[0.02] text-[10px] uppercase tracking-[0.2em] text-slate-500">
                    <th className="px-6 py-4 font-bold">文章信息</th>
                    <th className="px-6 py-4 font-bold">分类标签</th>
                    <th className="px-6 py-4 font-bold text-center">状态</th>
                    <th className="px-6 py-4 font-bold text-center">数据</th>
                    <th className="px-6 py-4 font-bold">发布时间</th>
                    <th className="px-6 py-4 font-bold text-right">操作</th>
                  </tr>
                </thead>
                <tbody className="text-sm divide-y divide-white/[0.03]">
                  {loading ? (
                    <tr>
                      <td colSpan={6} className="px-6 py-20 text-center">
                        <RefreshCcw className="h-8 w-8 text-purple-500/20 animate-spin mx-auto mb-4" />
                        <span className="text-slate-600 text-xs tracking-widest uppercase">数据加载中</span>
                      </td>
                    </tr>
                  ) : articles.length > 0 ? (
                    articles.map((article: any) => (
                      <tr key={article.id} className="group hover:bg-white/[0.02] transition-colors">
                        <td className="px-6 py-4">
                          <div className="flex flex-col gap-0.5">
                            <span className="font-bold text-slate-200 group-hover:text-purple-300 transition-colors truncate max-w-[200px]" title={article.title}>
                              {article.title}
                            </span>
                            <span className="font-mono text-[10px] text-slate-600 uppercase tracking-tighter truncate max-w-[200px]">
                              {article.slug}
                            </span>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <span className="inline-flex items-center rounded-lg border border-purple-500/20 bg-purple-500/5 px-2 py-0.5 text-[10px] font-bold text-purple-400">
                            {article.category?.name || '未分类'}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex justify-center">
                            <button
                              onClick={() => handleStatusChange(article.id, article.status)}
                              className={`px-2.5 py-1 rounded-full text-[10px] font-black uppercase tracking-wider transition-all border ${
                                article.status === 'PUBLISHED'
                                  ? 'border-emerald-500/30 bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20'
                                  : 'border-orange-500/30 bg-orange-500/10 text-orange-400 hover:bg-orange-500/20'
                              }`}
                            >
                              {article.status === 'PUBLISHED' ? 'Live' : 'Draft'}
                            </button>
                          </div>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <div className="flex flex-col items-center">
                            <span className="text-white font-bold">{article.viewCount || 0}</span>
                            <span className="text-[10px] text-slate-600 uppercase font-bold">Views</span>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <span className="text-xs text-slate-500 font-mono">
                            {article.createdAt ? format(new Date(article.createdAt), 'yyyy/MM/dd HH:mm') : '--'}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex justify-end gap-2 opacity-40 group-hover:opacity-100 transition-opacity">
                            <button className="p-2 rounded-lg hover:bg-white/5 text-slate-500 hover:text-blue-400 transition-colors" title="预览">
                              <Eye className="h-4 w-4" />
                            </button>
                            <Link 
                              to={`/admin-pxczxn/articles/edit/${article.id}`} 
                              className="p-2 rounded-lg hover:bg-white/5 text-slate-500 hover:text-purple-400 transition-colors" 
                              title="编辑"
                            >
                              <Edit className="h-4 w-4" />
                            </Link>
                            <button
                              onClick={() => handleDelete(article.id)}
                              className="p-2 rounded-lg hover:bg-white/5 text-slate-500 hover:text-red-400 transition-colors"
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
                      <td colSpan={6} className="px-6 py-20 text-center">
                        <div className="flex flex-col items-center gap-3">
                          <div className="p-4 rounded-full bg-white/[0.02] border border-white/5">
                            <EmptyArticleIcon className="h-10 w-10 text-slate-700" />
                          </div>
                          <p className="text-slate-500 text-sm font-bold tracking-widest uppercase">No Articles Found</p>
                        </div>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* 底部分页 */}
            <div className="border-t border-white/5 bg-white/[0.01] px-6 py-4">
              <div className="flex items-center justify-between">
                <p className="text-[10px] font-bold text-slate-600 uppercase tracking-widest">
                  Showing page {page} of {Math.ceil(total / 10) || 1}
                </p>
                <div className="flex items-center gap-2">
                  <button
                    disabled={page <= 1}
                    onClick={() => setPage(p => p - 1)}
                    className="px-3 py-1 text-xs font-bold rounded-lg border border-white/5 bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white disabled:opacity-30 transition-all"
                  >
                    Prev
                  </button>
                  <div className="h-6 w-px bg-white/5" />
                  <button
                    disabled={articles.length < 10}
                    onClick={() => setPage(p => p + 1)}
                    className="px-3 py-1 text-xs font-bold rounded-lg border border-white/5 bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white disabled:opacity-30 transition-all"
                  >
                    Next
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
