import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { motion } from 'motion/react';
import { Eye, Calendar, ArrowLeft, ArrowRight } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { format } from 'date-fns';
import Seo from '../components/Seo';
import request, { getStaticUrl } from '../lib/request';
import { buildBreadcrumbJsonLd, buildMetaDescription, toAbsoluteUrl } from '../lib/siteSettings';

export default function ArticleDetail() {
  const { slug } = useParams();
  const [article, setArticle] = useState<any>(null);
  const [navigation, setNavigation] = useState<any>({ previous: null, next: null });
  const [loading, setLoading] = useState(true);

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
        <Seo title="文章不存在" description="请求的文章不存在或已被删除。" path={slug ? `/post/${slug}` : '/blog'} noindex />
        <p>文章不存在或已被删除</p>
        <Link to="/blog" className="text-purple-400 hover:underline">
          返回博客
        </Link>
      </div>
    );
  }

  const articlePath = `/post/${article.slug || slug}`;
  const articleDescription = buildMetaDescription(article.summary || article.content);
  const articleImage = article.coverImage || article.cover ? getStaticUrl(article.coverImage || article.cover) : undefined;
  const articleTags = Array.isArray(article.tags)
    ? article.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')).filter(Boolean)
    : [];
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
    dateModified: article.createdAt,
    mainEntityOfPage: toAbsoluteUrl(articlePath),
    author: {
      '@type': 'Person',
      name: '破星辰只寻你',
    },
    keywords: articleTags,
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-10 sm:px-6 lg:px-8">
      <Seo
        title={article.title}
        description={articleDescription}
        path={articlePath}
        type="article"
        image={articleImage}
        publishedTime={article.createdAt}
        modifiedTime={article.createdAt}
        tags={articleTags}
        jsonLd={[breadcrumbJsonLd, articleJsonLd]}
      />

      <motion.article initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
        <header className="mb-10 text-center">
          <div className="mb-4 flex justify-center gap-2">
            {articleTags.map((tag: string) => (
              <span
                key={tag}
                className="rounded border border-white/10 bg-white/5 px-2.5 py-1 text-xs text-purple-300"
              >
                {tag}
              </span>
            ))}
          </div>

          <h1 className="mb-6 text-3xl font-black leading-tight text-white drop-shadow-[0_0_15px_rgba(168,85,247,0.3)] md:text-5xl">
            {article.title}
          </h1>

          <div className="flex items-center justify-center gap-6 text-sm text-slate-400">
            <span className="flex items-center gap-1.5">
              <Calendar className="h-4 w-4" />
              {format(new Date(article.createdAt || Date.now()), 'yyyy-MM-dd')}
            </span>
            <span className="flex items-center gap-1.5">
              <Eye className="h-4 w-4" />
              {article.viewCount || 0} 阅读
            </span>
          </div>
        </header>

        {articleImage ? (
          <div className="relative mb-12 h-64 w-full overflow-hidden rounded-3xl border border-white/10 shadow-2xl md:h-96">
            <div className="absolute inset-0 z-10 bg-gradient-to-t from-black/50 to-transparent" />
            <img
              src={articleImage}
              alt={article.title}
              className="h-full w-full object-cover"
            />
          </div>
        ) : null}

        <div className="rounded-3xl border border-white/5 bg-white/[0.02] p-6 backdrop-blur-sm md:p-12">
          <div
            className="prose prose-invert prose-purple max-w-none
                        prose-headings:font-bold prose-headings:text-slate-100
                        prose-p:text-slate-300 prose-p:leading-relaxed
                        prose-a:text-purple-400 hover:prose-a:text-purple-300
                        prose-code:text-pink-300 prose-code:bg-pink-500/10 prose-code:px-1.5 prose-code:py-0.5 prose-code:rounded
                        prose-pre:bg-slate-900/80 prose-pre:border prose-pre:border-white/10
                        prose-blockquote:bg-slate-900/80
                        prose-img:rounded-2xl prose-img:border prose-img:border-white/10"
          >
            <ReactMarkdown remarkPlugins={[remarkGfm]}>{article.content || '暂无内容'}</ReactMarkdown>
          </div>
        </div>

        <div className="mt-12 grid grid-cols-1 gap-4 md:grid-cols-2">
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
      </motion.article>
    </div>
  );
}
