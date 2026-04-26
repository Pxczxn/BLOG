import { Link, useLocation } from 'react-router-dom';
import { Search, User } from 'lucide-react';
import { motion } from 'motion/react';
import { cn } from '../lib/utils';
import type { ReactNode } from 'react';

export default function Navbar() {
  const location = useLocation();
  
  return (
    <motion.nav 
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      className="fixed top-0 inset-x-0 h-16 z-50 border-b border-white/10 bg-black/20 backdrop-blur-xl flex items-center px-4 sm:px-6 lg:px-8"
    >
      <div className="max-w-7xl mx-auto w-full h-full flex items-center justify-between">
        <div className="flex items-center gap-8">
          <Link to="/" className="text-xl font-bold bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
            DevBlog
          </Link>
          
          <div className="hidden md:flex gap-6 text-sm font-medium text-slate-400">
            <NavLink to="/" isActive={location.pathname === '/'}>首页</NavLink>
            <NavLink to="/category" isActive={location.pathname.startsWith('/category')}>分类</NavLink>
            <NavLink to="/tags" isActive={location.pathname.startsWith('/tags')}>标签</NavLink>
            <NavLink to="/community" isActive={location.pathname.startsWith('/community')}>社区</NavLink>
            <NavLink to="/about" isActive={location.pathname.startsWith('/about')}>关于</NavLink>
          </div>
        </div>
        
        <div className="flex items-center gap-4">
          <div className="relative hidden sm:block">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/50" />
            <input 
              type="text" 
              placeholder="搜索应用..."
              className="py-1.5 pl-9 pr-4 rounded-full bg-white/5 border border-white/10 text-sm focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 transition-all text-white placeholder-slate-500 w-48 focus:w-64"
            />
          </div>
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full border border-purple-500/50 p-0.5">
              <div className="w-full h-full rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center">
                 <User className="w-4 h-4 text-white/80" />
              </div>
            </div>
            <span className="text-sm opacity-80 hidden sm:block text-slate-200">Admin</span>
          </div>
        </div>
      </div>
    </motion.nav>
  );
}

function NavLink({ to, children, isActive = false }: { to: string; children: ReactNode, isActive?: boolean }) {
  return (
    <Link 
      to={to} 
      className={cn(
        "transition-colors",
        isActive 
          ? "text-purple-400 border-b-2 border-purple-400 pb-1" 
          : "hover:text-white"
      )}
    >
      {children}
    </Link>
  );
}
