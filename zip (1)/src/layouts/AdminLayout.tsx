import { Outlet, Link, useLocation } from 'react-router-dom';
import { 
  LayoutDashboard, 
  FileText, 
  FolderTree, 
  Tags, 
  MessageSquare, 
  Users, 
  Settings, 
  Search, 
  Bell,
  ShieldAlert,
  Flag
} from 'lucide-react';
import { cn } from '../lib/utils';
import { motion } from 'motion/react';
import type { ReactNode } from 'react';

const MENU_ITEMS = [
  { icon: LayoutDashboard, label: '仪表盘', path: '/admin' },
  { icon: FileText, label: '文章管理', path: '/admin/articles' },
  { icon: FolderTree, label: '分类管理', path: '/admin/categories' },
  { icon: Tags, label: '标签管理', path: '/admin/tags' },
  { icon: MessageSquare, label: '评论管理', path: '/admin/comments' },
  { icon: Users, label: '社区帖子', path: '/admin/community' },
  { icon: ShieldAlert, label: '审核任务', path: '/admin/moderation' },
  { icon: Flag, label: '举报处理', path: '/admin/reports' },
  { icon: Settings, label: '系统设置', path: '/admin/settings' },
];

export default function AdminLayout() {
  const location = useLocation();

  return (
    <div className="min-h-screen bg-[#030014] text-slate-200 font-sans flex overflow-hidden selection:bg-purple-500/30">
      {/* Background Effect */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute top-[-10%] left-[-10%] w-[120%] h-[120%] bg-[radial-gradient(circle_at_50%_50%,rgba(76,29,149,0.08),transparent_50%)]"></div>
      </div>

      {/* Sidebar */}
      <aside className="w-64 h-screen border-r border-white/10 bg-black/20 backdrop-blur-xl flex flex-col relative z-20 shrink-0">
        <div className="h-16 flex items-center px-6 border-b border-white/10">
          <div className="text-xl font-bold bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
            DevBlog Admin
          </div>
        </div>
        
        <div className="flex-1 overflow-y-auto py-6 px-4 space-y-1">
          {MENU_ITEMS.map((item) => {
            const isActive = location.pathname === item.path;
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={cn(
                  "flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-sm font-medium",
                  isActive 
                    ? "bg-purple-600/20 text-purple-300 border border-purple-500/30 shadow-[0_0_15px_rgba(168,85,247,0.15)]" 
                    : "text-slate-400 hover:bg-white/5 hover:text-slate-200 border border-transparent"
                )}
              >
                <Icon className={cn("w-4 h-4", isActive ? "text-purple-400" : "text-slate-500")} />
                {item.label}
              </Link>
            );
          })}
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col relative z-10 w-full overflow-hidden">
        {/* Top Header */}
        <header className="h-16 border-b border-white/10 bg-black/20 backdrop-blur-md flex items-center justify-between px-8 shrink-0">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/40" />
            <input 
              type="text" 
              placeholder="搜索..."
              className="py-1.5 pl-9 pr-4 rounded-full bg-white/5 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 transition-all text-white placeholder-slate-500 w-64"
            />
          </div>
          
          <div className="flex items-center gap-6">
            <button className="relative text-slate-400 hover:text-white transition-colors">
              <Bell className="w-5 h-5" />
              <span className="absolute -top-1 -right-1 w-2 h-2 bg-pink-500 rounded-full border border-[#030014]"></span>
            </button>
            <div className="flex items-center gap-3 pl-6 border-l border-white/10">
              <div className="text-right hidden sm:block">
                <div className="text-sm font-medium text-white">Admin User</div>
                <div className="text-[10px] text-slate-500 leading-none">Super Administrator</div>
              </div>
              <div className="w-9 h-9 rounded-full border-2 border-purple-500/50 p-0.5">
                <div className="w-full h-full rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center">
                  <span className="text-xs font-bold text-white">AD</span>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Dynamic Content */}
        <main className="flex-1 overflow-y-auto p-8">
          <motion.div
            initial={{ opacity: 0, y: 15 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
          >
            <Outlet />
          </motion.div>
        </main>
      </div>
    </div>
  );
}
