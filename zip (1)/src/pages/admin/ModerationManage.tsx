import { Search, ShieldAlert } from 'lucide-react';

const MODERATION_TASKS: any[] = [];

export default function ModerationManage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">审核任务</h1>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl p-4 backdrop-blur-md flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4 items-center">
          <select className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-red-500/50 appearance-none min-w-[120px]">
            <option value="">全部类型</option>
            <option value="article">文章审核</option>
            <option value="comment">评论审核</option>
            <option value="post">帖子审核</option>
          </select>
        </div>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl backdrop-blur-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">任务ID</th>
                <th className="px-6 py-4 font-medium">审核类型</th>
                <th className="px-6 py-4 font-medium">提交人</th>
                <th className="px-6 py-4 font-medium">风险评级</th>
                <th className="px-6 py-4 font-medium">提交时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {MODERATION_TASKS.length > 0 ? null : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <ShieldAlert className="w-12 h-12 text-slate-700 mb-3" />
                      <p>当前没有待审核任务</p>
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
