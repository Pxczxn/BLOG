import { Plus, Search, Filter, MoreHorizontal } from 'lucide-react';

const ARTICLES: any[] = []; // 空数据数组

export default function ArticleManage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">文章管理</h1>
        <button className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg font-medium transition-all shadow-[0_0_15px_rgba(79,70,229,0.3)] border border-indigo-400/30 flex items-center gap-2 text-sm">
          <Plus className="w-4 h-4" />
          新增文章
        </button>
      </div>

      {/* Filters & Actions */}
      <div className="bg-white/5 border border-white/10 rounded-2xl p-4 backdrop-blur-md flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4 items-center">
          <select className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 appearance-none min-w-[120px]">
            <option value="">全部分类</option>
          </select>
          <select className="bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-purple-500/50 appearance-none min-w-[120px]">
            <option value="">全部状态</option>
          </select>
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
            <input 
              type="text" 
              placeholder="搜索文章标题..."
              className="py-2 pl-9 pr-4 rounded-lg bg-white/5 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 transition-all text-white placeholder-slate-500 w-64"
            />
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white/5 border border-white/10 rounded-2xl backdrop-blur-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">标题</th>
                <th className="px-6 py-4 font-medium">分类</th>
                <th className="px-6 py-4 font-medium">状态</th>
                <th className="px-6 py-4 font-medium">作者</th>
                <th className="px-6 py-4 font-medium">发布时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {ARTICLES.length > 0 ? (
                ARTICLES.map((article: any, index: number) => (
                  <tr key={index} className="hover:bg-white/5 transition-colors">
                    {/* Data rows would go here */}
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <FileText className="w-12 h-12 text-slate-700 mb-3" />
                      <p>暂无数据</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        
        {/* Pagination Skeleton */}
        <div className="px-6 py-4 border-t border-white/10 flex items-center justify-between">
           <div className="text-sm text-slate-500">共 0 条记录</div>
           <div className="flex gap-1">
              <button disabled className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-white/5 text-slate-500 cursor-not-allowed border border-white/5">&lt;</button>
              <button className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-purple-600 text-white border border-purple-500/30">1</button>
              <button disabled className="w-8 h-8 rounded shrink-0 flex items-center justify-center bg-white/5 text-slate-500 cursor-not-allowed border border-white/5">&gt;</button>
           </div>
        </div>
      </div>
    </div>
  );
}

// Local FileText icon for empty state
function FileText({ className }: { className?: string }) {
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
