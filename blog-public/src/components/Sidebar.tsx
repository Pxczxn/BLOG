import { useEffect, useState } from 'react';
import { motion } from 'motion/react';
import request from '../lib/request';

export default function Sidebar() {
  const [categories, setCategories] = useState<any[]>([]);
  const [tags, setTags] = useState<any[]>([]);
  const [stats, setStats] = useState({
    articleCount: 0,
    categoryCount: 0,
    tagCount: 0,
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [articlesRes, categoriesRes, tagsRes] = await Promise.all([
          request.get('/api/public/articles?page=1&size=1'),
          request.get('/api/public/categories'),
          request.get('/api/public/tags'),
        ]);

        const catsData: any[] = Array.isArray(categoriesRes?.data ?? categoriesRes)
          ? (categoriesRes?.data ?? categoriesRes)
          : [];
        const tagsData: any[] = Array.isArray(tagsRes?.data ?? tagsRes)
          ? (tagsRes?.data ?? tagsRes)
          : [];
        const articlesData: any = articlesRes?.data ?? articlesRes ?? { total: 0 };

        setCategories(catsData.slice(0, 5));
        setTags(tagsData.slice(0, 10).map((item: any) => item.name || item));
        setStats({
          articleCount: articlesData.total || 0,
          categoryCount: catsData.length,
          tagCount: tagsData.length,
        });
      } catch (error) {
        // 静默失败，使用空数据
        setCategories([]);
        setTags([]);
        setStats({
          articleCount: 0,
          categoryCount: 0,
          tagCount: 0,
        });
      }
    };

    fetchData();
  }, []);

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        className="relative overflow-hidden rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-xl"
      >
        <div className="absolute top-0 right-0 h-24 w-24 rounded-full bg-purple-500/10 blur-2xl" />
        <div className="relative z-10 mb-4 flex items-center gap-4">
          <div className="h-14 w-14 overflow-hidden rounded-full border-2 border-purple-500 p-0.5 bg-slate-800">
            <img
              src="/assets/avatar.png"
              alt="Avatar"
              onError={(e) => {
                e.currentTarget.src =
                  'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&h=200&q=80';
              }}
              className="h-full w-full rounded-full object-cover"
            />
          </div>
          <div>
            <h4 className="text-lg font-bold text-white">破星辰只寻你</h4>
            <p className="text-xs text-slate-400">记录技术思考与持续构建过程</p>
          </div>
        </div>

        <div className="relative z-10 grid grid-cols-3 gap-2 border-t border-white/10 pt-4 text-center">
          <div>
            <p className="text-lg font-bold text-white">{stats.articleCount}</p>
            <p className="text-[10px] uppercase tracking-tighter text-slate-500">文章</p>
          </div>
          <div>
            <p className="text-lg font-bold text-white">{stats.categoryCount}</p>
            <p className="text-[10px] uppercase tracking-tighter text-slate-500">分类</p>
          </div>
          <div>
            <p className="text-lg font-bold text-white">{stats.tagCount}</p>
            <p className="text-[10px] uppercase tracking-tighter text-slate-500">标签</p>
          </div>
        </div>
      </motion.div>

      <motion.div
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ delay: 0.1 }}
        className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-xl"
      >
        <h4 className="mb-4 flex items-center justify-between text-sm font-bold uppercase tracking-wider text-white">
          <span>文章分类</span>
          <span className="text-[10px] text-purple-400">ALL</span>
        </h4>
        {categories.length > 0 ? (
          <ul className="space-y-2">
            {categories.map((cat, index) => (
              <li key={cat.id || cat.slug || cat.name || index} className="group flex cursor-pointer items-center justify-between text-sm">
                <span className="text-slate-400 transition-colors group-hover:text-white">{cat.name}</span>
                <span className="rounded bg-white/10 px-2 py-0.5 text-[10px] text-slate-300">
                  {cat.articleCount ?? cat.count ?? 0}
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-sm text-slate-500">暂无分类</p>
        )}
      </motion.div>

      <motion.div
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ delay: 0.2 }}
        className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-xl"
      >
        <h4 className="mb-4 text-sm font-bold uppercase tracking-wider text-white">标签云</h4>
        {tags.length > 0 ? (
          <div className="flex flex-wrap gap-2">
            {tags.map((tag, index) => (
              <span
                key={`${tag}-${index}`}
                className="cursor-pointer rounded-full border border-white/10 bg-white/5 px-3 py-1 text-[10px] text-slate-300 transition-all hover:border-purple-500/50 hover:text-purple-300"
              >
                {tag}
              </span>
            ))}
          </div>
        ) : (
          <p className="text-sm text-slate-500">暂无标签</p>
        )}
      </motion.div>
    </div>
  );
}