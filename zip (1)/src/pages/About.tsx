import { motion } from 'motion/react';
import { Github, Mail, Twitter, ChevronRight } from 'lucide-react';

export default function About() {
  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="flex flex-col md:flex-row gap-12 relative z-10">
        
        {/* Left Side: Avatar and Links */}
        <motion.div 
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="md:w-1/3 flex flex-col items-center flex-shrink-0"
        >
          <div className="w-48 h-48 rounded-full border-4 border-white/10 p-2 relative mb-6">
            <div className="absolute inset-0 bg-gradient-to-tr from-purple-600 to-blue-500 rounded-full blur-xl opacity-40"></div>
            <div className="w-full h-full bg-slate-800 rounded-full relative z-10 border border-white/20 flex items-center justify-center overflow-hidden">
               {/* 建议在这里通过 API 获取用户头像 */}
               <span className="text-slate-500 text-sm">暂无头像</span>
            </div>
          </div>
          
          <h2 className="text-2xl font-black text-white mb-2">个人简介</h2>
          <p className="text-slate-400 text-sm mb-8">@Username</p>
          
          <div className="flex justify-center gap-4 w-full">
            <div className="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-slate-500">
              <Github className="w-5 h-5" />
            </div>
            <div className="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-slate-500">
              <Twitter className="w-5 h-5" />
            </div>
            <div className="w-10 h-10 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-slate-500">
              <Mail className="w-5 h-5" />
            </div>
          </div>
        </motion.div>

        {/* Right Side: Bio and Skills */}
        <motion.div 
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.2 }}
          className="md:w-2/3"
        >
          <div className="bg-white/5 border border-white/10 rounded-3xl p-8 md:p-10 backdrop-blur-md">
            <h3 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
              <span className="w-1 h-5 bg-purple-500 rounded-full"></span>
              关于我
            </h3>
            
            <div className="space-y-4 text-slate-400 leading-relaxed text-sm md:text-base">
              <p>管理员暂未填写个人说明...</p>
            </div>
            
            <h3 className="text-xl font-bold text-white mt-10 mb-6 flex items-center gap-2">
              <span className="w-1 h-5 bg-blue-500 rounded-full"></span>
              技能储备
            </h3>
            
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="p-4 bg-black/20 rounded-xl border border-white/5">
                <h4 className="text-white font-medium mb-2">前端技能</h4>
                <p className="text-sm text-slate-500">等待更新...</p>
              </div>
              <div className="p-4 bg-black/20 rounded-xl border border-white/5">
                <h4 className="text-white font-medium mb-2">后端技能</h4>
                <p className="text-sm text-slate-500">等待更新...</p>
              </div>
              <div className="p-4 bg-black/20 rounded-xl border border-white/5">
                <h4 className="text-white font-medium mb-2">工程化</h4>
                <p className="text-sm text-slate-500">等待更新...</p>
              </div>
              <div className="p-4 bg-black/20 rounded-xl border border-white/5">
                <h4 className="text-white font-medium mb-2">其他工具</h4>
                <p className="text-sm text-slate-500">等待更新...</p>
              </div>
            </div>

            <div className="mt-10 pt-8 border-t border-white/10 flex justify-end">
              <motion.button 
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="flex items-center gap-2 text-purple-400 font-medium hover:text-purple-300 transition-colors"
              >
                查看完整简历 <ChevronRight className="w-4 h-4" />
              </motion.button>
            </div>
          </div>
        </motion.div>
        
      </div>
    </div>
  );
}
