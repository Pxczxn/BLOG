import { Save } from 'lucide-react';

export default function SettingsManage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">系统设置</h1>
        <button className="px-4 py-2 bg-purple-600 hover:bg-purple-500 text-white rounded-lg font-medium transition-all shadow-[0_0_15px_rgba(168,85,247,0.3)] border border-purple-400/30 flex items-center gap-2 text-sm">
          <Save className="w-4 h-4" />
          保存设置
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 relative z-10">
        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-md">
          <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
            <span className="w-1 h-5 bg-purple-500 rounded-full"></span>
            基础信息配置
          </h3>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">站点名称</label>
              <input type="text" placeholder="例如：破星辰只寻你" className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 text-white transition-colors" />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">站点副标题</label>
              <input type="text" placeholder="例如：在代码的星河中，寻找技术与自由" className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 text-white transition-colors" />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">管理员昵称</label>
              <input type="text" placeholder="例如：FrontendEngineer" className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 text-white transition-colors" />
            </div>
          </div>
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-md">
          <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
            <span className="w-1 h-5 bg-blue-500 rounded-full"></span>
            SEO 与功能开关
          </h3>
          <div className="space-y-4">
             <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">SEO 关键词 (Keywords)</label>
              <input type="text" placeholder="博客, 前端, React, 技术分享" className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-blue-500/50 text-white transition-colors" />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">站点描述 (Description)</label>
              <textarea rows={3} placeholder="输入用于搜索引擎索引的网站描述..." className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-blue-500/50 text-white transition-colors" />
            </div>
            
            <div className="pt-2">
              <label className="flex items-center gap-3 cursor-pointer">
                <div className="relative">
                  <input type="checkbox" className="sr-only" />
                  <div className="w-10 h-6 bg-white/10 rounded-full border border-white/10"></div>
                  <div className="w-4 h-4 bg-slate-400 rounded-full absolute left-1 top-1 transition-transform"></div>
                </div>
                <span className="text-sm font-medium text-slate-300">允许游客发布评论</span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
