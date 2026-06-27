import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'motion/react';
import { FileText } from 'lucide-react';
import ArticleCard, { type Article } from '../components/ArticleCard';
import Sidebar from '../components/Sidebar';
import EmptyState from '../components/EmptyState';
import Seo from '../components/Seo';
import request, { getStaticUrl } from '../lib/request';
import { buildBreadcrumbJsonLd, buildMetaDescription, fetchSiteSettings, readSiteSettings, toAbsoluteUrl } from '../lib/siteSettings';

export default function Home() {
  const [articles, setArticles] = useState<Article[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [siteSettings, setSiteSettings] = useState(readSiteSettings());

  useEffect(() => {
    fetchSiteSettings().then(setSiteSettings).catch(() => {});

    const fetchArticles = async () => {
      try {
        setLoading(true);
        setError(null);

        const resp = await request.get('/api/public/articles', {
          params: { page: 1, size: 10 },
        });

        const result: any = resp;
        const pageData = result?.data ?? result ?? { items: [] };
        const items = Array.isArray(pageData.items) ? pageData.items : [];

        const normalized: Article[] = items.map((item: any) => {
          const tags = Array.isArray(item.tags)
            ? item.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')).filter(Boolean)
            : [];
          const categoryName = item.category?.name ? [item.category.name] : [];

          return {
            id: item.slug || String(item.id),
            slug: item.slug || undefined,
            title: item.title || '无标题',
            summary: item.summary || '',
            coverImage: getStaticUrl(item.coverImage || ''),
            tags: [...categoryName, ...tags],
            views: item.viewCount || 0,
            publishedAt: item.createdAt
              ? new Date(item.createdAt).toLocaleDateString('zh-CN', {
                  year: 'numeric',
                  month: '2-digit',
                  day: '2-digit',
                })
              : '未知时间',
            isPinned: item.isPinned || false,
          };
        });

        setArticles(normalized);
      } catch (err) {
        setArticles([]);
        setError('后端暂时没有返回文章。');
      } finally {
        setLoading(false);
      }
    };

    fetchArticles();
  }, []);

  return (
    <div className="mx-auto max-w-7xl py-8">
      <Seo
        description={buildMetaDescription(siteSettings.siteSub, siteSettings.description)}
        path="/"
        image="/assets/avatar.png"
        jsonLd={[
          {
            '@context': 'https://schema.org',
            '@type': 'WebSite',
            name: siteSettings.siteName,
            description: buildMetaDescription(siteSettings.siteSub, siteSettings.description),
            url: toAbsoluteUrl('/'),
            inLanguage: 'zh-CN',
          },
          {
            '@context': 'https://schema.org',
            '@type': 'Organization',
            name: siteSettings.siteName,
            url: toAbsoluteUrl('/'),
            logo: toAbsoluteUrl('/favicon.ico'),
          },
          buildBreadcrumbJsonLd([{ name: '首页', path: '/' }]),
        ]}
      />
      <section className="relative z-10 px-4 py-10 text-center sm:px-8 lg:px-12">
        <motion.div
          initial={{ scale: 0.9, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.7 }}
          className="flex flex-col items-center"
        >
          <h1 className="mb-4 text-4xl font-black tracking-tight text-white drop-shadow-[0_0_15px_rgba(168,85,247,0.4)] md:text-5xl">
            {siteSettings.homeTitle}
          </h1>

          <p className="mb-6 max-w-2xl text-base text-slate-400 md:text-lg">
            {siteSettings.homeIntro}
          </p>

          <Link
            to="/blog"
            className="rounded-full border border-indigo-400/30 bg-indigo-600 px-8 py-2.5 font-semibold text-white shadow-[0_0_20px_rgba(79,70,229,0.4)] transition-all hover:bg-indigo-500"
          >
            阅读文章
          </Link>
        </motion.div>
      </section>

      <main
        className={[
          'relative z-10 grid grid-cols-1 gap-6 px-4 md:px-8',
          !loading && !error && articles.length === 0 ? 'md:grid-cols-1' : 'md:grid-cols-12',
        ].join(' ')}
      >
        <div className={['flex flex-col gap-4 overflow-hidden', !loading && !error && articles.length === 0 ? '' : 'md:col-span-8'].join(' ')}>
          <div className="mb-2 flex items-center justify-between">
            <h2 className="flex items-center gap-2 text-lg font-semibold text-white">
              <span className="h-5 w-1 rounded-full bg-purple-500" />
              最新文章
            </h2>
          </div>

          <div className="flex flex-col gap-4">
            {loading ? (
              <div className="rounded-2xl border border-white/5 bg-white/5 py-12 text-center">
                <p className="text-slate-400">正在获取文章...</p>
              </div>
            ) : error ? (
              <div className="rounded-2xl border border-amber-400/20 bg-amber-400/5 px-4 py-3 text-sm text-amber-100/80">
                {error}
              </div>
            ) : articles.length > 0 ? (
              articles.map((article, index) => (
                <ArticleCard key={article.id} article={article} delay={index * 0.1} />
              ))
            ) : (
              <EmptyState
                icon={FileText}
                title="文章正在整理中"
                description="这里会慢慢放技术笔记、项目记录和一些踩坑复盘。先逛逛资源页，或者去交流区留个想法。"
                actions={[
                  { label: '浏览资源', to: '/resources' },
                  { label: '去交流区', to: '/community', variant: 'ghost' },
                ]}
              />
            )}
          </div>
        </div>

        {(loading || error || articles.length > 0) && (
          <aside className="flex flex-col gap-6 md:col-span-4">
            <Sidebar />
          </aside>
        )}
      </main>
    </div>
  );
}
