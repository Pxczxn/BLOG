/**
 * 举报处理页
 */
import { useState, useEffect } from 'react';
import { Search, Flag, CheckCircle, XCircle } from 'lucide-react';
import request from '../../lib/request';
import { format } from 'date-fns';

export default function ReportManage() {
  const [reports, setReports] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState('OPEN'); // OPEN, RESOLVED, DISMISSED
  const [type, setType] = useState('');
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const fetchReports = async () => {
    try {
      setLoading(true);
      const res: any = await request.get('/api/admin/moderation/reports', {
        params: { page, size: 10, status: status || undefined, type: type || undefined }
      });
      const data = res?.data ?? res;
      setReports(data.items || []);
      setTotal(data.total || 0);
    } catch (error) {
      // Error handled silently
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, [page, status, type]);

  const handleReport = async (id: number, targetStatus: 'RESOLVED' | 'DISMISSED', handleAction: string) => {
    try {
      await request.put(`/api/admin/moderation/reports/${id}/handle`, { status: targetStatus, handleAction });
      fetchReports();
    } catch (error) {
      // Error handled silently
    }
  };

  const StatusBadge = ({ s }: { s: string }) => {
    switch (s) {
      case 'OPEN': return <span className="px-2 py-1 rounded bg-yellow-500/20 text-yellow-300 border border-yellow-500/30 text-xs">待处理</span>;
      case 'RESOLVED': return <span className="px-2 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 text-xs">已解决(违规)</span>;
      case 'DISMISSED': return <span className="px-2 py-1 rounded bg-slate-500/20 text-slate-300 border border-slate-500/30 text-xs">已驳回(正常)</span>;
      default: return <span className="px-2 py-1 rounded bg-slate-500/20 text-slate-300 border border-slate-500/30 text-xs">{s}</span>;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">举报处理</h1>
      </div>

      <div className="bg-slate-950/45 border border-white/5 rounded-3xl p-4 shadow-[0_16px_45px_rgba(0,0,0,0.18)] flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4 items-center">
          <select 
            value={status} 
            onChange={e => { setStatus(e.target.value); setPage(1); }}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-red-500/50 appearance-none min-w-[120px]"
          >
            <option value="">全部状态</option>
            <option value="OPEN">待处理</option>
            <option value="RESOLVED">已解决</option>
            <option value="DISMISSED">已驳回</option>
          </select>
          <select 
            value={type} 
            onChange={e => { setType(e.target.value); setPage(1); }}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-red-500/50 appearance-none min-w-[120px]"
          >
            <option value="">全部类型</option>
            <option value="SPAM">垃圾广告</option>
            <option value="ABUSE">辱骂攻击</option>
            <option value="PORN">色情低俗</option>
            <option value="OTHER">其他</option>
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
                <th className="px-6 py-4 font-medium">原因</th>
                <th className="px-6 py-4 font-medium">状态</th>
                <th className="px-6 py-4 font-medium">举报时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">加载中...</td>
                </tr>
              ) : reports.length > 0 ? (
                reports.map((report) => (
                  <tr key={report.id} className="hover:bg-white/5 transition-colors">
                    <td className="px-6 py-4 text-white font-medium">{report.targetId}</td>
                    <td className="px-6 py-4 text-slate-300">{report.reportType}</td>
                    <td className="px-6 py-4 text-slate-300 max-w-[200px] truncate" title={report.reason}>
                      {report.reason}
                    </td>
                    <td className="px-6 py-4">
                      <StatusBadge s={report.status} />
                    </td>
                    <td className="px-6 py-4 text-slate-400">
                      {report.createdAt ? format(new Date(report.createdAt), 'yyyy-MM-dd HH:mm') : '-'}
                    </td>
                    <td className="px-6 py-4 text-right">
                       <div className="flex justify-end gap-3">
                         {report.status === 'OPEN' && (
                           <>
                             <button onClick={() => handleReport(report.id, 'RESOLVED', 'CONTENT_HIDDEN')} className="text-red-400 hover:text-red-300 transition-colors" title="确认违规(隐藏内容)">
                               <CheckCircle className="w-4 h-4" />
                             </button>
                             <button onClick={() => handleReport(report.id, 'DISMISSED', 'NO_ACTION')} className="text-slate-400 hover:text-slate-300 transition-colors" title="驳回(正常)">
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
                      <Flag className="w-12 h-12 text-slate-700 mb-3" />
                      <p>暂无举报数据</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        
        {/* 分页骨架 */}
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
                disabled={reports.length < 10}
                onClick={() => setPage(p => p + 1)}
                className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-white/5 text-slate-500 hover:text-white hover:bg-white/10 disabled:opacity-50 disabled:cursor-not-allowed border border-white/5 transition-colors"
              >&gt;</button>
           </div>
        </div>
      </div>
    </div>
  );
}
