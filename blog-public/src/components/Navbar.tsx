import type { ReactNode } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Search, User, BookOpen, Package, MessageSquare, Info, LogOut } from 'lucide-react';
import { motion } from 'motion/react';
import { cn } from '../lib/utils';
import { useAuth } from '../lib/AuthContext';
import { getStaticUrl } from '../lib/request';
import RoleBadge from './RoleBadge';

export default function Navbar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  return (
    <motion.nav
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      className="fixed inset-x-0 top-0 z-50 flex h-16 items-center border-b border-white/10 bg-black/20 px-4 backdrop-blur-xl sm:px-6 lg:px-8"
    >
      <div className="mx-auto flex h-full w-full max-w-7xl items-center justify-between">
        <div className="flex items-center gap-8">
          <Link
            to="/"
            className="shrink-0 bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 bg-clip-text text-xl font-bold text-transparent"
          >
            破星辰只寻你
          </Link>

          <div className="hidden gap-2 text-sm font-medium text-slate-400 md:flex">
            <NavLink to="/" isActive={location.pathname === '/'}>
              首页
            </NavLink>

            <NavLink
              to="/blog"
              isActive={['/blog', '/post', '/category', '/tags'].some((path) => location.pathname.startsWith(path))}
            >
              <span className="flex items-center gap-1.5">
                <BookOpen className="h-4 w-4" />
                博客
              </span>
            </NavLink>

            <NavLink to="/resources" isActive={location.pathname.startsWith('/resources')}>
              <span className="flex items-center gap-1.5">
                <Package className="h-4 w-4" />
                资源
              </span>
            </NavLink>

            <NavLink to="/community" isActive={location.pathname.startsWith('/community')}>
              <span className="flex items-center gap-1.5">
                <MessageSquare className="h-4 w-4" />
                社区
              </span>
            </NavLink>

            <NavLink to="/about" isActive={location.pathname.startsWith('/about')}>
              <span className="flex items-center gap-1.5">
                <Info className="h-4 w-4" />
                关于
              </span>
            </NavLink>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="relative hidden sm:block">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-white/50" />
            <input
              type="text"
              placeholder="搜索内容..."
              className="w-40 rounded-full border border-white/10 bg-white/5 py-1.5 pl-9 pr-4 text-sm text-white placeholder-slate-500 transition-all focus:w-56 focus:border-purple-500/50 focus:outline-none focus:ring-1 focus:ring-purple-500/50"
            />
          </div>

          {user ? (
            <div className="group/user relative">
              <button
                type="button"
                onClick={() => navigate('/me')}
                className="flex items-center gap-2"
              >
                <div className="h-8 w-8 overflow-hidden rounded-full border border-purple-500/50 p-0.5">
                  {user.avatar ? (
                    <img
                      src={getStaticUrl(user.avatar)}
                      alt={user.username}
                      className="h-full w-full rounded-full object-cover"
                    />
                  ) : (
                    <div className="flex h-full w-full items-center justify-center rounded-full bg-gradient-to-br from-indigo-500 to-purple-600">
                      <User className="h-4 w-4 text-white/80" />
                    </div>
                  )}
                </div>
              </button>

              <div className="invisible absolute right-0 top-full z-[60] mt-2 w-56 overflow-hidden rounded-2xl border border-white/10 bg-slate-900/90 opacity-0 shadow-2xl transition-all group-hover/user:visible group-hover/user:opacity-100">
                <div className="border-b border-white/10 p-4">
                  <div className="flex items-center gap-2">
                    <p className="line-clamp-1 text-sm font-bold text-white">
                      {user.displayName || user.username}
                    </p>
                    <RoleBadge role={user.role} compact />
                  </div>
                  <p className="mt-1 line-clamp-1 text-xs text-slate-400">@{user.username}</p>
                </div>
                <div className="p-2">
                  <button
                    type="button"
                    onClick={() => navigate('/me')}
                    className="flex w-full items-center gap-2 rounded-xl px-3 py-2 text-left text-sm text-slate-300 transition-colors hover:bg-white/5 hover:text-white"
                  >
                    <User className="h-4 w-4" />
                    个人主页
                  </button>
                  <Link
                    to="/community/new"
                    className="flex items-center gap-2 rounded-xl px-3 py-2 text-sm text-slate-300 transition-colors hover:bg-white/5 hover:text-white"
                  >
                    <MessageSquare className="h-4 w-4" />
                    发布帖子
                  </Link>
                  <button
                    type="button"
                    onClick={logout}
                    className="mt-1 flex w-full items-center gap-2 rounded-xl px-3 py-2 text-sm text-slate-300 transition-colors hover:bg-red-500/10 hover:text-red-400"
                  >
                    <LogOut className="h-4 w-4" />
                    退出登录
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <Link to="/login" className="group flex items-center gap-2">
              <div className="h-8 w-8 rounded-full border border-purple-500/50 p-0.5 transition-colors group-hover:border-purple-400">
                <div className="flex h-full w-full items-center justify-center rounded-full bg-gradient-to-br from-slate-700 to-slate-800">
                  <User className="h-4 w-4 text-white/80" />
                </div>
              </div>
              <span className="hidden text-sm font-medium text-slate-300 transition-colors group-hover:text-white sm:block">
                登录
              </span>
            </Link>
          )}
        </div>
      </div>
    </motion.nav>
  );
}

function NavLink({
  to,
  children,
  isActive = false,
}: {
  to: string;
  children: ReactNode;
  isActive?: boolean;
}) {
  return (
    <Link
      to={to}
      className={cn(
        'relative flex items-center justify-center rounded-lg px-4 py-2 transition-all duration-300',
        isActive ? 'bg-purple-500/10 text-purple-400' : 'hover:bg-white/5 hover:text-white',
      )}
    >
      {children}
      {isActive && (
        <motion.div
          layoutId="nav-active"
          className="absolute bottom-0 left-2 right-2 h-0.5 rounded-full bg-gradient-to-r from-purple-500 to-pink-500"
        />
      )}
    </Link>
  );
}
