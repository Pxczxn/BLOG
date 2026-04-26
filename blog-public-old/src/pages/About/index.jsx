/*
 * 功能：前端模块逻辑。
 */
import React from 'react';
import { Mail, Github, MapPin, Code2 } from 'lucide-react';

const AboutPage = () => {
    return (
        <div className="animate-in fade-in slide-in-from-bottom-8 duration-1000 font-sans pb-16">
            <header className="mb-16">
                <h1 className="text-4xl md:text-5xl font-bold tracking-tight text-slate-100 mb-6 font-serif">关于我</h1>
                <p className="text-lg text-slate-400 leading-relaxed font-light">
                    我是一名全栈开发者，热爱构建干净、高效、有质感的 Web 应用。这里是我的数字花园，记录技术与设计上的思考。
                </p>
                <div className="flex flex-wrap items-center gap-4 mt-6 text-sm text-slate-500 font-medium">
                    <span className="flex items-center gap-1.5"><MapPin className="w-4 h-4" /> Earth</span>
                    <span className="flex items-center gap-1.5"><Code2 className="w-4 h-4" /> Full-Stack</span>
                </div>
            </header>

            <div className="space-y-16">
                <section>
                    <h2 className="text-xl font-semibold text-slate-200 mb-4 tracking-tight">背景</h2>
                    <div className="prose prose-invert prose-p:leading-relaxed prose-p:font-light prose-p:text-slate-400">
                        <p>
                            你好，我是 <strong>破星辰只寻你</strong>。这个博客项目从零搭建，包含基于 React 的前台展示和独立后台管理系统，后端由 Spring Boot 驱动。
                        </p>
                        <p>
                            我相信技术的价值在于创造更好的体验。无论是代码架构，还是界面细节，我都希望做到清晰、稳定、可持续。
                        </p>
                    </div>
                </section>

                <section>
                    <h2 className="text-xl font-semibold text-slate-200 mb-6 tracking-tight">技术栈</h2>
                    <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                        {[
                            { name: 'React', category: 'Frontend' },
                            { name: 'Vue', category: 'Frontend' },
                            { name: 'Node.js', category: 'Backend' },
                            { name: 'Java / Spring', category: 'Backend' },
                            { name: 'TailwindCSS', category: 'Design' },
                            { name: 'TypeScript', category: 'Language' },
                        ].map((tech, idx) => (
                            <div key={idx} className="p-4 rounded-xl border border-white/5 bg-white/[0.02] hover:bg-white/[0.04] transition-colors">
                                <p className="font-medium text-slate-200 text-sm">{tech.name}</p>
                                <p className="text-xs text-slate-500 mt-1">{tech.category}</p>
                            </div>
                        ))}
                    </div>
                </section>

                <section>
                    <h2 className="text-xl font-semibold text-slate-200 mb-6 tracking-tight">找到我</h2>
                    <div className="flex flex-col sm:flex-row gap-3">
                        <a href="https://github.com/Pxczxn" target="_blank" rel="noreferrer" className="flex-1 flex items-center justify-center gap-2 py-3 rounded-xl border border-white/5 bg-white/[0.02] hover:bg-white/[0.05] text-sm font-medium text-slate-300 transition-colors">
                            <Github className="w-4 h-4" /> GitHub
                        </a>
                        <a href="mailto:Pxczxn@163.com" className="flex-1 flex items-center justify-center gap-2 py-3 rounded-xl border border-white/5 bg-white/[0.02] hover:bg-white/[0.05] text-sm font-medium text-slate-300 transition-colors">
                            <Mail className="w-4 h-4" /> Email
                        </a>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default AboutPage;

