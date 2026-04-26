


import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { motion } from 'motion/react';
import { ArrowLeft, Send } from 'lucide-react';
import request from '../lib/request';
import { useAuth } from '../lib/AuthContext';

export default function CommunityPostEditor() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [tags, setTags] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { user } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim()) {
      setError('标题和内容不能为空');
      return;
    }
    
    if (!user) {
      setError('请先登录再发帖');
      return;
    }
    
    setError('');
    setLoading(true);
    
    try {
      const tagArray = tags.split(',').map(t => t.trim()).filter(Boolean);
      await request.post('/api/community/posts', { 
        title, 
        content,
        tags: tagArray
      });
      navigate('/community');
    } catch (err: any) {
      setError(err.message || '发布失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <Link to="/community" className="inline-flex items-center gap-2 text-slate-400 hover:text-purple-400 mb-8 transition-colors">
        <ArrowLeft className="w-4 h-4" /> 返回社区
      </Link>

      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white/[0.02] border border-white/5 rounded-3xl p-6 md:p-10 backdrop-blur-sm"
      >
        <h1 className="text-2xl font-bold text-white mb-8">发布新帖子</h1>
        
        {error && (
          <div className="mb-6 p-4 bg-red-500/10 border border-red-500/30 rounded-xl text-red-400 text-sm">
            {error}
          </div>
        )}
        
        {!user && (
          <div className="mb-6 p-4 bg-yellow-500/10 border border-yellow-500/30 rounded-xl text-yellow-400 text-sm">
            提示：请先 <Link to="/login?redirect=/community/new" className="underline font-bold">登录</Link> 再发布帖子。
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">标题</label>
            <input 
              type="text" 
              value={title}
              onChange={e => setTitle(e.target.value)}
              placeholder="写一个清楚的问题或主题"
              className="w-full bg-black/20 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-slate-600 focus:outline-none focus:border-purple-500/50 transition-all"
              disabled={!user}
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">标签（用逗号分隔）</label>
            <input 
              type="text" 
              value={tags}
              onChange={e => setTags(e.target.value)}
              placeholder="例如：React, 经验分享, 求助"
              className="w-full bg-black/20 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-slate-600 focus:outline-none focus:border-purple-500/50 transition-all"
              disabled={!user}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-400 mb-2">正文（支持 Markdown）</label>
            <textarea 
              value={content}
              onChange={e => setContent(e.target.value)}
              placeholder="在这里分享你的想法、代码或问题..."
              className="w-full h-64 bg-black/20 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-slate-600 focus:outline-none focus:border-purple-500/50 transition-all font-mono text-sm resize-y"
              disabled={!user}
            ></textarea>
          </div>

          <div className="flex justify-end">
            <button 
              type="submit" 
              disabled={loading || !user}
              className="px-8 py-3 bg-gradient-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white rounded-xl font-medium transition-all shadow-[0_0_20px_rgba(168,85,247,0.3)] flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? '发布中...' : '发布帖子'}
              {!loading && <Send className="w-4 h-4" />}
            </button>
          </div>
        </form>
      </motion.div>
    </div>
  );
}
