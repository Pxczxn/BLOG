import { motion } from 'motion/react';
import ArticleCard, { Article } from '../components/ArticleCard';
import Sidebar from '../components/Sidebar';

// 此处应通过 API 获取数据，目前为空数组
const ARTICLES: Article[] = [];

export default function Home() {
  return (
    <div className="max-w-7xl mx-auto py-8">
      {/* Hero Section */}
      <section className="relative z-10 px-12 py-10 flex flex-col items-center text-center">
        <motion.div
          initial={{ scale: 0.9, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.7 }}
          className="flex flex-col items-center"
        >
          <h1 className="text-5xl font-black tracking-tight mb-4 text-white drop-shadow-[0_0_15px_rgba(168,85,247,0.4)]">
            探索技术边界
          </h1>
          
          <p className="text-lg text-slate-400 max-w-2xl mb-6">
            分享前端开发、架构设计与工程化经验
          </p>
          
          <button className="px-8 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-full font-semibold transition-all shadow-[0_0_20px_rgba(79,70,229,0.4)] border border-indigo-400/30">
            阅读文章
          </button>
        </motion.div>
      </section>

      {/* Main Content Layout */}
      <main className="relative z-10 px-8 grid grid-cols-1 md:grid-cols-12 gap-6">
        {/* Left: Article List */}
        <div className="md:col-span-8 flex flex-col gap-4 overflow-hidden">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-lg font-semibold flex items-center gap-2 text-white">
              <span className="w-1 h-5 bg-purple-500 rounded-full"></span>
              最新文章
            </h2>
          </div>
          
          <div className="flex flex-col gap-4">
            {ARTICLES.length > 0 ? (
              ARTICLES.map((article, index) => (
                <ArticleCard key={article.id} article={article} delay={index * 0.1} />
              ))
            ) : (
              <div className="py-12 text-center border border-white/5 bg-white/5 rounded-2xl glass">
                <p className="text-slate-400">暂无文章，敬请期待</p>
              </div>
            )}
          </div>
        </div>
        
        {/* Right: Sidebar */}
        <aside className="md:col-span-4 flex flex-col gap-6">
          <Sidebar />
        </aside>
      </main>
    </div>
  );
}
