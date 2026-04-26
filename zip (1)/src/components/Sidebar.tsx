import { motion } from 'motion/react';

const CATEGORIES = [
  { name: '前端开发', count: 42 },
  { name: '后端开发', count: 28 },
  { name: '随笔杂谈', count: 16 },
  { name: '生活日常', count: 12 },
  { name: '学习笔记', count: 24 }
];

const TAGS = ['React', 'Vue', 'Node.js', 'TypeScript', 'Tailwind', 'Vite', 'MySQL', 'Docker', 'CSS', '算法'];

export default function Sidebar() {
  return (
    <div className="space-y-6">
      {/* Profile Card */}
      <motion.div 
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-xl relative overflow-hidden"
      >
        <div className="absolute top-0 right-0 w-24 h-24 bg-purple-500/10 blur-2xl rounded-full"></div>
        <div className="flex items-center gap-4 mb-4 relative z-10">
          <div className="w-14 h-14 rounded-full border-2 border-purple-500 p-0.5 overflow-hidden">
            <img 
              src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&h=200&q=80" 
              alt="Avatar"
              className="w-full h-full object-cover rounded-full"
            />
          </div>
          <div>
            <h4 className="font-bold text-lg text-white">前端工程师</h4>
            <p className="text-xs text-slate-400">记录技术思考与成长轨迹</p>
          </div>
        </div>
        <div className="grid grid-cols-3 gap-2 text-center border-t border-white/10 pt-4 relative z-10">
          <div><p className="text-lg font-bold">128</p><p className="text-[10px] text-slate-500 uppercase tracking-tighter">文章</p></div>
          <div><p className="text-lg font-bold">42</p><p className="text-[10px] text-slate-500 uppercase tracking-tighter">分类</p></div>
          <div><p className="text-lg font-bold">3.4k</p><p className="text-[10px] text-slate-500 uppercase tracking-tighter">关注</p></div>
        </div>
      </motion.div>

      {/* Categories */}
      <motion.div 
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ delay: 0.1 }}
        className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-xl"
      >
        <h4 className="text-sm font-bold text-white mb-4 flex items-center justify-between uppercase tracking-wider">
          <span>文章分类</span>
          <span className="text-[10px] text-purple-400">ALL</span>
        </h4>
        <ul className="space-y-2">
          {CATEGORIES.map((cat, i) => (
            <li key={i} className="flex items-center justify-between text-sm group cursor-pointer">
              <span className="text-slate-400 group-hover:text-white transition-colors">{cat.name}</span>
              <span className="bg-white/10 px-2 py-0.5 rounded text-[10px] text-slate-300">{cat.count}</span>
            </li>
          ))}
        </ul>
      </motion.div>

      {/* Tag Cloud */}
      <motion.div 
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-xl"
      >
        <h4 className="text-sm font-bold text-white mb-4 uppercase tracking-wider">标签云</h4>
        <div className="flex flex-wrap gap-2">
          {TAGS.map((tag, i) => (
            <span 
              key={i} 
              className="px-3 py-1 bg-white/5 rounded-full text-[10px] border border-white/10 hover:border-purple-500/50 hover:text-purple-300 transition-all cursor-pointer text-slate-300"
            >
              {tag}
            </span>
          ))}
        </div>
      </motion.div>
    </div>
  );
}
