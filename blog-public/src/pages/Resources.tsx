import type { ReactNode } from 'react';
import { motion } from 'motion/react';
import { ExternalLink, Download, Wrench, Layout, Globe } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';

type ResourceItem = {
  name: string;
  desc: string;
  link: string;
  type: 'tool' | 'download' | 'link';
};

type ResourceSection = {
  key: 'tools' | 'assets' | 'nav';
  title: string;
  subtitle: string;
  icon: ReactNode;
  items: ResourceItem[];
};

const RESOURCE_SECTIONS: ResourceSection[] = [
  {
    key: 'tools',
    title: '在线工具',
    subtitle: '站内常用工具集合',
    icon: <Wrench className="h-5 w-5 text-blue-400" />,
    items: [
      { name: '代码格式化', desc: 'Prettier 在线格式化与配置预览', link: 'https://prettier.io/playground/', type: 'tool' },
      { name: 'JSON 转换', desc: 'JSON 校验、格式化与压缩', link: 'https://jsonformatter.org/', type: 'tool' },
      { name: '图片在线压缩', desc: 'Squoosh 多格式图片压缩工具', link: 'https://squoosh.app/', type: 'tool' },
    ],
  },
  {
    key: 'assets',
    title: '精选素材',
    subtitle: '高质量设计与开发素材',
    icon: <Layout className="h-5 w-5 text-purple-400" />,
    items: [
      { name: 'UI 设计模板', desc: 'Figma Community 设计资源', link: 'https://www.figma.com/community', type: 'download' },
      { name: '壁纸图库', desc: 'Unsplash 高质量壁纸图库', link: 'https://unsplash.com/wallpapers', type: 'download' },
      { name: '图标资源包', desc: 'Lucide 常用线性图标库', link: 'https://lucide.dev/icons/', type: 'download' },
    ],
  },
  {
    key: 'nav',
    title: '技术导航',
    subtitle: '高频技术站点与入口',
    icon: <Globe className="h-5 w-5 text-emerald-400" />,
    items: [
      { name: '前端文档', desc: 'MDN Web 文档中文入口', link: 'https://developer.mozilla.org/zh-CN/docs/Web', type: 'link' },
      { name: '开源社区', desc: 'GitHub Explore 开源项目入口', link: 'https://github.com/explore', type: 'link' },
    ],
  },
];

export default function Resources() {
  const { type } = useParams();
  const selectedSection = type ? RESOURCE_SECTIONS.find((section) => section.key === type) ?? null : null;
  const sections = selectedSection ? [selectedSection] : RESOURCE_SECTIONS;

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <div className="mb-10 text-center">
        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-5 text-5xl font-black text-white drop-shadow-[0_0_15px_rgba(168,85,247,0.4)]"
        >
          {selectedSection ? selectedSection.title : '资源宝库'}
        </motion.h1>
        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="mx-auto max-w-2xl text-lg text-slate-400"
        >
          {selectedSection
            ? selectedSection.subtitle
            : '搜集并整理互联网上的优质工具与素材，为你节省每一个寻找的时间。'}
        </motion.p>

        <div className="mx-auto mt-6 inline-flex flex-wrap items-center justify-center gap-1 rounded-full border border-white/10 bg-white/5 p-1">
          {RESOURCE_SECTIONS.map((section) => {
            const active = selectedSection?.key === section.key;
            return (
              <Link
                key={section.key}
                to={section.key === 'tools' ? '/resources/tools' : section.key === 'assets' ? '/resources/assets' : '/resources/nav'}
                className={[
                  'rounded-full px-4 py-2 text-sm transition-colors',
                  active
                    ? 'bg-purple-500/25 text-purple-100 shadow-[0_0_18px_rgba(168,85,247,0.18)]'
                    : 'text-slate-300 hover:bg-white/5 hover:text-white',
                ].join(' ')}
              >
                {section.title}
              </Link>
            );
          })}
        </div>
      </div>

      <div className="grid grid-cols-1 gap-8 relative z-10">
        {sections.map((group) => (
          <motion.section
            key={group.key}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25 }}
          >
            {!selectedSection && (
              <div className="mb-6 flex items-center gap-3 px-2">
                <div className="rounded-lg border border-white/10 bg-white/5 p-2">
                  {group.icon}
                </div>
                <h2 className="text-xl font-bold text-slate-100">{group.title}</h2>
              </div>
            )}

            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
              {group.items.map((item) => (
                <motion.a
                  key={item.name}
                  href={item.link}
                  target="_blank"
                  rel="noreferrer"
                  whileHover={{ scale: 1.01 }}
                  className="group flex h-full cursor-pointer items-center justify-between rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur-md transition-all hover:border-purple-500/30 hover:bg-white/10"
                >
                  <div className="flex-1">
                    <h3 className="mb-1 font-bold text-slate-200 transition-colors group-hover:text-purple-300">
                      {item.name}
                    </h3>
                    <p className="text-xs text-slate-500 line-clamp-1">{item.desc}</p>
                  </div>
                  <div className="ml-4 rounded-xl bg-white/5 p-2 text-slate-500 transition-all group-hover:bg-purple-500/20 group-hover:text-purple-400">
                    {item.type === 'download' ? (
                      <Download className="h-4 w-4" />
                    ) : (
                      <ExternalLink className="h-4 w-4" />
                    )}
                  </div>
                </motion.a>
              ))}
            </div>
          </motion.section>
        ))}
      </div>
    </div>
  );
}
