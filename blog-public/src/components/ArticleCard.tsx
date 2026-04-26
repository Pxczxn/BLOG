/**
 * 文章卡片组件
 * <p>
 * 展示文章摘要的卡片组件
 */
import { Link } from 'react-router-dom';
import { Eye, Clock } from 'lucide-react';
import { motion } from 'motion/react';
import { cn } from '../lib/utils';

export interface Article {
  id: string;
  title: string;
  summary?: string;
  coverImage: string;
  tags: string[];
  views: number;
  publishedAt: string;
  isPinned?: boolean;
}

export default function ArticleCard({ article, delay = 0 }: { article: Article; delay?: number }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay }}
    >
      <Link 
        to={`/post/${article.id}`} 
        className="bg-white/[0.02] border border-white/5 rounded-3xl p-5 flex flex-col sm:flex-row gap-6 backdrop-blur-sm hover:bg-white/[0.05] hover:border-white/10 transition-all cursor-pointer group relative"
      >
        {article.isPinned && (
          <div className="absolute top-2 left-2 z-10 bg-purple-500/80 backdrop-blur text-[10px] uppercase tracking-wider font-semibold px-2 py-0.5 rounded text-white border border-purple-400/30">
            置顶
          </div>
        )}
        <div className="w-full sm:w-48 h-48 sm:h-32 rounded-xl bg-slate-800 shrink-0 overflow-hidden relative border border-white/5">
          <div className="absolute inset-0 bg-gradient-to-br from-purple-900/40 to-blue-900/40 z-10"></div>
          <img 
            src={article.coverImage} 
            alt={article.title}
            className="w-full h-full object-cover relative z-0 transition-transform duration-500 group-hover:scale-105 opacity-80 mix-blend-screen"
          />
        </div>
        <div className="flex flex-col justify-between py-1 flex-1">
          <div>
            <h3 className="text-xl font-bold group-hover:text-purple-300 transition-colors line-clamp-2">
              {article.title}
            </h3>
            {article.summary && (
              <p className="mt-2 line-clamp-2 text-sm leading-6 text-slate-400">
                {article.summary}
              </p>
            )}
            <div className="mt-3 flex flex-wrap gap-2">
              {article.tags.map((tag, i) => (
                <span key={tag} className={cn(
                  "px-2 py-0.5 rounded-md text-[10px] border",
                  i % 2 === 0 ? "bg-purple-500/20 text-purple-300 border-purple-500/30" : "bg-blue-500/20 text-blue-300 border-blue-500/30"
                )}>
                  {tag}
                </span>
              ))}
            </div>
          </div>
          
          <div className="flex items-center gap-6 text-sm text-slate-500 mt-4 sm:mt-0">
             <div className="flex items-center gap-1.5">
               <Clock className="w-4 h-4" />
               <span>{article.publishedAt}</span>
             </div>
             <div className="flex items-center gap-1.5">
               <Eye className="w-4 h-4" />
               <span>{article.views.toLocaleString()} 阅读</span>
             </div>
          </div>
        </div>
      </Link>
    </motion.div>
  );
}
