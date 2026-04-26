/**
 * 标签页
 * <p>
 * 按标签浏览文章
 */
import { useState, useEffect } from "react";
import { motion } from "motion/react";
import request from "../lib/request";

export default function Tags() {
    const [tags, setTags] = useState<any[]>([]);

    useEffect(() => {
        const fetchTags = async () => {
            try {
                const resp = await request.get("/api/public/tags");
                // @ts-ignore
                const data = resp?.data ?? resp ?? [];
                setTags(Array.isArray(data) ? data : []);
            } catch (error) {
                console.error("Failed to fetch tags:", error);
            }
        };
        fetchTags();
    }, []);

    const styles = [
        {
            color: "text-indigo-400",
            border: "border-indigo-500/30",
            bg: "bg-indigo-500/10",
        },
        {
            color: "text-emerald-400",
            border: "border-emerald-500/30",
            bg: "bg-emerald-500/10",
        },
        {
            color: "text-green-400",
            border: "border-green-500/30",
            bg: "bg-green-500/10",
        },
        {
            color: "text-blue-400",
            border: "border-blue-500/30",
            bg: "bg-blue-500/10",
        },
        {
            color: "text-cyan-400",
            border: "border-cyan-500/30",
            bg: "bg-cyan-500/10",
        },
        {
            color: "text-purple-400",
            border: "border-purple-500/30",
            bg: "bg-purple-500/10",
        },
        {
            color: "text-orange-400",
            border: "border-orange-500/30",
            bg: "bg-orange-500/10",
        },
        {
            color: "text-pink-400",
            border: "border-pink-500/30",
            bg: "bg-pink-500/10",
        },
        {
            color: "text-rose-400",
            border: "border-rose-500/30",
            bg: "bg-rose-500/10",
        },
        {
            color: "text-yellow-400",
            border: "border-yellow-500/30",
            bg: "bg-yellow-500/10",
        },
    ];

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
                    {tags.map((tag, i) => {
                        const style = styles[i % styles.length];
                        const sizeClass = "text-sm px-3 py-1";

                        return (
                            <motion.span
                                key={tag.id || tag.slug || tag.name}
                                initial={{ opacity: 0, scale: 0.8 }}
                                animate={{ opacity: 1, scale: 1 }}
                                transition={{ delay: 0.1 + i * 0.03 }}
                                className={`
                  ${sizeClass} ${style.color} ${style.bg} border ${style.border}
                  rounded-full font-medium inline-flex items-center gap-2 cursor-pointer
                  hover:-translate-y-1 hover:shadow-lg transition-transform duration-300 relative group
                `}
                            >
                                <span># {tag.name}</span>
                                <span className="opacity-60 text-[0.75em] bg-black/20 px-2 rounded-full">
                                    {tag.articleCount ?? tag.count ?? 0}
                                </span>
                            </motion.span>
                        );
                    })}
                </div>
            </motion.div>
        </div>
    );
}
