import { motion } from 'motion/react';
import { MessageSquare, Users, ThumbsUp, Eye } from 'lucide-react';

// 此处应通过 API 获取数据，目前为空数组
const TOPICS: any[] = [];

export default function Community() {
  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <motion.h1 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="text-3xl font-black text-white drop-shadow-[0_0_15px_rgba(168,85,247,0.4)]"
          >
            畅所欲言
          </motion.h1>
          <motion.p 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.1 }}
            className="text-slate-400 text-sm mt-2"
          >
            与众多开发者一起交流、分享和进步
          </motion.p>
        </div>
        
        <motion.button 
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="px-6 py-2 bg-purple-600 hover:bg-purple-500 text-white rounded-full font-medium transition-all shadow-[0_0_20px_rgba(168,85,247,0.4)] border border-purple-400/30 flex items-center gap-2"
        >
          <MessageSquare className="w-4 h-4" />
          发布帖子
        </motion.button>
      </div>

      <div className="bg-white/5 border border-white/10 rounded-2xl backdrop-blur-xl relative z-10 overflow-hidden">
        {/* Header Tabs */}
        <div className="flex items-center gap-6 px-6 py-4 border-b border-white/10 text-sm font-medium">
          <button className="text-purple-400 relative">
            最新发布
            <span className="absolute -bottom-4 left-0 w-full h-0.5 bg-purple-500"></span>
          </button>
          <button className="text-slate-400 hover:text-white transition-colors">热门讨论</button>
          <button className="text-slate-400 hover:text-white transition-colors">技术问答</button>
          <button className="text-slate-400 hover:text-white transition-colors">精华内容</button>
        </div>

        {/* Topic List */}
        <div className="flex flex-col min-h-[300px]">
          {TOPICS.length > 0 ? (
            TOPICS.map((topic, index) => (
              <motion.div 
                key={topic.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05 }}
                className="px-6 py-5 border-b border-white/5 hover:bg-white/5 transition-colors group cursor-pointer flex flex-col md:flex-row md:items-center justify-between gap-4"
              >
                <div className="flex gap-4 items-start md:items-center flex-1">
                   <div className="w-10 h-10 rounded-full bg-slate-800 border border-white/10 shrink-0 flex items-center justify-center overflow-hidden">
                      <Users className="w-5 h-5 text-slate-500" />
                   </div>
                   <div>
                     <div className="flex items-center gap-2 mb-1">
                       {topic.tags.map((tag: string) => (
                         <span key={tag} className="text-[10px] px-2 py-0.5 rounded bg-purple-500/20 text-purple-300 border border-purple-500/30">
                           {tag}
                         </span>
                       ))}
                       <span className="text-xs text-slate-500 hidden md:inline-flex ml-2">{topic.author} • {topic.time}</span>
                     </div>
                     <h3 className="text-base font-bold text-slate-200 group-hover:text-purple-300 transition-colors">
                       {topic.title}
                     </h3>
                     <span className="text-xs text-slate-500 md:hidden mt-2 inline-block">By {topic.author}</span>
                   </div>
                </div>
                
                <div className="flex items-center gap-6 text-sm text-slate-500 ml-14 md:ml-0">
                  <div className="flex items-center gap-1.5 flex-col md:flex-row">
                    <span className="flex items-center gap-1"><MessageSquare className="w-4 h-4 hidden md:block" /> {topic.replies}</span>
                    <span className="text-[10px] md:hidden">回复</span>
                  </div>
                  <div className="flex items-center gap-1.5 flex-col md:flex-row">
                    <span className="flex items-center gap-1"><ThumbsUp className="w-4 h-4 hidden md:block" /> {topic.likes}</span>
                    <span className="text-[10px] md:hidden">点赞</span>
                  </div>
                  <div className="flex items-center gap-1.5 flex-col md:flex-row hidden md:flex">
                    <span className="flex items-center gap-1"><Eye className="w-4 h-4 hidden md:block" /> {topic.views}</span>
                    <span className="text-[10px] md:hidden">浏览</span>
                  </div>
                </div>
              </motion.div>
            ))
          ) : (
            <div className="flex-1 flex items-center justify-center">
              <p className="text-slate-500">空空如也，快来发布第一篇帖子吧</p>
            </div>
          )}
        </div>
        
        {TOPICS.length > 0 && (
          <div className="p-4 text-center">
            <button className="text-sm text-slate-400 hover:text-white transition-colors">
              查看更多内容...
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
