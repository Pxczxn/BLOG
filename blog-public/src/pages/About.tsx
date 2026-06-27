import { motion } from 'motion/react';
import { Github, Mail, Twitter, ChevronRight } from 'lucide-react';
import { useState, useEffect } from 'react';
import Seo from '../components/Seo';
import {
  buildBreadcrumbJsonLd,
  buildMetaDescription,
  DEFAULT_ABOUT_SETTINGS,
  fetchSiteSettings,
  readAboutSettings,
  readSiteSettings,
  toAboutSettings,
  toAbsoluteUrl,
} from '../lib/siteSettings';

const profile = {
  username: 'pxczxn',
  displayName: '破星辰只寻你',
  avatar: '/assets/avatar.png',
  website: 'https://github.com/Pxczxn',
};

const defaultSettings = {
  bio: '一个正在学习与探索 AI Coding 的全栈开发者。这里会记录技术、设计和持续构建过程中的思考与实践。',
  bioSub: '这个博客会更偏向真实项目、踩坑记录、审美迭代，以及把想法真正落成作品的过程。',
  frontend: 'React / Vue / TypeScript / Tailwind CSS',
  backend: 'Java / Spring Boot / Node.js',
  engineering: 'Vite / Webpack / Git',
  other: 'MySQL / Redis / Linux'
};

export default function About() {
  const [settings, setSettings] = useState(DEFAULT_ABOUT_SETTINGS);
  const [siteSettings, setSiteSettings] = useState(readSiteSettings());

  useEffect(() => {
    setSettings(readAboutSettings());
    setSiteSettings(readSiteSettings());
    fetchSiteSettings()
      .then((settings) => {
        setSiteSettings(settings);
        setSettings(toAboutSettings(settings));
      })
      .catch(() => {});
  }, []);

  const pageDescription = buildMetaDescription(`${settings.bio} ${settings.bioSub}`, siteSettings.description);
  const breadcrumbJsonLd = buildBreadcrumbJsonLd([
    { name: '首页', path: '/' },
    { name: '关于', path: '/about' },
  ]);
  const profileJsonLd = {
    '@context': 'https://schema.org',
    '@type': 'Person',
    name: profile.displayName,
    alternateName: profile.username,
    url: toAbsoluteUrl('/about'),
    image: toAbsoluteUrl(profile.avatar),
    sameAs: [profile.website],
    description: pageDescription,
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-12 sm:px-6 lg:px-8">
      <Seo
        title="关于"
        description={pageDescription}
        path="/about"
        type="profile"
        image={profile.avatar}
        jsonLd={[breadcrumbJsonLd, profileJsonLd]}
      />
      <div className="relative z-10 flex flex-col gap-12 md:flex-row">
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="flex shrink-0 flex-col items-center md:w-1/3"
        >
          <div className="relative mb-6 h-48 w-48 rounded-full border-4 border-white/10 p-2">
            <div className="absolute inset-0 rounded-full bg-gradient-to-tr from-purple-600 to-blue-500 opacity-40 blur-xl" />
            <div className="relative z-10 h-full w-full overflow-hidden rounded-full border border-white/20 bg-slate-800">
              <img src={profile.avatar} alt={profile.displayName} className="h-full w-full object-cover" />
            </div>
          </div>

          <h2 className="mb-2 text-2xl font-black text-white">{profile.displayName}</h2>
          <p className="mb-8 text-sm text-slate-400">@{profile.username}</p>

          <div className="flex w-full justify-center gap-4">
            <a
              href="https://github.com/Pxczxn"
              target="_blank"
              rel="noreferrer"
              className="flex h-10 w-10 items-center justify-center rounded-full border border-white/10 bg-white/5 text-slate-500 transition-colors hover:border-white/30 hover:text-white"
            >
              <Github className="h-5 w-5" />
            </a>
            <div className="flex h-10 w-10 cursor-not-allowed items-center justify-center rounded-full border border-white/10 bg-white/5 text-slate-500">
              <Twitter className="h-5 w-5" />
            </div>
            <a
              href="mailto:Pxczxn@163.com"
              className="flex h-10 w-10 items-center justify-center rounded-full border border-white/10 bg-white/5 text-slate-500 transition-colors hover:border-white/30 hover:text-white"
            >
              <Mail className="h-5 w-5" />
            </a>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.2 }}
          className="md:w-2/3"
        >
          <div className="rounded-3xl border border-white/10 bg-white/5 p-8 backdrop-blur-md md:p-10">
            <h3 className="mb-6 flex items-center gap-2 text-xl font-bold text-white">
              <span className="h-5 w-1 rounded-full bg-purple-500" />
              关于我
            </h3>

            <div className="space-y-4 text-sm leading-relaxed text-slate-400 md:text-base">
              <p>{settings.bio}</p>
              <p>{settings.bioSub}</p>
            </div>

            <h3 className="mt-10 mb-6 flex items-center gap-2 text-xl font-bold text-white">
              <span className="h-5 w-1 rounded-full bg-blue-500" />
              技能栈
            </h3>

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="rounded-xl border border-white/5 bg-black/20 p-4">
                <h4 className="mb-2 font-medium text-white">前端技能</h4>
                <p className="text-sm text-slate-500">{settings.frontend}</p>
              </div>
              <div className="rounded-xl border border-white/5 bg-black/20 p-4">
                <h4 className="mb-2 font-medium text-white">后端技能</h4>
                <p className="text-sm text-slate-500">{settings.backend}</p>
              </div>
              <div className="rounded-xl border border-white/5 bg-black/20 p-4">
                <h4 className="mb-2 font-medium text-white">工程化</h4>
                <p className="text-sm text-slate-500">{settings.engineering}</p>
              </div>
              <div className="rounded-xl border border-white/5 bg-black/20 p-4">
                <h4 className="mb-2 font-medium text-white">其他工具</h4>
                <p className="text-sm text-slate-500">{settings.other}</p>
              </div>
            </div>

            <div className="mt-10 flex flex-wrap items-center justify-between gap-4 border-t border-white/10 pt-8">
              <p className="text-sm text-slate-500">个人链接: {profile.website}</p>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="flex items-center gap-2 font-medium text-purple-400 transition-colors hover:text-purple-300"
              >
                查看完整简介
                <ChevronRight className="h-4 w-4" />
              </motion.button>
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
