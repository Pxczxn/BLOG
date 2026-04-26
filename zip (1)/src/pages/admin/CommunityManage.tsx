import { Plus, Search, Users } from 'lucide-react';

const COMMUNITY_POSTS: any[] = [];

export default function CommunityManage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">社区帖子管理</h1>
        <button className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg font-medium transition-all shadow-[0_0_15px_rgba(16,185,129,0.3)] border border-emerald-400/30 flex items-center gap-2 text-sm">
          <Plus className="w-4 h-4" />
          发布官方贴
        </button>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl p-4 backdrop-blur-md flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4 items-center">
          <select className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-emerald-500/50 appearance-none min-w-[120px]">
            <option value="">全部板块</option>
            <option value="tech">技术交流</option>
            <option value="qa">问答求助</option>
            <option value="share">资源分享</option>
          </select>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
            <input 
              type="text" 
              placeholder="搜索帖子标题..."
              className="py-2 pl-9 pr-4 rounded-lg bg-white/5 border border-white/10 text-sm focus:outline-none focus:border-emerald-500/50 transition-all text-white placeholder-slate-500 w-64"
            />
          </div>
        </div>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl backdrop-blur-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">帖子标题</th>
                <th className="px-6 py-4 font-medium">所属板块</th>
                <th className="px-6 py-4 font-medium">发布者</th>
                <th className="px-6 py-4 font-medium">浏览/回复</th>
                <th className="px-6 py-4 font-medium">发布时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {COMMUNITY_POSTS.length > 0 ? null : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <Users className="w-12 h-12 text-slate-700 mb-3" />
                      <p>暂无社区帖子数据</p>
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
