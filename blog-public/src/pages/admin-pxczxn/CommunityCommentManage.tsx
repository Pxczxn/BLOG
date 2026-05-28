import { useEffect, useState } from 'react'; // file touched
import { CheckCircle, MessageSquare, Trash2, XCircle } from 'lucide-react';
import { format } from 'date-fns';
import { Link } from 'react-router-dom';
import request from '../../lib/request';
import AdminPageHeader from '../../components/admin/AdminPageHeader';

export default function CommunityCommentManage() {
  const [comments, setComments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const fetchComments = async () => {
    setLoading(true);
    try {
      const res: any = await request.get('/api/admin/community/comments', {
        params: { page, size: 10, status: status || undefined }
      });
      const data = res?.data ?? res;
      setComments(data.items || []);
      setTotal(data.total || 0);
    } catch {
      setComments([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchComments();
  }, [page, status]);

  const handleAction = async (id: number, action: 'approve' | 'reject') => {
    try {
      await request.put(`/api/admin/community/comments/${id}/${action}`);
      void fetchComments();
    } catch {}
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('确定要删除这条帖子评论吗？')) return;
    try {
      await request.delete(`/api/admin/community/comments/${id}`);
      void fetchComments();
    } catch {}
  };

  return (
    <div className="space-y-6">
      <AdminPageHeader
        title="帖子评论管理"
        actions={
          <Link
            to="/admin-pxczxn/community"
            className="flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-200 transition-all hover:bg-white/10 hover:text-white"
          >
            <MessageSquare className="h-4 w-4" />
            返回帖子
          </Link>
        }
      />

      <div className="rounded-3xl border border-white/5 bg-slate-950/45 p-4 shadow-[0_16px_45px_rgba(0,0,0,0.18)]">
        <select
          value={status}
          onChange={(e) => { setStatus(e.target.value); setPage(1); }}
          className="min-w-[160px] rounded-lg border border-white/10 bg-white/5 px-3 py-2 text-sm text-slate-300 focus:outline-none"
        >
          <option value="">全部评论</option>
          <option value="PENDING">待审核</option>
          <option value="APPROVED">已通过</option>
          <option value="REJECTED">已拒绝</option>
        </select>
      </div>

      <div className="overflow-hidden rounded-3xl border border-white/5 bg-slate-950/45 shadow-[0_16px_45px_rgba(0,0,0,0.18)]">
        <div className="overflow-x-auto">
          <table className="w-full border-collapse text-left">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">评论内容</th>
                <th className="px-6 py-4 font-medium">帖子</th>
                <th className="px-6 py-4 font-medium">评论者</th>
                <th className="px-6 py-4 font-medium">状态</th>
                <th className="px-6 py-4 font-medium">时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5 text-sm">
              {loading ? (
                <tr><td colSpan={6} className="px-6 py-16 text-center text-slate-500">加载中...</td></tr>
              ) : comments.length ? (
                comments.map((comment) => (
                  <tr key={comment.id} className="hover:bg-white/5">
                    <td className="max-w-[320px] truncate px-6 py-4 text-slate-300" title={comment.content}>{comment.content}</td>
                    <td className="px-6 py-4 text-slate-400">
                      <div className="font-medium text-white">{comment.postTitle || `帖子 #${comment.postId}`}</div>
                      <div className="text-xs text-slate-500">{comment.postSlug || ''}</div>
                    </td>
                    <td className="px-6 py-4 text-white">{comment.nickname || '-'}</td>
                    <td className="px-6 py-4">
                      <span className={`rounded border px-2 py-1 text-xs ${
                        comment.status === 'APPROVED' ? 'border-emerald-500/30 bg-emerald-500/20 text-emerald-300'
                          : comment.status === 'REJECTED' ? 'border-red-500/30 bg-red-500/20 text-red-300'
                          : 'border-yellow-500/30 bg-yellow-500/20 text-yellow-300'
                      }`}>
                        {comment.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-slate-400">{comment.createdAt ? format(new Date(comment.createdAt), 'yyyy-MM-dd HH:mm') : '-'}</td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex justify-end gap-3">
                        {comment.status === 'PENDING' && (
                          <>
                            <button onClick={() => handleAction(comment.id, 'approve')} className="text-emerald-400 hover:text-emerald-300" title="通过"><CheckCircle className="h-4 w-4" /></button>
                            <button onClick={() => handleAction(comment.id, 'reject')} className="text-orange-400 hover:text-orange-300" title="拒绝"><XCircle className="h-4 w-4" /></button>
                          </>
                        )}
                        <button onClick={() => handleDelete(comment.id)} className="text-slate-400 hover:text-red-400" title="删除"><Trash2 className="h-4 w-4" /></button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan={6} className="px-6 py-16 text-center text-slate-500">暂无帖子评论</td></tr>
              )}
            </tbody>
          </table>
        </div>
        <div className="flex items-center justify-between border-t border-white/10 px-6 py-4">
          <div className="text-sm text-slate-500">共 {total} 条记录</div>
          <div className="flex gap-1">
            <button disabled={page <= 1} onClick={() => setPage((p) => p - 1)} className="h-8 w-8 rounded border border-white/5 bg-white/5 text-slate-500 disabled:cursor-not-allowed disabled:opacity-50">&lt;</button>
            <button className="h-8 w-8 rounded bg-gradient-to-r from-purple-600 to-blue-600 text-white">{page}</button>
            <button disabled={comments.length < 10} onClick={() => setPage((p) => p + 1)} className="h-8 w-8 rounded border border-white/5 bg-white/5 text-slate-500 disabled:cursor-not-allowed disabled:opacity-50">&gt;</button>
          </div>
        </div>
      </div>
    </div>
  );
}
