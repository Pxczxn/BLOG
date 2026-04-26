import { Plus, Search, FolderTree } from 'lucide-react';

const CATEGORIES: any[] = [];

export default function CategoryManage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">分类管理</h1>
        <button className="px-4 py-2 bg-purple-600 hover:bg-purple-500 text-white rounded-lg font-medium transition-all shadow-[0_0_15px_rgba(168,85,247,0.3)] border border-purple-400/30 flex items-center gap-2 text-sm">
          <Plus className="w-4 h-4" />
          新增分类
        </button>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl p-4 backdrop-blur-md flex flex-wrap gap-4 items-center justify-between">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
          <input 
            type="text" 
            placeholder="搜索分类名称..."
            className="py-2 pl-9 pr-4 rounded-lg bg-white/5 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 transition-all text-white placeholder-slate-500 w-64"
          />
        </div>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl backdrop-blur-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-black/20 text-xs uppercase tracking-wider text-slate-400">
                <th className="px-6 py-4 font-medium">分类名称</th>
                <th className="px-6 py-4 font-medium">别名</th>
                <th className="px-6 py-4 font-medium">描述</th>
                <th className="px-6 py-4 font-medium">文章数</th>
                <th className="px-6 py-4 font-medium">创建时间</th>
                <th className="px-6 py-4 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm divide-y divide-white/5">
              {CATEGORIES.length > 0 ? null : (
                <tr>
                  <td colSpan={6} className="px-6 py-16 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center">
                      <FolderTree className="w-12 h-12 text-slate-700 mb-3" />
                      <p>暂无分类数据</p>
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
