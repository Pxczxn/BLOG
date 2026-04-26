import { useEffect, useMemo, useState } from 'react';
import { motion } from 'motion/react';
import { FileText, Filter, X } from 'lucide-react';
import ArticleCard, { type Article } from '../components/ArticleCard';
import EmptyState from '../components/EmptyState';
import request, { getStaticUrl } from '../lib/request';

type CategoryOption = {
  id?: number | string;
  name: string;
  slug?: string;
  articleCount?: number;
  count?: number;
};

type TagOption = {
  id?: number | string;
  name: string;
  slug?: string;
  articleCount?: number;
  count?: number;
};

export default function Blog() {
  const [articles, setArticles] = useState<Article[]>([]);
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [tags, setTags] = useState<TagOption[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedTag, setSelectedTag] = useState<string>('all');
  const [loading, setLoading] = useState(true);
  const [metaLoading, setMetaLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const selectedCategoryLabel = useMemo(() => {
    if (selectedCategory === 'all') return '全部分类';
    return categories.find((item) => item.slug === selectedCategory || String(item.id) === selectedCategory)?.name ?? '已选分类';
  }, [categories, selectedCategory]);

  const selectedTagLabel = useMemo(() => {
    if (selectedTag === 'all') return '全部标签';
    return tags.find((item) => item.slug === selectedTag || String(item.id) === selectedTag)?.name ?? '已选标签';
  }, [tags, selectedTag]);

  const tagFilter = useMemo(() => {
    const mapped = tags.map((item) => ({
      key: item.slug || String(item.id),
      label: item.name,
      count: item.articleCount ?? item.count,
    }));
    const visible = mapped.slice(0, 8);
    const selected = mapped.find((item) => item.key === selectedTag);
    const items = selected && selectedTag !== 'all' && !visible.some((item) => item.key === selectedTag)
      ? [...visible, selected]
      : visible;

    return {
      items: [{ key: 'all', label: '全部标签', count: undefined }, ...items],
      hiddenCount: Math.max(mapped.length - visible.length, 0),
    };
  }, [selectedTag, tags]);

  useEffect(() => {
    const fetchMeta = async () => {
      try {
        const [categoriesRes, tagsRes] = await Promise.all([
          request.get('/api/public/categories'),
          request.get('/api/public/tags'),
        ]);

        const categoriesData = Array.isArray(categoriesRes?.data ?? categoriesRes)
          ? (categoriesRes?.data ?? categoriesRes)
          : [];
        const tagsData = Array.isArray(tagsRes?.data ?? tagsRes) ? (tagsRes?.data ?? tagsRes) : [];

        setCategories(categoriesData);
        setTags(tagsData);
      } catch (metaError) {
        console.error('Failed to fetch blog filters:', metaError);
        setCategories([]);
        setTags([]);
      } finally {
        setMetaLoading(false);
      }
    };

    fetchMeta();
  }, []);

  useEffect(() => {
    const fetchArticles = async () => {
      try {
        setLoading(true);
        setError(null);

        const params: Record<string, string | number> = {
          page: 1,
          size: 12,
        };
        if (selectedCategory !== 'all') {
          params.categoryId = selectedCategory;
        }
        if (selectedTag !== 'all') {
          params.tagId = selectedTag;
        }

        const resp = await request.get('/api/public/articles', { params });
        const result: any = resp;
        const pageData = result?.data ?? result ?? { items: [] };
        const items = Array.isArray(pageData.items) ? pageData.items : [];

        const normalized: Article[] = items.map((item: any) => {
          const categoryName = item.category?.name ? [item.category.name] : [];
          const tagNames = Array.isArray(item.tags)
            ? item.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')).filter(Boolean)
            : [];

          return {
            id: item.slug || String(item.id),
            title: item.title || '无标题',
            summary: item.summary || '',
            coverImage: getStaticUrl(item.coverImage || ''),
            tags: [...categoryName, ...tagNames],
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
      } catch (blogError) {
        console.error('Failed to fetch blog articles:', blogError);
        setArticles([]);
        setError('文章加载失败，请稍后重试。');
      } finally {
        setLoading(false);
      }
    };

    fetchArticles();
  }, [selectedCategory, selectedTag]);

  const clearFilters = () => {
    setSelectedCategory('all');
    setSelectedTag('all');
  };

  const hasActiveFilters = selectedCategory !== 'all' || selectedTag !== 'all';

  return (
    <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <section className="relative z-10 mb-6 overflow-hidden rounded-[2rem] border border-white/10 bg-white/5 px-6 py-7 text-center backdrop-blur-xl">
        <p className="mb-2 text-xs uppercase tracking-[0.32em] text-purple-400/80">Blog</p>
        <h1 className="text-3xl font-black text-white drop-shadow-[0_0_18px_rgba(168,85,247,0.35)] md:text-4xl">
          博客文章
        </h1>
        <p className="mx-auto mt-3 max-w-2xl text-sm text-slate-400 md:text-base">
          在这里按照分类和标签一起筛选文章，快速找到你最想读的内容。
        </p>
      </section>

      <main className="relative z-10 grid grid-cols-1 gap-6 lg:grid-cols-[320px_minmax(0,1fr)]">
        <aside className="h-fit rounded-3xl border border-white/10 bg-white/5 p-5 backdrop-blur-xl lg:sticky lg:top-6">
          <div className="flex h-[30px] items-center justify-between gap-4">
            <div className="flex items-center gap-2">
              <Filter className="h-4 w-4 text-purple-400" />
              <h2 className="text-sm font-semibold uppercase tracking-wider text-white">筛选文章</h2>
            </div>
            {(selectedCategory !== 'all' || selectedTag !== 'all') && (
              <button
                type="button"
                onClick={clearFilters}
                className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-slate-300 transition-colors hover:border-white/20 hover:text-white"
              >
                <X className="h-3.5 w-3.5" />
                清空筛选
              </button>
            )}
          </div>

          {metaLoading && <p className="mt-2 text-xs text-slate-500">加载筛选项中...</p>}

          <FilterRow
            title="分类"
            items={[
              { key: 'all', label: '全部分类', count: undefined },
              ...categories.map((item) => ({
                key: item.slug || String(item.id),
                label: item.name,
                count: item.articleCount ?? item.count,
              })),
            ]}
            selectedKey={selectedCategory}
            onChange={setSelectedCategory}
          />

          <FilterRow
            title="标签"
            items={tagFilter.items}
            selectedKey={selectedTag}
            onChange={setSelectedTag}
          />
          {tagFilter.hiddenCount > 0 && (
            <p className="mt-3 text-xs text-slate-500">
              还有 {tagFilter.hiddenCount} 个标签，文章多了再展开。
            </p>
          )}

          <div className="mt-4 flex flex-wrap items-center gap-2 text-xs text-slate-500">
            <span>当前筛选：</span>
            <span className="rounded-full border border-white/10 bg-black/20 px-3 py-1 text-slate-300">
              {selectedCategoryLabel}
            </span>
            <span className="rounded-full border border-white/10 bg-black/20 px-3 py-1 text-slate-300">
              {selectedTagLabel}
            </span>
          </div>
        </aside>

        <div className="flex min-w-0 flex-col gap-4 overflow-hidden">
          <div className="mb-2 flex items-center justify-between">
            <h2 className="flex items-center gap-2 text-lg font-semibold text-white">
              <span className="h-5 w-1 rounded-full bg-purple-500" />
              最新文章
            </h2>
            {!loading && !error && (
              <span className="text-xs text-slate-500">
                共 {articles.length} 篇
              </span>
            )}
          </div>

          <div className="flex flex-col gap-4">
            {loading ? (
              <div className="rounded-2xl border border-white/5 bg-white/5 py-12 text-center">
                <p className="text-slate-400">正在筛选文章...</p>
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
                title={hasActiveFilters ? '还没有匹配的文章' : '文章正在整理中'}
                description={
                  hasActiveFilters
                    ? '换个分类或标签看看，后面内容多起来这里会更有用。'
                    : '这里会慢慢积累技术笔记、项目记录和复盘文章。'
                }
                actions={hasActiveFilters ? [] : [{ label: '先看资源页', to: '/resources' }]}
              />
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

function FilterRow({
  title,
  items,
  selectedKey,
  onChange,
}: {
  title: string;
  items: Array<{ key: string; label: string; count?: number }>;
  selectedKey: string;
  onChange: (key: string) => void;
}) {
  return (
    <div className="mt-6">
      <div className="mb-3 flex items-center gap-2 text-sm font-medium text-slate-300">
        <span className="h-4 w-1 rounded-full bg-purple-500" />
        {title}
      </div>
      <div className="flex flex-wrap gap-2">
        {items.map((item) => {
          const active = item.key === selectedKey;
          return (
            <button
              key={item.key}
              type="button"
              onClick={() => onChange(item.key)}
              className={[
                'inline-flex items-center gap-2 rounded-full border px-4 py-2 text-sm transition-all',
                active
                  ? 'border-purple-400/50 bg-purple-500/20 text-purple-200 shadow-[0_0_20px_rgba(168,85,247,0.15)]'
                  : 'border-white/10 bg-white/5 text-slate-400 hover:border-white/20 hover:text-white',
              ].join(' ')}
            >
              <span>{item.label}</span>
              {typeof item.count === 'number' && (
                <span className="rounded-full bg-black/20 px-2 py-0.5 text-[10px] text-slate-300">
                  {item.count}
                </span>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}
