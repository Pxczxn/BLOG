


import { useState, useEffect } from 'react';
import { Search, ShieldAlert, CheckCircle, XCircle } from 'lucide-react';
import request from '../../lib/request';
import { format } from 'date-fns';

export default function ModerationManage() {
  const [tasks, setTasks] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState('PENDING'); 
  const [type, setType] = useState('');
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const fetchTasks = async () => {
    try {
      setLoading(true);
      const res: any = await request.get('/api/admin/moderation/tasks', {
        params: { page, size: 10, status: status || undefined, type: type || undefined }
      });
      const data = res?.data ?? res;
      setTasks(data.items || []);
      setTotal(data.total || 0);
    } catch (error) {
      
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, [page, status, type]);

  const handleDecision = async (id: number, decision: 'APPROVED' | 'REJECTED') => {
    try {
      await request.put(`/api/admin/moderation/tasks/${id}/decision`, { decision });
      fetchTasks();
    } catch (error) {
      
    }
  };

  const StatusBadge = ({ s }: { s: string }) => {
    switch (s) {
      case 'PENDING': return <span className="px-2 py-1 rounded bg-yellow-500/20 text-yellow-300 border border-yellow-500/30 text-xs">待审核</span>;
      case 'APPROVED': return <span className="px-2 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 text-xs">已通过</span>;
      case 'REJECTED': return <span className="px-2 py-1 rounded bg-red-500/20 text-red-300 border border-red-500/30 text-xs">已拒绝</span>;
      case 'CANCELED': return <span className="px-2 py-1 rounded bg-slate-500/20 text-slate-300 border border-slate-500/30 text-xs">已取消</span>;
      default: return <span className="px-2 py-1 rounded bg-slate-500/20 text-slate-300 border border-slate-500/30 text-xs">{s}</span>;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">审核任务</h1>
      </div>

      <div className="bg-slate-950/45 border border-white/5 rounded-3xl p-4 shadow-[0_16px_45px_rgba(0,0,0,0.18)] flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4 items-center">
          <select 
            value={status} 
            onChange={e => { setStatus(e.target.value); setPage(1); }}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 appearance-none min-w-[120px]"
          >
            <option value="">全部状态</option>
            <option value="PENDING">待审核</option>
            <option value="APPROVED">已通过</option>
            <option value="REJECTED">已拒绝</option>
            <option value="CANCELED">已取消</option>
          </select>
          <select 
            value={type} 
            onChange={e => { setType(e.target.value); setPage(1); }}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 appearance-none min-w-[120px]"
          >
            <option value="">全部类型</option>
            <option value="POST">帖子</option>
            <option value="COMMENT">评论</option>
          </select>
        </div>
      </div>

      <div className="bg-slate-950/45 border border-white/5 rounded-3xl shadow-[0_16px_45px_rgba(0,0,0,0.18)] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">目标ID</th>
                <th className="px-6 py-4 font-medium">类型</th>
                <th className="px-6 py-4 font-medium">风险级别</th>
                <th className="px-6 py-4 font-medium">状态</th>
                <th className="px-6 py-4 font-medium">创建时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">加载中...</td>
                </tr>
              ) : tasks.length > 0 ? (
                tasks.map((task) => (
                  <tr key={task.id} className="hover:bg-white/5 transition-colors">
                    <td className="px-6 py-4 text-white font-medium">{task.targetId}</td>
                    <td className="px-6 py-4 text-slate-300">{task.targetType}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded text-xs border ${
                        task.riskLevel === 'HIGH' ? 'bg-red-500/20 text-red-400 border-red-500/30' :
                        task.riskLevel === 'MEDIUM' ? 'bg-orange-500/20 text-orange-400 border-orange-500/30' :
                        'bg-blue-500/20 text-blue-400 border-blue-500/30'
                      }`}>
                        {task.riskLevel || 'UNKNOWN'}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <StatusBadge s={task.status} />
                    </td>
                    <td className="px-6 py-4 text-slate-400">
                      {task.createdAt ? format(new Date(task.createdAt), 'yyyy-MM-dd HH:mm') : '-'}
                    </td>
                    <td className="px-6 py-4 text-right">
                       <div className="flex justify-end gap-3">
                         {task.status === 'PENDING' && (
                           <>
                             <button onClick={() => handleDecision(task.id, 'APPROVED')} className="text-emerald-400 hover:text-emerald-300 transition-colors" title="放行">
                               <CheckCircle className="w-4 h-4" />
                             </button>
                             <button onClick={() => handleDecision(task.id, 'REJECTED')} className="text-red-400 hover:text-red-300 transition-colors" title="拒绝/违规">
                               <XCircle className="w-4 h-4" />
                             </button>
                           </>
                         )}
                       </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <ShieldAlert className="w-12 h-12 text-slate-700 mb-3" />
                      <p>暂无审核任务</p>
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
                disabled={tasks.length < 10}
                onClick={() => setPage(p => p + 1)}
                className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-white/5 text-slate-500 hover:text-white hover:bg-white/10 disabled:opacity-50 disabled:cursor-not-allowed border border-white/5 transition-colors"
              >&gt;</button>
           </div>
        </div>
      </div>
    </div>
  );
}
