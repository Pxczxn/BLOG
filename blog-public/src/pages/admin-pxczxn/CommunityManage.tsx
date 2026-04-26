




import { useState, useEffect } from 'react';
import { Search, Users, Eye, EyeOff, Trash2, Edit } from 'lucide-react';
import request from '../../lib/request';
import { format } from 'date-fns';
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
      
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要彻底删除该帖子吗？')) return;
    try {
      await request.delete(`/api/admin/community/posts/${id}`);
      fetchPosts();
    } catch (error) {
      
    }
  };

  const StatusBadge = ({ status }: { status: string }) => {
    switch (status) {
      case 'PUBLISHED': return <span className="px-2 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 text-xs">已发布</span>;
      case 'PENDING_REVIEW': return <span className="px-2 py-1 rounded bg-yellow-500/20 text-yellow-300 border border-yellow-500/30 text-xs">待审核</span>;
      case 'HIDDEN': return <span className="px-2 py-1 rounded bg-orange-500/20 text-orange-300 border border-orange-500/30 text-xs">已隐藏</span>;
      case 'REJECTED': return <span className="px-2 py-1 rounded bg-red-500/20 text-red-300 border border-red-500/30 text-xs">已拒绝</span>;
      default: return <span className="px-2 py-1 rounded bg-slate-500/20 text-slate-300 border border-slate-500/30 text-xs">{status}</span>;
    }
  };

  return (
    <div className="space-y-6">
      <AdminPageHeader title="社区帖子管理" />

      <div className="bg-slate-950/45 border border-white/5 rounded-3xl p-4 shadow-[0_16px_45px_rgba(0,0,0,0.18)] flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4 items-center">
          <select 
            value={status} 
            onChange={e => { setStatus(e.target.value); setPage(1); }}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 appearance-none min-w-[120px]"
          >
            <option value="">全部状态</option>
            <option value="PUBLISHED">已发布</option>
            <option value="PENDING_REVIEW">待审核</option>
            <option value="HIDDEN">已隐藏</option>
            <option value="REJECTED">已拒绝</option>
          </select>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
            <input 
              type="text" 
              placeholder="搜索帖子标题..."
              className="py-2 pl-9 pr-4 rounded-lg bg-white/5 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 transition-all text-white placeholder-slate-500 w-64"
            />
          </div>
        </div>
      </div>

      <div className="bg-slate-950/45 border border-white/5 rounded-3xl shadow-[0_16px_45px_rgba(0,0,0,0.18)] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">标题</th>
                <th className="px-6 py-4 font-medium">作者</th>
                <th className="px-6 py-4 font-medium">状态</th>
                <th className="px-6 py-4 font-medium">交互数据</th>
                <th className="px-6 py-4 font-medium">发布时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">加载中...</td>
                </tr>
              ) : posts.length > 0 ? (
                posts.map((post) => (
                  <tr key={post.id} className="hover:bg-white/5 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-medium text-white max-w-[200px] truncate" title={post.title}>{post.title}</div>
                    </td>
                    <td className="px-6 py-4 text-slate-300">
                      {post.author?.username || '匿名'}
                    </td>
                    <td className="px-6 py-4">
                      <StatusBadge status={post.status} />
                    </td>
                    <td className="px-6 py-4 text-slate-400 text-xs">
                      赞: {post.likeCount || 0} • 评: {post.commentCount || 0}
                    </td>
                    <td className="px-6 py-4 text-slate-400">
                      {post.createdAt ? format(new Date(post.createdAt), 'yyyy-MM-dd HH:mm') : '-'}
                    </td>
                    <td className="px-6 py-4 text-right">
                       <div className="flex justify-end gap-3">
                         <button 
                           onClick={() => handleStatusChange(post.id, post.status)}
                           className="text-slate-400 hover:text-orange-400 transition-colors" 
                           title={post.status === 'HIDDEN' ? '设为公开' : '隐藏'}
                         >
                           {post.status === 'HIDDEN' ? <Eye className="w-4 h-4" /> : <EyeOff className="w-4 h-4" />}
                         </button>
                         <button 
                           onClick={() => handleDelete(post.id)}
                           className="text-slate-400 hover:text-red-400 transition-colors" title="删除">
                           <Trash2 className="w-4 h-4" />
                         </button>
                       </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <Users className="w-12 h-12 text-slate-700 mb-3" />
                      <p>暂无社区帖子</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        
        
        <div className="px-6 py-4 border-t border-white/10 flex items-center justify-between">
           <div className="text-sm text-slate-500">共 {total} 条记录</div>
           <div className="flex gap-1">
              <button 
                disabled={page <= 1}
                onClick={() => setPage(p => p - 1)}
                className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-white/5 text-slate-500 hover:text-white hover:bg-white/10 disabled:opacity-50 disabled:cursor-not-allowed border border-white/5 transition-colors"
              >&lt;</button>
              <button className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-gradient-to-r from-purple-600 to-blue-600 text-white border-none shadow-[0_0_10px_rgba(168,85,247,0.4)]">
                {page}
              </button>
              <button 
                disabled={posts.length < 10}
                onClick={() => setPage(p => p + 1)}
                className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-white/5 text-slate-500 hover:text-white hover:bg-white/10 disabled:opacity-50 disabled:cursor-not-allowed border border-white/5 transition-colors"
              >&gt;</button>
           </div>
        </div>
      </div>
    </div>
  );
}
