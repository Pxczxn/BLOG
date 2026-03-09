import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Home, ArrowLeft } from 'lucide-react';

const NotFoundPage = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-[70vh] flex items-center justify-center font-sans">
            <div className="text-center max-w-lg mx-auto px-6">
                <div className="mb-8 relative">
                    <div className="text-[120px] md:text-[160px] font-bold leading-none select-none text-slate-800">
                        404
                    </div>
                    <div className="absolute inset-0 flex items-center justify-center">
                        <span className="text-4xl md:text-5xl font-medium text-slate-500">页面走丢了</span>
                    </div>
                </div>

                <p className="text-lg mb-12 leading-relaxed text-slate-500">
                    你访问的页面不存在或已被删除。<br />
                    让我帮你找到正确的方向。
                </p>

                <div className="flex flex-col sm:flex-row gap-4 justify-center">
                    <button onClick={() => navigate(-1)} className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-xl border border-white/10 bg-white/5 text-sm font-medium text-slate-300 hover:bg-white/10 transition-all">
                        <ArrowLeft className="w-4 h-4" />
                        返回上一页
                    </button>
                    <Link to="/" className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-xl border border-indigo-500/30 bg-indigo-500/20 text-sm font-medium text-indigo-300 hover:bg-indigo-500/30 transition-all">
                        <Home className="w-4 h-4" />
                        回到首页
                    </Link>
                </div>

                <div className="mt-16 pt-8 border-t border-white/5">
                    <p className="text-sm mb-4 text-slate-600">或者试试这些链接：</p>
                    <div className="flex flex-wrap gap-3 justify-center">
                        <Link to="/" className="text-sm text-slate-500 hover:text-indigo-400 transition-colors">全部文章</Link>
                        <span className="text-slate-700">路</span>
                        <Link to="/about" className="text-sm text-slate-500 hover:text-indigo-400 transition-colors">关于作者</Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default NotFoundPage;
