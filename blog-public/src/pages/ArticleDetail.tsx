import { useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { motion } from 'motion/react';
import { ArrowLeft, ArrowRight, Calendar, Clock3, Copy, Eye, ListTree } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { format } from 'date-fns';
import toast from 'react-hot-toast';
import Seo from '../components/Seo';
import request, { getStaticUrl } from '../lib/request';
import { buildBreadcrumbJsonLd, buildMetaDescription, toAbsoluteUrl } from '../lib/siteSettings';

type TocItem = {
  id: string;
  text: string;
  level: 2 | 3;
};

function slugifyHeading(text: string) {
  return text
    .trim()
    .toLowerCase()
    .replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    .replace(/^-+|-+$/g, '')
    .slice(0, 80);
}

function extractText(children: any): string {
  if (typeof children === 'string' || typeof children === 'number') return String(children);
  if (Array.isArray(children)) return children.map(extractText).join('');
  if (children?.props?.children) return extractText(children.props.children);
  return '';
}

function buildToc(content = '') {
  const used = new Map<string, number>();
  return content
    .split('\n')
    .map((line) => {
      const match = /^(#{2,3})\s+(.+)$/.exec(line.trim());
      if (!match) return null;
      const text = match[2].replace(/[#`*_~[\]()]/g, '').trim();
      const baseId = slugifyHeading(text) || 'section';
      const count = used.get(baseId) || 0;
      used.set(baseId, count + 1);
      return {
        id: count > 0 ? `${baseId}-${count + 1}` : baseId,
        text,
        level: match[1].length as 2 | 3,
      };
    })
    .filter(Boolean) as TocItem[];
}

export default function ArticleDetail() {
  const { slug } = useParams();
  const [article, setArticle] = useState<any>(null);
  const [navigation, setNavigation] = useState<any>({ previous: null, next: null });
  const [loading, setLoading] = useState(true);
  const [readingProgress, setReadingProgress] = useState(0);

  useEffect(() => {
    const fetchArticle = async () => {
      try {
        setLoading(true);
        const res: any = await request.get(`/api/public/articles/${slug}`);
        const data = res?.data ?? res;
        setArticle(data);

        request.post(`/api/public/articles/${slug}/view`).catch(console.error);

        const navRes: any = await request.get(`/api/public/articles/${slug}/navigation`);
        setNavigation(navRes?.data ?? navRes ?? { previous: null, next: null });
      } catch (error) {
        console.error('Failed to fetch article:', error);
        setArticle(null);
      } finally {
        setLoading(false);
      }
    };

    if (slug) {
      fetchArticle();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [slug]);

  useEffect(() => {
    const updateProgress = () => {
      const scrollTop = window.scrollY;
      const height = document.documentElement.scrollHeight - window.innerHeight;
      setReadingProgress(height > 0 ? Math.min(100, Math.max(0, (scrollTop / height) * 100)) : 0);
    };

    updateProgress();
    window.addEventListener('scroll', updateProgress, { passive: true });
    window.addEventListener('resize', updateProgress);
    return () => {
      window.removeEventListener('scroll', updateProgress);
      window.removeEventListener('resize', updateProgress);
    };
  }, []);

  const articlePath = `/post/${article?.slug || slug || ''}`;
  const articleDescription = buildMetaDescription(article?.summary || article?.content || '');
  const articleImage = article?.coverImage || article?.cover ? getStaticUrl(article.coverImage || article.cover) : undefined;
  const articleTags = Array.isArray(article?.tags)
    ? article.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')).filter(Boolean)
    : [];
  const toc = useMemo(() => buildToc(article?.content || ''), [article?.content]);
  const readingMinutes = Math.max(1, Math.ceil(String(article?.content || '').replace(/\s+/g, '').length / 500));

  const copyArticleLink = async () => {
    await navigator.clipboard.writeText(toAbsoluteUrl(articlePath));
    toast.success('文章链接已复制');
  };

  if (loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="animate-pulse text-purple-400">正在加载内容...</div>
      </div>
    );
  }

  if (!article) {
    return (
      <div className="flex min-h-[50vh] flex-col items-center justify-center gap-4 text-slate-400">
        <Seo title="文章不存在" description="请求的文章不存在或已经被删除。" path={slug ? `/post/${slug}` : '/blog'} noindex />
        <p>文章不存在或已经被删除</p>
        <Link to="/blog" className="text-purple-400 hover:underline">
          返回博客
        </Link>
      </div>
    );
  }

  const breadcrumbJsonLd = buildBreadcrumbJsonLd([
    { name: '首页', path: '/' },
    { name: '博客', path: '/blog' },
    { name: article.title, path: articlePath },
  ]);
  const articleJsonLd = {
    '@context': 'https://schema.org',
    '@type': 'Article',
    headline: article.title,
    description: articleDescription,
    image: articleImage ? [toAbsoluteUrl(articleImage)] : undefined,
    datePublished: article.createdAt,
    dateModified: article.updatedAt || article.createdAt,
    mainEntityOfPage: toAbsoluteUrl(articlePath),
    author: {
      '@type': 'Person',
      name: '破星辰只寻你',
    },
    keywords: articleTags,
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <div className="fixed left-0 top-0 z-[70] h-1 bg-purple-400 transition-[width]" style={{ width: `${readingProgress}%` }} />
      <Seo
        title={article.title}
        description={articleDescription}
        path={articlePath}
        type="article"
        image={articleImage}
        publishedTime={article.createdAt}
        modifiedTime={article.updatedAt || article.createdAt}
        tags={articleTags}
        jsonLd={[breadcrumbJsonLd, articleJsonLd]}
      />

      <motion.article
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="grid gap-8 lg:grid-cols-[minmax(0,1fr)_280px]"
      >
        <div className="min-w-0">
          <header className="mb-8 text-center">
            <Link to="/blog" className="mb-6 inline-flex items-center gap-2 text-sm text-slate-400 transition hover:text-white">
              <ArrowLeft className="h-4 w-4" />
              返回博客
            </Link>

            {articleTags.length > 0 ? (
              <div className="mb-4 flex flex-wrap justify-center gap-2">
                {articleTags.map((tag: string) => (
                  <span key={tag} className="rounded border border-white/10 bg-white/5 px-2.5 py-1 text-xs text-purple-300">
                    {tag}
                  </span>
                ))}
              </div>
            ) : null}

            <h1 className="mx-auto mb-6 max-w-4xl text-3xl font-black leading-tight text-white drop-shadow-[0_0_15px_rgba(168,85,247,0.3)] md:text-5xl">
              {article.title}
            </h1>

            <div className="flex flex-wrap items-center justify-center gap-5 text-sm text-slate-400">
              <span className="flex items-center gap-1.5">
                <Calendar className="h-4 w-4" />
                {format(new Date(article.createdAt || Date.now()), 'yyyy-MM-dd')}
              </span>
              <span className="flex items-center gap-1.5">
                <Clock3 className="h-4 w-4" />
                约 {readingMinutes} 分钟
              </span>
              <span className="flex items-center gap-1.5">
                <Eye className="h-4 w-4" />
                {article.viewCount || 0} 阅读
              </span>
              <button type="button" onClick={copyArticleLink} className="flex items-center gap-1.5 transition hover:text-white">
                <Copy className="h-4 w-4" />
                复制链接
              </button>
            </div>
          </header>

          {articleImage ? (
            <div className="relative mb-8 h-64 w-full overflow-hidden rounded-2xl border border-white/10 shadow-2xl md:h-96">
              <div className="absolute inset-0 z-10 bg-gradient-to-t from-black/50 to-transparent" />
              <img src={articleImage} alt={article.title} className="h-full w-full object-cover" />
            </div>
          ) : null}

          <div className="rounded-2xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-sm md:p-10">
            <div
              className="prose prose-invert prose-purple max-w-none
                          prose-headings:scroll-mt-24 prose-headings:font-bold prose-headings:text-slate-100
                          prose-p:text-slate-300 prose-p:leading-relaxed
                          prose-a:text-purple-400 hover:prose-a:text-purple-300
                          prose-code:rounded prose-code:bg-pink-500/10 prose-code:px-1.5 prose-code:py-0.5 prose-code:text-pink-300
                          prose-pre:border prose-pre:border-white/10 prose-pre:bg-slate-900/80
                          prose-blockquote:bg-slate-900/80
                          prose-img:rounded-2xl prose-img:border prose-img:border-white/10"
            >
              <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                components={{
                  h2({ children }) {
                    const text = extractText(children);
                    const item = toc.find((entry) => entry.text === text && entry.level === 2);
                    return <h2 id={item?.id}>{children}</h2>;
                  },
                  h3({ children }) {
                    const text = extractText(children);
                    const item = toc.find((entry) => entry.text === text && entry.level === 3);
                    return <h3 id={item?.id}>{children}</h3>;
                  },
                }}
              >
                {article.content || '暂无内容'}
              </ReactMarkdown>
            </div>
          </div>

          <div className="mt-10 grid grid-cols-1 gap-4 md:grid-cols-2">
            {navigation?.previous ? (
              <Link
                to={`/post/${navigation.previous.slug || navigation.previous.id}`}
                className="group flex items-center gap-4 rounded-2xl border border-white/10 bg-white/5 p-5 text-left transition-all hover:bg-white/10"
              >
                <ArrowLeft className="h-5 w-5 text-purple-400 transition-transform group-hover:-translate-x-1" />
                <div>
                  <p className="mb-1 text-xs text-slate-500">上一篇</p>
                  <p className="line-clamp-1 text-sm font-medium text-slate-200">{navigation.previous.title}</p>
                </div>
              </Link>
            ) : (
              <div />
            )}

            {navigation?.next ? (
              <Link
                to={`/post/${navigation.next.slug || navigation.next.id}`}
                className="group flex items-center justify-end gap-4 rounded-2xl border border-white/10 bg-white/5 p-5 text-right transition-all hover:bg-white/10"
              >
                <div>
                  <p className="mb-1 text-xs text-slate-500">下一篇</p>
                  <p className="line-clamp-1 text-sm font-medium text-slate-200">{navigation.next.title}</p>
                </div>
                <ArrowRight className="h-5 w-5 text-purple-400 transition-transform group-hover:translate-x-1" />
              </Link>
            ) : (
              <div />
            )}
          </div>
        </div>

        <aside className="hidden lg:block">
          <div className="sticky top-24 space-y-4">
            <section className="rounded-2xl border border-white/10 bg-white/[0.03] p-5 backdrop-blur-xl">
              <div className="mb-4 flex items-center gap-2 text-sm font-semibold text-white">
                <ListTree className="h-4 w-4 text-purple-300" />
                文章目录
              </div>
              {toc.length > 0 ? (
                <nav className="space-y-2">
                  {toc.map((item) => (
                    <a
                      key={item.id}
                      href={`#${item.id}`}
                      className={`block rounded-lg px-3 py-2 text-sm text-slate-400 transition hover:bg-white/5 hover:text-white ${
                        item.level === 3 ? 'ml-4' : ''
                      }`}
                    >
                      {item.text}
                    </a>
                  ))}
                </nav>
              ) : (
                <p className="text-sm leading-6 text-slate-500">这篇文章暂时没有二级或三级标题。</p>
              )}
            </section>

            <section className="rounded-2xl border border-white/10 bg-white/[0.03] p-5 text-sm text-slate-400 backdrop-blur-xl">
              <div className="mb-3 font-semibold text-white">阅读状态</div>
              <div className="h-2 overflow-hidden rounded-full bg-white/10">
                <div className="h-full rounded-full bg-purple-400 transition-[width]" style={{ width: `${readingProgress}%` }} />
              </div>
              <p className="mt-3">{Math.round(readingProgress)}% 已读</p>
            </section>
          </div>
        </aside>
      </motion.article>
    </div>
  );
}
