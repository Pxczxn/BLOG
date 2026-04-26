/**
 * 设置管理页
 * <p>
 * 系统设置和配置
 */
import { useState } from 'react';
import { Save, CheckCircle } from 'lucide-react';

export default function SettingsManage() {
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [form, setForm] = useState({
    siteName: '破星辰只寻你',
    siteSub: '在代码的星河中，寻找技术与自由',
    adminNick: 'FrontendEngineer',
    keywords: '博客, 前端, React, 技术分享',
    description: '一个专注于前端技术与设计的个人博客',
    allowGuest: false
  });

  const handleSave = () => {
    setSaving(true);
    // 模拟保存请求
    setTimeout(() => {
      setSaving(false);
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    }, 800);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between mb-2 min-h-[40px]">
        <h1 className="text-2xl font-bold text-white">系统设置</h1>
        <div className="flex items-center gap-4">
          {saved && <span className="text-emerald-400 text-sm flex items-center gap-1"><CheckCircle className="w-4 h-4"/> 已保存</span>}
          <button 
            onClick={handleSave}
            disabled={saving}
            className="px-4 py-2 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white rounded-xl font-medium transition-all shadow-[0_0_15px_rgba(168,85,247,0.4)] border-none flex items-center gap-2 text-sm disabled:opacity-50"
          >
            <Save className="w-4 h-4" />
            {saving ? '保存中...' : '保存设置'}
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 relative z-10">
        <div className="bg-white/[0.02] border border-white/5 rounded-3xl backdrop-blur-sm p-6 backdrop-blur-md">
          <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
            <span className="w-1 h-5 bg-purple-500 rounded-full"></span>
            基础信息配置
          </h3>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">站点名称</label>
              <input 
                type="text" 
                value={form.siteName}
                onChange={e => setForm({...form, siteName: e.target.value})}
                className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 text-white transition-colors" 
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">站点副标题</label>
              <input 
                type="text" 
                value={form.siteSub}
                onChange={e => setForm({...form, siteSub: e.target.value})}
                className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 text-white transition-colors" 
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">管理员昵称</label>
              <input 
                type="text" 
                value={form.adminNick}
                onChange={e => setForm({...form, adminNick: e.target.value})}
                className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 text-white transition-colors" 
              />
            </div>
          </div>
        </div>

        <div className="bg-white/[0.02] border border-white/5 rounded-3xl backdrop-blur-sm p-6 backdrop-blur-md">
          <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
            <span className="w-1 h-5 bg-blue-500 rounded-full"></span>
            SEO 与功能开关
          </h3>
          <div className="space-y-4">
             <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">SEO 关键词 (Keywords)</label>
              <input 
                type="text" 
                value={form.keywords}
                onChange={e => setForm({...form, keywords: e.target.value})}
                className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 text-white transition-colors" 
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-400 mb-1">站点描述 (Description)</label>
              <textarea 
                rows={3} 
                value={form.description}
                onChange={e => setForm({...form, description: e.target.value})}
                className="w-full py-2 px-4 rounded-lg bg-black/20 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 text-white transition-colors" 
              />
            </div>
            
            <div className="pt-2">
              <label className="flex items-center gap-3 cursor-pointer">
                <div className="relative">
                  <input 
                    type="checkbox" 
                    className="sr-only" 
                    checked={form.allowGuest}
                    onChange={e => setForm({...form, allowGuest: e.target.checked})}
                  />
                  <div className={`w-10 h-6 rounded-full border transition-colors ${form.allowGuest ? 'bg-blue-500 border-blue-400' : 'bg-white/10 border-white/10'}`}></div>
                  <div className={`w-4 h-4 bg-white rounded-full absolute left-1 top-1 transition-transform ${form.allowGuest ? 'translate-x-4' : ''}`}></div>
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
