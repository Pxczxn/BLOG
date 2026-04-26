import { Search, Flag } from 'lucide-react';

const REPORTS: any[] = [];

export default function ReportManage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">举报处理</h1>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl p-4 backdrop-blur-md flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4 items-center">
          <select className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-red-500/50 appearance-none min-w-[120px]">
            <option value="pending">待处理</option>
            <option value="resolved">已处理</option>
            <option value="rejected">已驳回</option>
          </select>
        </div>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl backdrop-blur-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">被举报内容</th>
                <th className="px-6 py-4 font-medium">举报类型</th>
                <th className="px-6 py-4 font-medium">举报人</th>
                <th className="px-6 py-4 font-medium">处理状态</th>
                <th className="px-6 py-4 font-medium">举报时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {REPORTS.length > 0 ? null : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <Flag className="w-12 h-12 text-slate-700 mb-3" />
                      <p>当前没有举报记录</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
