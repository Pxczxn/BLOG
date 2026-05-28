/**
 * 分类页
 * <p>
 * 按分类浏览文章
 */
import { useState, useEffect } from "react";
import { motion } from "motion/react";
import { Folder } from "lucide-react";
import Seo from '../components/Seo';
import request from "../lib/request";
import { buildBreadcrumbJsonLd, buildMetaDescription } from '../lib/siteSettings';

export default function Category() {
    const [categories, setCategories] = useState<any[]>([]);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const resp = await request.get("/api/public/categories");
                // @ts-ignore
                const data = resp?.data ?? resp ?? [];
                setCategories(Array.isArray(data) ? data : []);
            } catch (error) {
                console.error("Failed to fetch categories:", error);
            }
        };
        fetchCategories();
    }, []);

    // 预定义颜色，用于动态分配
    const colors = [
        {
            color: "from-blue-500/20 to-purple-500/20",
            border: "border-blue-500/30",
        },
        {
            color: "from-emerald-500/20 to-teal-500/20",
            border: "border-emerald-500/30",
        },
        {
            color: "from-orange-500/20 to-red-500/20",
            border: "border-orange-500/30",
        },
        {
            color: "from-pink-500/20 to-rose-500/20",
            border: "border-pink-500/30",
        },
        {
            color: "from-indigo-500/20 to-blue-500/20",
            border: "border-indigo-500/30",
        },
        {
            color: "from-purple-500/20 to-pink-500/20",
            border: "border-purple-500/30",
        },
    ];

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <Seo
                title="分类"
                description={buildMetaDescription(`浏览 ${categories.length} 个文章分类与归档。`)}
                path="/category"
                jsonLd={buildBreadcrumbJsonLd([
                    { name: '首页', path: '/' },
                    { name: '分类', path: '/category' },
                ])}
            />
            <div className="mb-12 text-center">
                <motion.h1
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="text-4xl font-black text-white mb-4 drop-shadow-[0_0_15px_rgba(168,85,247,0.4)]"
                >
                    文章分类
                </motion.h1>
                <motion.p
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.1 }}
                    className="text-slate-400"
                >
                    将知识进行系统化的整理与归档
                </motion.p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 relative z-10">
                {categories.map((cat, i) => {
                    const style = colors[i % colors.length];
                    return (
                        <motion.div
                            key={cat.id || cat.slug || cat.name}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: i * 0.1 }}
                            className={`bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-md hover:bg-white/10 transition-all cursor-pointer group relative overflow-hidden`}
                        >
                            <div
                                className={`absolute top-0 right-0 w-32 h-32 bg-gradient-to-br ${style.color} blur-2xl rounded-full opacity-50 group-hover:opacity-100 transition-opacity`}
                            ></div>
                            <div className="relative z-10">
                                <div className="flex items-center justify-between mb-4">
                                    <div
                                        className={`w-10 h-10 rounded-xl bg-white/5 border ${style.border} flex items-center justify-center text-white/80 group-hover:text-white transition-colors`}
                                    >
                                        <Folder className="w-5 h-5" />
                                    </div>
                                    <span className="text-2xl font-black text-white/20 group-hover:text-white/40 transition-colors">
                                        {cat.articleCount ?? cat.count ?? 0}
                                    </span>
                                </div>
                                <h3 className="text-xl font-bold text-slate-200 group-hover:text-purple-300 transition-colors mb-2">
                                    {cat.name}
                                </h3>
                                <p className="text-sm text-slate-500">
                                    {cat.description || "暂无描述"}
                                </p>
                            </div>
                        </motion.div>
                    );
                })}
            </div>
        </div>
    );
}
