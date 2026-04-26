import { motion } from 'motion/react';

const TAGS = [
  { name: 'React', count: 42, color: 'text-indigo-400', border: 'border-indigo-500/30', bg: 'bg-indigo-500/10' },
  { name: 'Vue', count: 28, color: 'text-emerald-400', border: 'border-emerald-500/30', bg: 'bg-emerald-500/10' },
  { name: 'Node.js', count: 35, color: 'text-green-400', border: 'border-green-500/30', bg: 'bg-green-500/10' },
  { name: 'TypeScript', count: 56, color: 'text-blue-400', border: 'border-blue-500/30', bg: 'bg-blue-500/10' },
  { name: 'Tailwind CSS', count: 22, color: 'text-cyan-400', border: 'border-cyan-500/30', bg: 'bg-cyan-500/10' },
  { name: 'Vite', count: 18, color: 'text-purple-400', border: 'border-purple-500/30', bg: 'bg-purple-500/10' },
  { name: 'MySQL', count: 14, color: 'text-orange-400', border: 'border-orange-500/30', bg: 'bg-orange-500/10' },
  { name: 'Docker', count: 20, color: 'text-blue-500', border: 'border-blue-600/30', bg: 'bg-blue-600/10' },
  { name: 'CSS', count: 30, color: 'text-pink-400', border: 'border-pink-500/30', bg: 'bg-pink-500/10' },
  { name: 'HTML5', count: 15, color: 'text-orange-500', border: 'border-orange-600/30', bg: 'bg-orange-600/10' },
  { name: '算法', count: 40, color: 'text-rose-400', border: 'border-rose-500/30', bg: 'bg-rose-500/10' },
  { name: '设计模式', count: 12, color: 'text-fuchsia-400', border: 'border-fuchsia-500/30', bg: 'bg-fuchsia-500/10' },
  { name: '性能优化', count: 25, color: 'text-yellow-400', border: 'border-yellow-500/30', bg: 'bg-yellow-500/10' },
  { name: '微前端', count: 8, color: 'text-violet-400', border: 'border-violet-500/30', bg: 'bg-violet-500/10' },
  { name: 'GraphQL', count: 10, color: 'text-pink-500', border: 'border-pink-600/30', bg: 'bg-pink-600/10' },
];

export default function Tags() {
  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-12 text-center">
        <motion.h1 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-4xl font-black text-white mb-4 drop-shadow-[0_0_15px_rgba(168,85,247,0.4)]"
        >
          标签云
        </motion.h1>
        <motion.p 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="text-slate-400"
        >
          通过标签碎片化索引知识
        </motion.p>
      </div>

      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-white/5 border border-white/10 rounded-3xl p-8 md:p-12 backdrop-blur-xl relative z-10"
      >
        <div className="flex flex-wrap items-center justify-center gap-4 md:gap-6">
          {TAGS.map((tag, i) => {
            // Since we don't have real counts yet, make them all the same size class for now
            const sizeClass = 'text-sm px-3 py-1';

            return (
              <motion.span
                key={tag.name}
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.1 + (i * 0.03) }}
                className={`
                  ${sizeClass} ${tag.color} ${tag.bg} border ${tag.border}
                  rounded-full font-medium inline-flex items-center gap-2 cursor-pointer
                  hover:-translate-y-1 hover:shadow-lg transition-transform duration-300 relative group
                `}
              >
                <span># {tag.name}</span>
                <span className="opacity-60 text-[0.75em] bg-black/20 px-2 rounded-full">
                  --
                </span>
              </motion.span>
            );
          })}
        </div>
      </motion.div>
    </div>
  );
}
