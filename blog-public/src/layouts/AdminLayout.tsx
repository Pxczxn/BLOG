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
  Flag,
} from 'lucide-react';
import { cn } from '../lib/utils';

const MENU_ITEMS = [
  { icon: LayoutDashboard, label: '仪表盘', path: '/admin-pxczxn' },
  { icon: FileText, label: '文章管理', path: '/admin-pxczxn/articles' },
  { icon: FolderTree, label: '分类管理', path: '/admin-pxczxn/categories' },
  { icon: Tags, label: '标签管理', path: '/admin-pxczxn/tags' },
  { icon: MessageSquare, label: '评论管理', path: '/admin-pxczxn/comments' },
  { icon: Users, label: '社区帖子', path: '/admin-pxczxn/community' },
  { icon: ShieldAlert, label: '审核任务', path: '/admin-pxczxn/moderation' },
  { icon: Flag, label: '举报处理', path: '/admin-pxczxn/reports' },
  { icon: Settings, label: '系统设置', path: '/admin-pxczxn/settings' },
];

export default function AdminLayout() {
  const location = useLocation();

  return (
    <div className="flex min-h-screen overflow-hidden bg-[#030014] font-sans text-slate-200 selection:bg-purple-500/30">
      <div className="pointer-events-none fixed inset-0 z-0">
        <div className="absolute top-[-10%] left-[-10%] h-[120%] w-[120%] bg-[radial-gradient(circle_at_50%_50%,rgba(76,29,149,0.08),transparent_50%)]" />
      </div>

      <aside className="relative z-20 flex h-screen w-64 shrink-0 flex-col border-r border-white/10 bg-slate-950/85">
        <div className="flex h-16 items-center border-b border-white/10 px-6">
          <div className="bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 bg-clip-text text-xl font-bold text-transparent">
            DevBlog Admin
          </div>
        </div>

        <div className="flex-1 space-y-1 overflow-y-auto px-4 py-6">
          {MENU_ITEMS.map((item) => {
            const exactRoot = item.path === '/admin-pxczxn';
            const isActive = exactRoot
              ? location.pathname === item.path
              : location.pathname === item.path || location.pathname.startsWith(`${item.path}/`);
            const Icon = item.icon;

            return (
              <Link
                key={item.path}
                to={item.path}
                className={cn(
                  'flex items-center gap-3 rounded-xl border px-4 py-3 text-sm font-medium transition-all',
                  isActive
                    ? 'border-purple-500/30 bg-purple-600/20 text-purple-300 shadow-[0_0_15px_rgba(168,85,247,0.15)]'
                    : 'border-transparent text-slate-400 hover:bg-white/5 hover:text-slate-200',
                )}
              >
                <Icon className={cn('h-4 w-4', isActive ? 'text-purple-400' : 'text-slate-500')} />
                {item.label}
              </Link>
            );
          })}
        </div>
      </aside>

      <div className="relative z-10 flex w-full flex-1 flex-col overflow-hidden">
        <header className="flex h-16 shrink-0 items-center justify-between border-b border-white/10 bg-slate-950/85 px-8">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-white/40" />
            <input
              type="text"
              placeholder="搜索..."
              className="w-64 rounded-full border border-white/10 bg-white/5 py-1.5 pl-9 pr-4 text-sm text-white placeholder-slate-500 transition-all focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
            />
          </div>

          <div className="flex items-center gap-6">
            <button className="relative text-slate-400 transition-colors hover:text-white">
              <Bell className="h-5 w-5" />
              <span className="absolute -top-1 -right-1 h-2 w-2 rounded-full border border-[#030014] bg-pink-500" />
            </button>

            <div className="flex items-center gap-3 border-l border-white/10 pl-6">
              <div className="hidden text-right sm:block">
                <div className="text-sm font-medium text-white">Admin</div>
                <div className="text-[10px] leading-none text-slate-500">Super Administrator</div>
              </div>
              <div className="h-9 w-9 rounded-full border-2 border-purple-500/50 p-0.5">
                <div className="flex h-full w-full items-center justify-center rounded-full bg-gradient-to-br from-indigo-500 to-purple-600">
                  <span className="text-xs font-bold text-white">AD</span>
                </div>
              </div>
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-8">
          <div>
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
