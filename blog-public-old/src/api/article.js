/*
 * 功能：前端模块逻辑。
 */
import { toRelativeMediaUrl } from '../utils/media';

export const normalizeArticle = (article) => {
    if (!article) {
        return article;
    }

    const normalizedCover = toRelativeMediaUrl(article.coverImage ?? article.cover ?? '');

    return {
        ...article,
        cover: normalizedCover,
        coverImage: normalizedCover,
        tags: Array.isArray(article.tags) ? article.tags : [],
        category: article.category ?? null,
        summary: article.summary ?? '',
        viewCount: article.viewCount ?? 0,
    };
};

export const normalizeArticlePage = (payload) => {
    const page = payload?.data ?? payload ?? { items: [], total: 0 };
    return {
        ...page,
        items: Array.isArray(page.items) ? page.items.map(normalizeArticle) : [],
        total: page.total ?? 0,
    };
};

