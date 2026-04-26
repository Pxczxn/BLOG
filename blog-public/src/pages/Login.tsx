


import { useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { motion } from 'motion/react';
import { User, Key, ArrowRight } from 'lucide-react';
import { useAuth } from '../lib/AuthContext';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login } = useAuth();

  const getLoginErrorMessage = () => '账号或密码错误';

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      setError('请输入用户名和密码');
      return;
    }
    
    setError('');
    setLoading(true);
    
    try {
      await login({ username, password });
      const redirect = searchParams.get('redirect');
      navigate(redirect?.startsWith('/') ? redirect : '/community', { replace: true });
    } catch {
      setError(getLoginErrorMessage());
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#030014] flex items-center justify-center relative overflow-hidden font-sans pt-16">
      
      <div className="absolute inset-0 pointer-events-none z-0">
        <div className="absolute top-[-20%] left-[-10%] w-[120%] h-[120%] bg-[radial-gradient(circle_at_50%_50%,rgba(76,29,149,0.1),transparent_50%)]"></div>
      </div>

      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md p-8 relative z-10"
      >
        <div className="bg-white/5 border border-white/10 rounded-3xl p-8 backdrop-blur-xl shadow-2xl relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500"></div>
          
          <div className="text-center mb-8">
            <h1 className="text-2xl font-bold text-white tracking-tight">欢迎回来</h1>
            <p className="text-slate-400 text-sm mt-2">登录你的社区账号</p>
          </div>

          <form onSubmit={handleLogin} className="space-y-5">
            <div className="space-y-1">
              <label className="text-xs font-medium text-slate-400 uppercase tracking-wider ml-1">用户名或邮箱</label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-500" />
                <input 
                  type="text" 
                  value={username}
                  onChange={e => setUsername(e.target.value)}
                  placeholder="用户名或邮箱"
                  className="w-full py-3 pl-11 pr-4 bg-black/20 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 transition-all"
                  autoComplete="off"
                />
              </div>
            </div>

            <div className="space-y-1">
              <label className="text-xs font-medium text-slate-400 uppercase tracking-wider ml-1">密码</label>
              <div className="relative">
                <Key className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-500" />
                <input 
                  type="password" 
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full py-3 pl-11 pr-4 bg-black/20 border border-white/10 rounded-xl text-white placeholder-slate-600 focus:outline-none focus:border-purple-500/50 focus:ring-1 focus:ring-purple-500/50 transition-all"
                  autoComplete="off"
                />
              </div>
            </div>

            {error && (
              <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-xl text-red-400 text-sm text-center">
                {error}
              </div>
            )}

            <button 
              type="submit" 
              disabled={loading}
              className="w-full py-3.5 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white rounded-xl font-medium transition-all shadow-[0_0_20px_rgba(168,85,247,0.3)] flex items-center justify-center gap-2 group disabled:opacity-70 disabled:cursor-not-allowed mt-4"
            >
              {loading ? '登录中...' : '登录'}
              {!loading && <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />}
            </button>

            <div className="mt-6 text-center">
              <p className="text-slate-400 text-sm">
                还没有账号？ <Link to={`/register${searchParams.get('redirect') ? `?redirect=${encodeURIComponent(searchParams.get('redirect') || '')}` : ''}`} className="text-purple-400 hover:text-purple-300 font-medium">立即注册</Link>
              </p>
            </div>
          </form>
        </div>
      </motion.div>
    </div>
  );
}
