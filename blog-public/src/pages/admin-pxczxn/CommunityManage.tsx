/**
 * 社区管理页
 * <p>
 * 社区帖子的管理与状态控制
 */
import { useState, useEffect } from 'react';
import { Search, Users, Eye, EyeOff, Trash2, Edit, MessageSquare, Filter, RefreshCcw } from 'lucide-react';
import request from '../../lib/request';
import { format } from 'date-fns';
import { Link } from 'react-router-dom';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

export default function CommunityManage() {
  const [posts, setPosts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const res: any = await request.get('/api/admin/community/posts', {
        params: { page, size: 10, status: status || undefined }
      });
      const data = res?.data ?? res;
      setPosts(data.items || []);
      setTotal(data.total || 0);
    } catch (error) {
      // Error handled silently
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, [page, status]);

  const handleStatusChange = async (id: number, currentStatus: string) => {
    const newStatus = currentStatus === 'HIDDEN' ? 'PUBLISHED' : 'HIDDEN';
    try {
      await request.put(`/api/admin/community/posts/${id}/status`, { status: newStatus });
      fetchPosts();
    } catch (error) {
      // Error handled silently
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要彻底删除该帖子吗？')) return;
    try {
      await request.delete(`/api/admin/community/posts/${id}`);
      fetchPosts();
    } catch (error) {
      // Error handled silently
    }
  };

  const statusMeta = {
    '': { label: '全部状态', color: 'slate' },
    PUBLISHED: { label: '已发布', color: 'emerald' },
    PENDING_REVIEW: { label: '待审核', color: 'yellow' },
    HIDDEN: { label: '已隐藏', color: 'orange' },
    REJECTED: { label: '已拒绝', color: 'red' },
  } as const;

  const StatusBadge = ({ status }: { status: string }) => {
    switch (status) {
      case 'PUBLISHED': return <span className="px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-[10px] font-black uppercase">Live</span>;
      case 'PENDING_REVIEW': return <span className="px-2 py-0.5 rounded-full bg-yellow-500/10 text-yellow-400 border border-yellow-500/20 text-[10px] font-black uppercase">Review</span>;
      case 'HIDDEN': return <span className="px-2 py-0.5 rounded-full bg-orange-500/10 text-orange-400 border border-orange-500/20 text-[10px] font-black uppercase">Hidden</span>;
      case 'REJECTED': return <span className="px-2 py-0.5 rounded-full bg-red-500/10 text-red-400 border border-red-500/20 text-[10px] font-black uppercase">Rejected</span>;
      default: return <span className="px-2 py-0.5 rounded-full bg-slate-500/10 text-slate-400 border border-slate-500/20 text-[10px] font-black uppercase">{status}</span>;
    }
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <AdminPageHeader
        title="社区帖子管理"
        actions={
          <Link
            to="/admin-pxczxn/community/comments"
            className="flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-200 transition-all hover:bg-white/10 hover:text-white backdrop-blur-md"
          >
            <MessageSquare className="h-4 w-4" />
            评论管理
          </Link>
        }
      />

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[300px_minmax(0,1fr)]">
        {/* 筛选侧边栏 */}
        <aside className="lg:sticky lg:top-6 h-fit space-y-4">
          <div className="rounded-3xl border border-white/5 bg-slate-900/40 p-6 backdrop-blur-xl shadow-2xl">
            <div className="mb-6 flex items-center justify-between">
              <div className="flex items-center gap-2 text-white">
                <Filter className="h-4 w-4 text-blue-400" />
                <h2 className="font-bold">查询过滤</h2>
              </div>
              <button
                type="button"
                onClick={() => { setStatus(''); setPage(1); }}
                className="text-xs text-slate-500 hover:text-white flex items-center gap-1 transition-colors"
              >
                <RefreshCcw className="h-3 w-3" />
                重置
              </button>
            </div>

            <div className="space-y-6">
              <div>
                <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-slate-500">内容搜索</label>
                <div className="relative group">
                  <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500 group-focus-within:text-blue-400 transition-colors" />
                  <input
                    type="text"
                    placeholder="标题关键词..."
                    className="w-full rounded-xl border border-white/10 bg-black/20 py-2.5 pl-10 pr-4 text-sm text-white placeholder-slate-600 focus:border-blue-500/50 focus:outline-none focus:ring-1 focus:ring-blue-500/50 transition-all"
                  />
                </div>
              </div>

              <div>
                <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-slate-500">帖子状态</label>
                <div className="grid grid-cols-1 gap-2">
                  {(['', 'PUBLISHED', 'PENDING_REVIEW', 'HIDDEN', 'REJECTED'] as const).map((value) => {
                    const meta = statusMeta[value];
                    const active = status === value;
                    return (
                      <button
                        key={value || 'all'}
                        type="button"
                        onClick={() => { setStatus(value); setPage(1); }}
                        className={`flex items-center justify-between rounded-xl border px-4 py-2.5 text-sm transition-all ${
                          active
                            ? 'border-blue-500/50 bg-blue-500/10 text-white shadow-[0_0_15px_rgba(59,130,246,0.1)]'
                            : 'border-white/5 bg-white/[0.02] text-slate-400 hover:bg-white/5 hover:border-white/10'
                        }`}
                      >
                        <span>{meta.label}</span>
                        {active && <div className="h-1.5 w-1.5 rounded-full bg-blue-500 shadow-[0_0_8px_rgba(59,130,246,0.8)]" />}
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>
          </div>

          <div className="rounded-2xl border border-white/5 bg-blue-500/5 p-4 backdrop-blur-md text-sm flex items-center justify-between">
            <span className="text-slate-400 font-medium">总计帖子</span>
            <span className="font-mono text-blue-400 font-bold">{total} <span className="text-[10px] text-slate-600">POSTS</span></span>
          </div>
        </aside>

        {/* 内容列表 */}
        <div className="space-y-4">
          <section className="rounded-3xl border border-white/5 bg-slate-900/40 shadow-2xl backdrop-blur-xl overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-white/5 bg-white/[0.02] text-[10px] uppercase tracking-[0.2em] text-slate-500">
                    <th className="px-6 py-4 font-bold">主题内容</th>
                    <th className="px-6 py-4 font-bold text-center">发布者</th>
                    <th className="px-6 py-4 font-bold text-center">状态</th>
                    <th className="px-6 py-4 font-bold text-center">交互统计</th>
                    <th className="px-6 py-4 font-bold">创建日期</th>
                    <th className="px-6 py-4 font-bold text-right">管理</th>
                  </tr>
                </thead>
                <tbody className="text-sm divide-y divide-white/[0.03]">
                  {loading ? (
                    <tr>
                      <td colSpan={6} className="px-6 py-20 text-center">
                        <RefreshCcw className="h-8 w-8 text-blue-500/20 animate-spin mx-auto mb-4" />
                        <span className="text-slate-600 text-xs tracking-widest uppercase">Fetching Data</span>
                      </td>
                    </tr>
                  ) : posts.length > 0 ? (
                    posts.map((post) => (
                      <tr key={post.id} className="group hover:bg-white/[0.02] transition-colors">
                        <td className="px-6 py-4">
                          <div className="font-bold text-slate-200 group-hover:text-blue-300 transition-colors truncate max-w-[220px]" title={post.title}>
                            {post.title}
                          </div>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <span className="text-slate-400 font-medium">{post.author?.username || 'Guest'}</span>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <StatusBadge status={post.status} />
                        </td>
                        <td className="px-6 py-4 text-center">
                          <div className="inline-flex gap-3 text-[10px] font-bold uppercase tracking-tighter">
                            <div className="flex flex-col items-center">
                              <span className="text-white">{post.likeCount || 0}</span>
                              <span className="text-slate-600">Likes</span>
                            </div>
                            <div className="h-6 w-px bg-white/5" />
                            <div className="flex flex-col items-center">
                              <span className="text-white">{post.commentCount || 0}</span>
                              <span className="text-slate-600">Reply</span>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <span className="text-xs text-slate-500 font-mono">
                            {post.createdAt ? format(new Date(post.createdAt), 'yyyy/MM/dd HH:mm') : '--'}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex justify-end gap-2 opacity-40 group-hover:opacity-100 transition-opacity">
                            <button 
                              onClick={() => handleStatusChange(post.id, post.status)}
                              className="p-2 rounded-lg hover:bg-white/5 text-slate-500 hover:text-orange-400 transition-colors" 
                              title={post.status === 'HIDDEN' ? '显示' : '隐藏'}
                            >
                              {post.status === 'HIDDEN' ? <Eye className="h-4 w-4" /> : <EyeOff className="h-4 w-4" />}
                            </button>
                            <button 
                              onClick={() => handleDelete(post.id)}
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
                      <td colSpan={6} className="px-6 py-20 text-center text-slate-500">
                        <div className="flex flex-col items-center gap-3">
                          <div className="p-4 rounded-full bg-white/[0.02] border border-white/5 text-slate-700">
                            <Users className="h-10 w-10" />
                          </div>
                          <p className="text-xs font-bold tracking-widest uppercase">No Community Posts</p>
                        </div>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            <div className="border-t border-white/5 bg-white/[0.01] px-6 py-4">
              <div className="flex items-center justify-between">
                <p className="text-[10px] font-bold text-slate-600 uppercase tracking-widest">
                  Page {page} Index
                </p>
                <div className="flex items-center gap-2">
                  <button
                    disabled={page <= 1}
                    onClick={() => setPage(p => p - 1)}
                    className="px-3 py-1 text-xs font-bold rounded-lg border border-white/5 bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white disabled:opacity-30 transition-all"
                  >
                    PREV
                  </button>
                  <button
                    disabled={posts.length < 10}
                    onClick={() => setPage(p => p + 1)}
                    className="px-3 py-1 text-xs font-bold rounded-lg border border-white/5 bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white disabled:opacity-30 transition-all"
                  >
                    NEXT
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
